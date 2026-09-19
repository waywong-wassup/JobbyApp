package com.jobapplicationapp.jobby.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.jobapplicationapp.jobby.data.JobApplication
import com.jobapplicationapp.jobby.data.User
import com.jobapplicationapp.jobby.ui.theme.AppTheme
import org.junit.Rule
import org.junit.Test


class JobApplicationDetailsTest {

    val testJob1 = JobApplication(
        jobApplicationId = "1",
        userId = "1",
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
        notes = null
    )

    val testJob2 = JobApplication(
        jobApplicationId = "2",
        userId = "1",
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
        notes = null
    )

    private fun createJobViewModel() = JobApplicationViewModel(DummyJobRepository()).apply { setUserId("1") }
    private fun createUserViewModel() = UserViewModel(DummyUserRepository()).apply { setUserId("1") }




    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun setupForm(
        viewModel: JobApplicationViewModel,
        jobTitleError: Boolean = false,
        companyNameError: Boolean = false
    ) {
        composeTestRule.setContent {
            AppTheme {
                JobApplicationDetailsForm(
                    jobTitleFocusRequester = remember { FocusRequester() },
                    companyNameFocusRequester = remember { FocusRequester() },
                    jobTitleError = jobTitleError,
                    companyNameError = companyNameError,
                    onJobTitleChange = {},
                    onCompanyNameChange = {},
                    jobApplicationViewModel = viewModel
                )
            }
        }
    }

    @Test
    fun jobApplicationDetailsScreen_onDeleteIconClick_showDeleteDialog() {
        val jobViewModel = createJobViewModel().apply { selectJob(testJob1) }
        val userViewModel = createUserViewModel()
        composeTestRule.setContent {
            AppTheme {
                JobApplicationDetailsScreen(
                    jobApplicationViewModel = jobViewModel,
                    userViewModel = userViewModel
                )
            }
        }
        composeTestRule.onNodeWithContentDescription("Delete").performClick()
        composeTestRule.onNodeWithText("Confirm Delete?").assertIsDisplayed()
    }

    @Test
    fun jobApplicationDetailsForm_allFieldsDisplayed() {
        val testJob = testJob1
        val viewModel = createJobViewModel()
        viewModel.selectJob(testJob)

        setupForm(viewModel)

        composeTestRule.onNodeWithText("Job Title").assertIsDisplayed()
        composeTestRule.onNodeWithText(testJob.jobTitle).assertIsDisplayed()
        composeTestRule.onNodeWithText("Company Name").assertIsDisplayed()
        composeTestRule.onNodeWithText(testJob.companyName).assertIsDisplayed()
        composeTestRule.onNodeWithText("Location").assertIsDisplayed()
        composeTestRule.onNodeWithText(testJob.location!!).assertIsDisplayed()
        composeTestRule.onNodeWithText("Application URL").assertIsDisplayed()
        composeTestRule.onNodeWithText("Progress:").assertIsDisplayed()
        composeTestRule.onNodeWithText("Salary").assertIsDisplayed()
        composeTestRule.onNodeWithText("100,000").assertIsDisplayed()
        composeTestRule.onNodeWithText("Posted Date").assertIsDisplayed()
        composeTestRule.onNodeWithText("Job Type").assertIsDisplayed()
        composeTestRule.onNodeWithText("Contact Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Contact Details").assertIsDisplayed()
        composeTestRule.onNodeWithText("Notes").performScrollTo()
                                                .assertIsDisplayed()
    }

    @Test
    fun jobApplicationDetailsForm_jobTitleError_showsError() {
        val viewModel = createJobViewModel()
        viewModel.selectJob(testJob1)
        setupForm(viewModel, jobTitleError = true)
        composeTestRule.onNodeWithTag("jobTitleError").assertIsDisplayed()
        composeTestRule.onNodeWithText("Required").assertIsDisplayed()
    }

    @Test
    fun jobApplicationDetailsForm_CompanyNameError_showsError() {
        val viewModel = createJobViewModel()
        viewModel.selectJob(testJob1)
        setupForm(viewModel, companyNameError = true)
        composeTestRule.onNodeWithTag("companyNameError").assertIsDisplayed()
        composeTestRule.onNodeWithText("Required").assertIsDisplayed()
    }

    @Test
    fun jobApplicationDetailsScreen_onBackClick_triggersCallback() {
        var backTriggered = false
        composeTestRule.setContent {
            AppTheme {
                JobApplicationDetailsScreen(
                    jobApplicationViewModel = createJobViewModel(),
                    userViewModel = createUserViewModel(),
                    onBackClick = { backTriggered = true }
                )
            }
        }
        composeTestRule.onNodeWithContentDescription("Back to Job applications").performClick()
        assert(backTriggered)
    }

    @Test
    fun jobApplicationDetailsScreen_onDiscardClick_triggersCallback() {
        var discardTriggered = false
        composeTestRule.setContent {
            AppTheme {
                JobApplicationDetailsScreen(
                    jobApplicationViewModel = createJobViewModel(),
                    userViewModel = createUserViewModel(),
                    onDiscardClick = { discardTriggered = true }
                )
            }
        }
        composeTestRule.onNodeWithText("Discard").performClick()
        assert(discardTriggered)
    }

    @Test
    fun jobApplicationDetailsScreen_onSaveClick_withValidInput_triggersCallback() {
        var saveTriggered = false
        val viewModel = createJobViewModel()
        viewModel.selectJob(testJob1)

        composeTestRule.setContent {
            AppTheme {
                JobApplicationDetailsScreen(
                    jobApplicationViewModel = viewModel,
                    userViewModel = createUserViewModel(),
                    onSaveClick = { saveTriggered = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Save").performClick()
        assert(saveTriggered)
    }

    @Test
    fun jobApplicationDetailsScreen_onSaveClick_withInvalidInput_showsError() {
        var saveTriggered = false
        val viewModel = createJobViewModel()
        viewModel.selectJob(testJob1.copy(jobTitle = ""))

        composeTestRule.setContent {
            AppTheme {
                JobApplicationDetailsScreen(
                    jobApplicationViewModel = viewModel,
                    userViewModel = createUserViewModel(),
                    onSaveClick = { saveTriggered = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Save").performClick()

        composeTestRule.onNodeWithText("Required").assertIsDisplayed()
        assert(!saveTriggered)
    }

    @Test
    fun jobApplicationDetailsScreen_onConfirmDelete_triggersCallback() {
        var confirmDeleteTriggered = false
        val jobViewModel = createJobViewModel().apply { selectJob(testJob1) }
        composeTestRule.setContent {
            AppTheme {
                JobApplicationDetailsScreen(
                    jobApplicationViewModel = jobViewModel,
                    userViewModel = createUserViewModel(),
                    onConfirmDelete = { confirmDeleteTriggered = true }
                )
            }
        }

        // Open dialog
        composeTestRule.onNodeWithContentDescription("Delete").performClick()
        composeTestRule.onNodeWithText("Delete").performClick()

        assert(confirmDeleteTriggered)
    }

    @Test
    fun jobApplicationDetailsForm_progressDropdownSelection_updatesValue() {
        val viewModel = createJobViewModel()
        viewModel.selectJob(testJob1)

        composeTestRule.setContent {
            AppTheme {
                JobApplicationDetailsForm(
                    jobTitleFocusRequester = remember { FocusRequester() },
                    companyNameFocusRequester = remember { FocusRequester() },
                    jobTitleError = false,
                    companyNameError = false,
                    onJobTitleChange = {},
                    onCompanyNameChange = {},
                    jobApplicationViewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Progress:").performScrollTo().performClick()

        composeTestRule.onNodeWithText("Interview").performClick()

        composeTestRule.onNodeWithText("Interview").assertIsDisplayed()
    }
}
