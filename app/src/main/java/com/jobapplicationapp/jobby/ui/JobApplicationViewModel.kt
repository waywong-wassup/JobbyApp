package com.jobapplicationapp.jobby.ui

import androidx.lifecycle.ViewModel
import com.jobapplicationapp.jobby.data.JobApplicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.lifecycle.viewModelScope
import com.jobapplicationapp.jobby.data.JobApplication
import com.jobapplicationapp.jobby.data.OFFLINE_USER_ID
import com.jobapplicationapp.jobby.data.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest

class JobApplicationViewModel (
    private val jobApplicationRepository: JobApplicationRepository
) : ViewModel() {
    private val _userId = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<JobApplicationUiState> = _userId
        .filterNotNull()
        .flatMapLatest { userId -> jobApplicationRepository.getAllJobApplications(userId)}
            .map<List<JobApplication>, JobApplicationUiState> {
                JobApplicationUiState.Success(it)
            }
        .catch {
           emit(JobApplicationUiState.Error)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = JobApplicationUiState.Loading
        )
    private val _currentJobApplication = MutableStateFlow<JobApplication?>(null)
    val currentJobApplication = _currentJobApplication.asStateFlow()

    fun addJobApplication(newJobApplication: JobApplication) {
        viewModelScope.launch {
            jobApplicationRepository.addJobApplication(newJobApplication)
        }
    }

    fun updateJobApplication(jobApplication: JobApplication) {
        viewModelScope.launch {
            jobApplicationRepository.updateJobApplication(jobApplication)
        }
    }

    fun deleteJobApplication(jobApplication: JobApplication) {
        viewModelScope.launch {
            jobApplicationRepository.deleteJobApplication(jobApplication)
        }
    }



    private val _changingJobApplication = MutableStateFlow<JobApplication?>(null)
    val changingJobApplication = _changingJobApplication.asStateFlow()

    fun updateJobDetailFieldsUiStates(transform: JobApplication.() -> JobApplication) {
        _changingJobApplication.update {
            currentJobApplication -> currentJobApplication?.transform()
        }
    }

    fun deleteCurrentJobApplication() {
        val job = _changingJobApplication.value
        try {
            if (job != null) {
                deleteJobApplication(job)
            }
        }
        catch (e: Exception) {
            println("Error deleting job application: ${e.message}")
        }
    }

    fun saveJobApplicationChange() {
        val changes = _changingJobApplication.value
        if (changes != null && validateInput() == ValidationError.NONE) {
            viewModelScope.launch {
                try {
                    jobApplicationRepository.updateJobApplication(changes)
                } catch (e: Exception) {
                    println("Error saving job application: ${e.message}")
                }
            }
        }

    }
    fun selectJob(job: JobApplication) {
        _changingJobApplication.value = job
    }

    fun loadJobApplication(id: String) {
        // id = "0" if add new job
        if(id == "0"){
            _changingJobApplication.value = JobApplication(
                userId = _userId.value ?: OFFLINE_USER_ID,
                jobTitle = "",
                companyName = "",
                progress = "Applied",
                location = "",
                salary = null,
                applicationURL = null,
                contactName = null,
                contactDetails = null,
                jobType = null,
                applicationPostedDate = null,
                notes = null
            )
        }
        else
        {
            viewModelScope.launch {
                val job = jobApplicationRepository.getJobApplicationById(id)
                    .firstOrNull()
                if (job != null) {
                    _changingJobApplication.value = job
                } else {
                    // Handle the case where the job application with the given ID is not found
                    println("Error: Job application with ID $id not found")
                    _changingJobApplication.value = null
                }
            }
        }
    }

    enum class ValidationError { NONE, JOB_TITLE_REQUIRED, COMPANY_NAME_REQUIRED }

    fun validateInput() : ValidationError {
        val job = _changingJobApplication.value ?: return ValidationError.NONE
        return when {
            job.jobTitle.isBlank() -> ValidationError.JOB_TITLE_REQUIRED
            job.companyName.isBlank() -> ValidationError.COMPANY_NAME_REQUIRED
            else -> ValidationError.NONE
        }
    }

    fun setUserId(id: String) {
        _userId.value = id
    }

 }