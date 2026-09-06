package com.jobapplicationapp.jobby.ui

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.jobapplicationapp.jobby.data.JobApplication
import com.jobapplicationapp.jobby.data.JobApplicationRepository
import com.jobapplicationapp.jobby.data.User
import com.jobapplicationapp.jobby.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import org.junit.Rule
import org.junit.Test

class JobbyAppNavHostTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    lateinit var navController: TestNavHostController

    private val testJob = JobApplication(
        jobApplicationId = 1,
        jobTitle = "Software Engineer",
        companyName = "Test Company",
        location = "Remote",
        progress = "Applied",
        applicationURL = null,
        contactName = null,
        contactDetails = null,
        jobType = null,
        applicationPostedDate = null,
        notes = null,
        salary = null
    )

    // Fake Repositories to avoid real database usage
    class FakeJobRepository(initialJobs: List<JobApplication> = emptyList()) : JobApplicationRepository {
        private val _jobs = MutableStateFlow(initialJobs)
        override fun getAllJobApplications() = _jobs.asStateFlow()
        override suspend fun addJobApplication(job: JobApplication) {
            _jobs.update { it + job.copy(jobApplicationId = it.size + 1) }
        }
        override suspend fun updateJobApplication(job: JobApplication) {
            _jobs.update { list -> list.map { if (it.jobApplicationId == job.jobApplicationId) job else it } }
        }
        override suspend fun deleteJobApplication(job: JobApplication) {
            _jobs.update { list -> list.filter { it.jobApplicationId != job.jobApplicationId } }
        }
        override fun getJobApplicationById(id: Int) = flowOf(_jobs.value.find { it.jobApplicationId == id })
    }

    class FakeUserRepository : UserRepository {
        override suspend fun deleteUser(user: User) {}
        override suspend fun addUser(user: User) {}
        override fun getCurrentUser(userId: Int) = flowOf(User.sampleUser)
    }

    private fun setupNavHost(
        jobRepository: JobApplicationRepository = FakeJobRepository(),
        userRepository: UserRepository = FakeUserRepository()
    ) {
        val jobViewModel = JobApplicationViewModel(jobRepository)
        val userViewModel = UserViewModel(userRepository, 1)

        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            JobbyAppNavHost(
                navController = navController,
                jobApplicationViewModel = jobViewModel,
                userViewModel = userViewModel
            )
        }
    }

    @Test
    fun jobbyAppNavHost_verifyStartDestination() {
        setupNavHost()
        composeTestRule.onNodeWithText("Jobby").assertIsDisplayed()
        val route = navController.currentBackStackEntry?.destination?.route
        assert(route?.contains("JobApplicationListScreenRoute") == true)
    }

    @Test
    fun jobbyAppNavHost_clickAdd_navigatesToDetails() {
        setupNavHost()
        composeTestRule.onNodeWithContentDescription("Add Job Application").performClick()
        val route = navController.currentBackStackEntry?.destination?.route
        assert(route?.contains("JobApplicationDetailsScreenRoute") == true)
        composeTestRule.onNodeWithText("Job Application Details").assertIsDisplayed()
    }

    @Test
    fun jobbyAppNavHost_clickJobItem_navigatesToDetailsWithCorrectData() {
        // Setup with one job
        setupNavHost(jobRepository = FakeJobRepository(listOf(testJob)))

        // Click the job title
        composeTestRule.onNodeWithText(testJob.jobTitle).performClick()

        // Verify navigation and data display
        val route = navController.currentBackStackEntry?.destination?.route
        assert(route?.contains("JobApplicationDetailsScreenRoute") == true)
        composeTestRule.onNodeWithText(testJob.jobTitle).assertIsDisplayed()
        composeTestRule.onNodeWithText(testJob.companyName).assertIsDisplayed()
    }

    @Test
    fun jobbyAppNavHost_clickBackInDetails_navigatesToList() {
        setupNavHost()
        composeTestRule.onNodeWithContentDescription("Add Job Application").performClick()
        composeTestRule.onNodeWithContentDescription("Back to Job applications").performClick()
        val route = navController.currentBackStackEntry?.destination?.route
        assert(route?.contains("JobApplicationListScreenRoute") == true)
    }

    @Test
    fun jobbyAppNavHost_clickSaveInDetails_navigatesToList() {
        setupNavHost()
        composeTestRule.onNodeWithContentDescription("Add Job Application").performClick()

        composeTestRule.onNodeWithText("Job Title").performTextInput("New Job")
        composeTestRule.onNodeWithText("Company Name").performTextInput("New Company")

        composeTestRule.onNodeWithText("Save").performClick()

        val route = navController.currentBackStackEntry?.destination?.route
        assert(route?.contains("JobApplicationListScreenRoute") == true)
    }

    @Test
    fun jobbyAppNavHost_clickDeleteInDetails_navigatesToList() {
        setupNavHost(jobRepository = FakeJobRepository(listOf(testJob)))

        // Navigate to existing job
        composeTestRule.onNodeWithText(testJob.jobTitle).performClick()

        // Open delete dialog
        composeTestRule.onNodeWithContentDescription("Delete").performClick()
        // Confirm delete
        composeTestRule.onNodeWithText("Delete").performClick()

        // Verify we are back on the list and the job is gone
        val route = navController.currentBackStackEntry?.destination?.route
        assert(route?.contains("JobApplicationListScreenRoute") == true)
        composeTestRule.onNodeWithText(testJob.jobTitle).assertDoesNotExist()
    }
}
