package com.jobapplicationapp.jobby.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.jobapplicationapp.jobby.data.JobApplication
import com.jobapplicationapp.jobby.data.User
import com.jobapplicationapp.jobby.ui.theme.AppTheme
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



}
