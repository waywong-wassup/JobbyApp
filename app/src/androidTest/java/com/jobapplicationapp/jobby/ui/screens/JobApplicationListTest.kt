package com.jobapplicationapp.jobby.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.jobapplicationapp.jobby.data.FakeJobApplicationRepository
import com.jobapplicationapp.jobby.data.model.JobApplication
import com.jobapplicationapp.jobby.data.model.SortOption
import com.jobapplicationapp.jobby.data.model.User
import com.jobapplicationapp.jobby.data.repository.UserPreferencesRepository
import com.jobapplicationapp.jobby.data.repository.UserRepository
import com.jobapplicationapp.jobby.ui.theme.AppTheme
import com.jobapplicationapp.jobby.viewmodel.JobApplicationViewModel
import com.jobapplicationapp.jobby.viewmodel.UserViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

class JobApplicationListTest {

    val testJob1 = JobApplication(
        jobApplicationId = "1",
        jobTitle = "Android Developer",
        companyName = "Tech Corp",
        location = "Auckland",
        salary = 100000,
        progress = "Applied",
        applicationURL = null,
        contactName = null,
        contactDetails = null,
        jobType = null,
        applicationPostedDate = null,
        notes = null,
        userId = "1"
    )

    val testJob2 = JobApplication(
    jobApplicationId = "2",
    jobTitle = "iOS Developer",
    companyName = "App Studio",
    location = "Remote",
    salary = 120000,
    progress = "Interview",
    applicationURL = null,
    contactName = null,
    contactDetails = null,
    jobType = null,
    applicationPostedDate = null,
    notes = null,
    userId = "1"
    )

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun jobApplicationList_displaysItems() {
        val testJobs = listOf(testJob1,testJob2)

        composeTestRule.setContent {
            AppTheme {
                JobApplicationList(
                    jobApplications = testJobs,
                    onEditClick = {}
                )
            }
        }

        // Verify both jobs are displayed
        composeTestRule.onNodeWithText("Android Developer").assertIsDisplayed()
        composeTestRule.onNodeWithText("iOS Developer").assertIsDisplayed()
        
        // Verify company names are displayed
        composeTestRule.onNodeWithText("Tech Corp").assertIsDisplayed()
        composeTestRule.onNodeWithText("App Studio").assertIsDisplayed()
    }
    @Test
    fun jobApplicationList_emptyList_isEmpty() {
        composeTestRule.setContent {
            AppTheme {
                JobApplicationList(jobApplications = emptyList(), onEditClick = {})
            }
        }
        composeTestRule.onNodeWithTag("JobCard").assertDoesNotExist()
    }

    @Test
    fun jobApplicationList_onClickOfEdit_triggerOnEditClick() {
        var capturedId = "-1"
        val testJobs = listOf(testJob1)
        composeTestRule.setContent {
            AppTheme() {
                JobApplicationList(
                    jobApplications = testJobs,
                    onEditClick = { id -> capturedId = id}
                )
            }
        }

        composeTestRule.onNodeWithText("Android Developer").performClick()
        assert(capturedId == "1")
    }

    @Test
    fun jobbyTopBar_displaysAppIconAndName() {
        composeTestRule.setContent {
            AppTheme {
                JobbyTopBar(user = User.sampleUser)
            }
        }
        composeTestRule.onNodeWithText("Jobby").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("JobbyApp").assertIsDisplayed()
    }

    @Test
    fun jobbyTopBar_displaysUserName() {
        composeTestRule.setContent {
            AppTheme {
                JobbyTopBar(user = User.sampleUser)
            }
        }
        composeTestRule.onNodeWithText("${User.sampleUser.firstName} ${User.sampleUser.lastName}").assertIsDisplayed()
    }

    @Test
    fun jobApplicationCard_WithLocation_showsLocationIcon() {
        val remoteJob = testJob1

        composeTestRule.setContent {
            AppTheme {
                JobApplicationCard(jobApplication = remoteJob)
            }
        }
        composeTestRule.onNodeWithContentDescription("Location Icon").assertIsDisplayed()
    }

    @Test
    fun jobApplicationCard_WithNoLocation_LocationIconNotShown() {
        val remoteJob = testJob1.copy(location = "")

        composeTestRule.setContent {
            AppTheme {
                JobApplicationCard(jobApplication = remoteJob)
            }
        }
        composeTestRule.onNodeWithContentDescription("Location Icon").assertDoesNotExist()
    }

    @Test
    fun jobApplicationCard_remoteLocation_showsHomeIcon() {
        val remoteJob = testJob2

        composeTestRule.setContent {
            AppTheme {
                JobApplicationCard(jobApplication = remoteJob)
            }
        }
        composeTestRule.onNodeWithContentDescription("Remote Icon").assertIsDisplayed()
    }

    @Test
    fun jobApplicationListScreen_onClickAddFAB_triggersAddClicked() {
        var addButtonClicked = false
        composeTestRule.setContent {
            AppTheme {
                // We test the full screen here to include the FAB
                JobApplicationListScreen(
                    onAddClick = { addButtonClicked = true }
                )
            }
        }
        composeTestRule.onNodeWithContentDescription("Add Job Application").performClick()
        assert(addButtonClicked)
    }

    @Test
    fun jobApplicationCard_veryLongTitle_stillDisplays() {
        val longTitleJob = testJob1.copy(jobTitle = "A".repeat(100))
        composeTestRule.setContent {
            AppTheme {
                JobApplicationCard(jobApplication = longTitleJob)
            }
        }
        composeTestRule.onNodeWithText("A".repeat(100), substring = true).assertIsDisplayed()
    }

    @Test
    fun sortDropDownButton_click_opensDropdownMenuWithSortOptions() {
        composeTestRule.setContent {
            AppTheme {
                SortDropDownButton(
                    selectedSortOption = SortOption.LAST_MODIFIED_DESC,
                    onSortOptionsSelected = {}
                )
            }
        }

        // Open menu
        composeTestRule.onNodeWithContentDescription("Sort Applications").performClick()

        // Assert sort options are displayed
        composeTestRule.onNodeWithText("Last Modified: Newest").assertIsDisplayed()
        composeTestRule.onNodeWithText("Last Modified: Oldest").assertIsDisplayed()
        composeTestRule.onNodeWithText("Date Applied: Newest").assertIsDisplayed()
        composeTestRule.onNodeWithText("Date Applied: Oldest").assertIsDisplayed()
        composeTestRule.onNodeWithText("Job Title A-Z").assertIsDisplayed()
        composeTestRule.onNodeWithText("Salary (High to Low)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Progress").assertIsDisplayed()
    }

    @Test
    fun sortDropDownButton_selectOption_triggersCallback() {
        var selectedOption: SortOption? = null
        composeTestRule.setContent {
            AppTheme {
                SortDropDownButton(
                    selectedSortOption = SortOption.LAST_MODIFIED_DESC,
                    onSortOptionsSelected = { selectedOption = it }
                )
            }
        }

        // Open menu and select Job Title A-Z
        composeTestRule.onNodeWithContentDescription("Sort Applications").performClick()
        composeTestRule.onNodeWithText("Job Title A-Z").performClick()

        assert(selectedOption == SortOption.JOB_TITLE_ASC)
    }

    @Test
    fun jobApplicationListScreen_sortingOptionChange_updatesUi() {
        val zebraJob = testJob1.copy(jobApplicationId = "101", jobTitle = "Zebra Specialist", salary = 50000)
        val appleJob = testJob2.copy(jobApplicationId = "102", jobTitle = "Apple Developer", salary = 150000)

        val fakeRepo = FakeJobApplicationRepository().apply {
            runBlocking {
                addJobApplication(zebraJob)
                addJobApplication(appleJob)
            }
        }

        val testUserRepo = object : UserRepository {
            override suspend fun deleteUser(user: User) {}
            override suspend fun addUser(user: User) {}
            override fun getCurrentUser(userId: String) = flowOf(User.sampleUser)
        }

        val testUserPrefRepo = object : UserPreferencesRepository {
            override val isSyncEnabled: Flow<Boolean> = flowOf(false)
            override suspend fun setIsSyncEnabled(isSyncEnabled: Boolean) {}
        }

        val viewModel = JobApplicationViewModel(fakeRepo).apply { setUserId("1") }
        val userViewModel = UserViewModel(testUserRepo, fakeRepo, testUserPrefRepo).apply { setUserId("1") }

        composeTestRule.setContent {
            AppTheme {
                JobApplicationListScreen(
                    jobApplicationViewModel = viewModel,
                    userViewModel = userViewModel
                )
            }
        }

        // Initially both items exist
        composeTestRule.onNodeWithText("Zebra Specialist").assertIsDisplayed()
        composeTestRule.onNodeWithText("Apple Developer").assertIsDisplayed()

        // Open sort menu and select Job Title A-Z
        composeTestRule.onNodeWithContentDescription("Sort Applications").performClick()
        composeTestRule.onNodeWithText("Job Title A-Z").performClick()

        // Verify items remain displayed correctly after sort selection
        composeTestRule.onNodeWithText("Apple Developer").assertIsDisplayed()
        composeTestRule.onNodeWithText("Zebra Specialist").assertIsDisplayed()
    }
}
