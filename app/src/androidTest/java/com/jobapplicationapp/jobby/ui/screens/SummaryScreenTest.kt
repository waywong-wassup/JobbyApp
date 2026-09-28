package com.jobapplicationapp.jobby.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.jobapplicationapp.jobby.data.model.JobApplication
import com.jobapplicationapp.jobby.data.model.Progress
import com.jobapplicationapp.jobby.data.repository.JobApplicationRepository
import com.jobapplicationapp.jobby.ui.theme.AppTheme
import com.jobapplicationapp.jobby.viewmodel.JobApplicationViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class SummaryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    class TestJobRepository(initialJobs: List<JobApplication> = emptyList()) : JobApplicationRepository {
        val jobsFlow = MutableStateFlow(initialJobs)
        var shouldThrowError = false

        override fun getAllJobApplications(userId: String): Flow<List<JobApplication>> = flow {
            if (shouldThrowError) {
                throw Exception("Error loading summary")
            }
            jobsFlow.collect { emit(it) }
        }

        override suspend fun addJobApplication(jobApplication: JobApplication) {
            jobsFlow.value = jobsFlow.value + jobApplication
        }

        override suspend fun updateJobApplication(jobApplication: JobApplication) {
            jobsFlow.value = jobsFlow.value.map { if (it.jobApplicationId == jobApplication.jobApplicationId) jobApplication else it }
        }

        override suspend fun deleteJobApplication(jobApplication: JobApplication) {
            jobsFlow.value = jobsFlow.value.filterNot { it.jobApplicationId == jobApplication.jobApplicationId }
        }

        override fun getJobApplicationById(id: String): Flow<JobApplication?> {
            return flowOf(jobsFlow.value.find { it.jobApplicationId == id })
        }

        override fun getUnsyncedJobApplications(userId: String): Flow<List<JobApplication>> {
            return flowOf(emptyList())
        }
    }

    private val testJobApplied = JobApplication(
        jobApplicationId = "1",
        userId = "1",
        jobTitle = "Android Developer",
        companyName = "Tech Corp",
        location = "Auckland",
        salary = 100000,
        progress = Progress.APPLIED.progressPhase,
        applicationURL = null,
        contactName = null,
        contactDetails = null,
        jobType = null,
        applicationPostedDate = null,
        notes = null
    )

    private val testJobInterview = JobApplication(
        jobApplicationId = "2",
        userId = "1",
        jobTitle = "iOS Developer",
        companyName = "App Studio",
        location = "Remote",
        salary = 120000,
        progress = Progress.INTERVIEW.progressPhase,
        applicationURL = null,
        contactName = null,
        contactDetails = null,
        jobType = null,
        applicationPostedDate = null,
        notes = null
    )

    private val testJobOffer = JobApplication(
        jobApplicationId = "3",
        userId = "1",
        jobTitle = "Backend Dev",
        companyName = "Cloud Inc",
        location = "Wellington",
        salary = 140000,
        progress = Progress.OFFERED.progressPhase,
        applicationURL = null,
        contactName = null,
        contactDetails = null,
        jobType = null,
        applicationPostedDate = null,
        notes = null
    )

    private val testJobRejected = JobApplication(
        jobApplicationId = "4",
        userId = "1",
        jobTitle = "Frontend Dev",
        companyName = "Web Co",
        location = "Remote",
        salary = null,
        progress = Progress.REJECTED.progressPhase,
        applicationURL = null,
        contactName = null,
        contactDetails = null,
        jobType = null,
        applicationPostedDate = null,
        notes = null
    )

    private fun createViewModel(repo: TestJobRepository): JobApplicationViewModel {
        return JobApplicationViewModel(repo).apply { setUserId("1") }
    }

    @Test
    fun summaryScreen_displaysTopAppBarTitleAndNavigationIcon() {
        val repo = TestJobRepository()
        val viewModel = createViewModel(repo)

        composeTestRule.setContent {
            AppTheme {
                SummaryScreen(
                    jobApplicationViewModel = viewModel,
                    onBackClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Job Search Summary").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Back to Applications").assertIsDisplayed()
    }

    @Test
    fun summaryScreen_backButtonClick_triggersOnBackClick() {
        var backClicked = false
        val repo = TestJobRepository()
        val viewModel = createViewModel(repo)

        composeTestRule.setContent {
            AppTheme {
                SummaryScreen(
                    jobApplicationViewModel = viewModel,
                    onBackClick = { backClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Back to Applications").performClick()
        assert(backClicked)
    }

    @Test
    fun summaryScreen_emptyJobs_displaysEmptyState() {
        val repo = TestJobRepository(emptyList())
        val viewModel = createViewModel(repo)

        composeTestRule.setContent {
            AppTheme {
                SummaryScreen(
                    jobApplicationViewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("No Applications Yet").assertIsDisplayed()
        composeTestRule.onNodeWithText(
            "Add job applications to track your metrics, callback rates, and interview progress!"
        ).assertIsDisplayed()
    }

    @Test
    fun summaryScreen_withJobs_displaysMetricCardsAndBreakdown() {
        val jobs = listOf(testJobApplied, testJobInterview, testJobOffer, testJobRejected)
        val repo = TestJobRepository(jobs)
        val viewModel = createViewModel(repo)

        composeTestRule.setContent {
            AppTheme {
                SummaryScreen(
                    jobApplicationViewModel = viewModel
                )
            }
        }

        // Metrics Headers
        composeTestRule.onNodeWithText("Total Applications").assertIsDisplayed()
        composeTestRule.onNodeWithText("Interviews").assertIsDisplayed()
        composeTestRule.onNodeWithText("Offers").assertIsDisplayed()
        composeTestRule.onNodeWithText("Callback Rate").assertIsDisplayed()

        // Total = 4
        composeTestRule.onNodeWithText("4").assertIsDisplayed()

        // Scroll down if needed
        composeTestRule.onNodeWithText("Rejected").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Ghosted").performScrollTo().assertIsDisplayed()

        // Application Breakdown Card
        composeTestRule.onNodeWithText("Application Breakdown").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun summaryScreen_withSalaries_displaysSalaryInsights() {
        val jobs = listOf(testJobApplied, testJobInterview, testJobOffer)
        val repo = TestJobRepository(jobs)
        val viewModel = createViewModel(repo)

        composeTestRule.setContent {
            AppTheme {
                SummaryScreen(
                    jobApplicationViewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Salary Insights").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Average Salary Target").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Highest Listing").performScrollTo().assertIsDisplayed()

        // Salaries: 100k, 120k, 140k -> avg = 120k, max = 140k
        composeTestRule.onNodeWithText("$120,000").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("$140,000").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun summaryScreen_withoutSalaries_salaryInsightsNotDisplayed() {
        val jobNoSalary = testJobApplied.copy(salary = null)
        val repo = TestJobRepository(listOf(jobNoSalary))
        val viewModel = createViewModel(repo)

        composeTestRule.setContent {
            AppTheme {
                SummaryScreen(
                    jobApplicationViewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Total Applications").assertIsDisplayed()
        composeTestRule.onNodeWithText("Salary Insights").assertDoesNotExist()
    }

    @Test
    fun summaryScreen_errorState_displaysErrorMessage() {
        val repo = TestJobRepository()
        repo.shouldThrowError = true
        val viewModel = createViewModel(repo)

        composeTestRule.setContent {
            AppTheme {
                SummaryScreen(
                    jobApplicationViewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Error loading summary").assertIsDisplayed()
    }

    @Test
    fun salaryInsightsCard_calculatesAverageAndHighestCorrectly() {
        val salaries = listOf(100000L, 120000L, 150000L)

        composeTestRule.setContent {
            AppTheme {
                SalaryInsightsCard(salaries = salaries)
            }
        }

        composeTestRule.onNodeWithText("Salary Insights").assertIsDisplayed()
        // avg of (100k, 120k, 150k) = 123,333
        composeTestRule.onNodeWithText("$123,333").assertIsDisplayed()
        // max = 150,000
        composeTestRule.onNodeWithText("$150,000").assertIsDisplayed()
    }
}
