package com.jobapplicationapp.jobby.ui

import app.cash.turbine.test
import com.jobapplicationapp.jobby.data.FakeJobApplicationRepository
import com.jobapplicationapp.jobby.data.FakeUserRepository
import com.jobapplicationapp.jobby.data.JobApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.test.runTest
import org.junit.Before

@OptIn(ExperimentalCoroutinesApi::class)
class JobApplicationViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    val testDataJob1 = JobApplication(
        jobApplicationId = 1,
        jobTitle = "Android Developer",
        companyName = "HSBC",
        location = "Auckland",
        salary = 100000,
        applicationURL = "www.hireme.com",
        progress = "APPLIED",
        contactName = "Ashley",
        contactDetails = "AshYouShallReceive@gmail.com",
        jobType = "Full Time",
        applicationPostedDate = "",
        notes = ""
    )

    private lateinit var fakeJobRepository: FakeJobApplicationRepository
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var viewModel: JobApplicationViewModel

    @Before
    fun setup() {
        fakeJobRepository = FakeJobApplicationRepository()
        fakeUserRepository = FakeUserRepository()
        viewModel = JobApplicationViewModel(
            fakeJobRepository,
            fakeUserRepository
        )
    }

    @Test
    fun uiState_onRepositoryError_returnsError() = runTest {
        fakeJobRepository.shouldThrowError = true
        val errorViewModel = JobApplicationViewModel(fakeJobRepository, fakeUserRepository)
        errorViewModel.uiState.test {
            val finalState = expectMostRecentItem()
            assertEquals(JobApplicationUiState.Error, finalState)
        }
    }


    @Test
    fun saveJobApplicationChange_newJobWithChanges_addsToRepository() = runTest {
        viewModel.loadJobApplication(0)
        viewModel.updateJobDetailFieldsUiStates { copy(jobTitle = "Android Developer", companyName = "Mcdonald") }

        viewModel.saveJobApplicationChange()
        fakeJobRepository.getAllJobApplications().test {
            val jobsList = awaitItem()
            assertTrue(jobsList.any { it.jobTitle == "Android Developer" })
            assertTrue(jobsList.any { it.companyName == "Mcdonald" })
        }
    }

    @Test
    fun saveJobApplicationChange_ExistingJob_addsToRepository() = runTest {
        fakeJobRepository.addJobApplication(testDataJob1)
        viewModel.loadJobApplication(1)
        viewModel.updateJobDetailFieldsUiStates { copy(jobTitle = "iOS Developer", companyName = "KFC") }
        viewModel.saveJobApplicationChange()
        fakeJobRepository.getAllJobApplications().test {
            val jobsList = awaitItem()
            assertTrue(jobsList.any { it.jobTitle == "iOS Developer" })
            assertTrue(jobsList.any { it.companyName == "KFC" })
        }
    }

    @Test
    fun saveJobApplicationChange_newJobNoChange_DoesNotSaveToRepository() = runTest  {
        viewModel.loadJobApplication(0)
        viewModel.saveJobApplicationChange() // it should fail to save
        fakeJobRepository.getAllJobApplications().test {
            val jobsList = awaitItem()
            //invalid input, should not save, hence jobsList should still be empty
            assertTrue(jobsList.isEmpty())
        }
    }

    @Test
    fun selectJob_setsChangingJobApplication() = runTest{
        fakeJobRepository.addJobApplication(testDataJob1)
        viewModel.selectJob(testDataJob1)
        val result = viewModel.changingJobApplication.value
        assertEquals(testDataJob1, result)
    }

     @Test
     fun deleteJobApplication_deletesFromRepository() = runTest {
         fakeJobRepository.addJobApplication(testDataJob1)
         viewModel.loadJobApplication(1)
         viewModel.deleteJobApplication(testDataJob1)
         fakeJobRepository.getAllJobApplications().test {
             val jobsList = awaitItem()
             assertTrue(jobsList.isEmpty())
         }
     }


    //id = 0, verify files are null when add new job
    // id = 1, then output will be the full value of application
    // id = -1, well, crashes i guess lol
    @Test
    fun loadJobApplication_zeroId_returnsNewJob() {
        viewModel.loadJobApplication(0)
        val result = viewModel.changingJobApplication.value
        assertEquals(0,result?.jobApplicationId)
        assertEquals("",result?.jobTitle)
        assertEquals("",result?.companyName)
        assertEquals("Applied",result?.progress)
        assertNull(result?.salary)
        assertNull(result?.applicationURL)
        assertNull(result?.contactName)
        assertNull(result?.contactDetails)
        assertNull(result?.jobType)
        assertNull(result?.applicationPostedDate)
        assertNull(result?.notes)
    }

    @Test
    fun loadJobApplication_validId_returnsExistingJob() {
        viewModel.addJobApplication(testDataJob1)
        viewModel.loadJobApplication(1)
        val result = viewModel.changingJobApplication.value
        assertEquals(1,result?.jobApplicationId)
        assertEquals("Android Developer",result?.jobTitle)
        assertEquals("HSBC",result?.companyName)
        assertEquals("Auckland",result?.location)
        assertEquals("100000".toLongOrNull(),result?.salary)
        assertEquals("www.hireme.com",result?.applicationURL)
        assertEquals("APPLIED",result?.progress)
        assertEquals("Ashley",result?.contactName)
        assertEquals("AshYouShallReceive@gmail.com",result?.contactDetails)
        assertEquals("Full Time",result?.jobType)
        assertEquals("",result?.applicationPostedDate)
        assertEquals("",result?.notes)
    }

    @Test
    fun loadJobApplication_inValidNegativeId_returnsInvalidJobWithNull() {
        viewModel.loadJobApplication(100)
        val result = viewModel.changingJobApplication.value
        assertNull(result)
    }



    @Test
    fun validateInput_EmptyJobTitle_returnsValidationError_JobTitleRequired() {
        //create a new job, id = 0
        viewModel.loadJobApplication(0)
        //fill in with empty JobTitle
        viewModel.updateJobDetailFieldsUiStates { copy(jobTitle = "", companyName = "Meta") }

        val result = viewModel.validateInput()

        assertEquals(JobApplicationViewModel.ValidationError.JOB_TITLE_REQUIRED, result)
    }

    @Test
    fun validateInput_EmptyCompanyName_returnsValidationError_CompanyNameRequired() {
        //create a new job, id = 0
        viewModel.loadJobApplication(0)
        viewModel.updateJobDetailFieldsUiStates { copy(jobTitle = "Developer", companyName = "") }

        val result = viewModel.validateInput()

        assertEquals(JobApplicationViewModel.ValidationError.COMPANY_NAME_REQUIRED, result)
    }

    @Test
    fun validateInput_EmptyJobTitleEmptyCompanyName_returnsValidationError_JobTitleRequired() {
        //create a new job, id = 0
        viewModel.loadJobApplication(0)
        viewModel.updateJobDetailFieldsUiStates { copy(jobTitle = "", companyName = "") }

        val result = viewModel.validateInput()

        assertEquals(JobApplicationViewModel.ValidationError.JOB_TITLE_REQUIRED, result)
    }

    @Test
    fun validateInput_validJobTitleValidCompanyName_returnsValidationError_NONE() {
        //create a new job, id = 0
        viewModel.loadJobApplication(0)
        viewModel.updateJobDetailFieldsUiStates { copy(jobTitle = "Janitor", companyName = "HSBC") }

        val result = viewModel.validateInput()

        assertEquals(JobApplicationViewModel.ValidationError.NONE, result)
    }

    @Test
    fun validateInput_noChanges_returnsValidationError_NONE() {
        viewModel.addJobApplication(testDataJob1)
        viewModel.loadJobApplication(1)
        val result = viewModel.validateInput()

        assertEquals(JobApplicationViewModel.ValidationError.NONE, result)
    }

}