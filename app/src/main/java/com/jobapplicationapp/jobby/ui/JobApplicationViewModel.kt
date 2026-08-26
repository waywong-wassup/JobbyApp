package com.jobapplicationapp.jobby.ui

import androidx.lifecycle.ViewModel
import com.jobapplicationapp.jobby.data.JobApplicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.lifecycle.viewModelScope
import com.jobapplicationapp.jobby.data.JobApplication
import com.jobapplicationapp.jobby.data.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class JobApplicationViewModel (
    private val jobApplicationRepository: JobApplicationRepository,
    //include userRepository for now until user edit feature is implemented
    private val userRepository: UserRepository
) : ViewModel() {
    val uiState: StateFlow<JobApplicationUiState> = jobApplicationRepository.getAllJobApplications()
        .map<List<JobApplication>, JobApplicationUiState> { JobApplicationUiState.Success(it) }
        .catch { emit(JobApplicationUiState.Error) }
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

    fun saveJobApplicationChange() {
        val changes = _changingJobApplication.value
        if (changes != null) {
            viewModelScope.launch {
                if (changes.jobApplicationId == 0) {
                    jobApplicationRepository.addJobApplication(changes)
                } else {
                    jobApplicationRepository.updateJobApplication(changes)
                }
            }
        }
    }
    fun selectJob(job: JobApplication) {
        _changingJobApplication.value = job
    }

    fun loadJobApplication(id: Int) {
        // id = 0 if add new job
        if(id==0){
            _changingJobApplication.value = JobApplication(
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
                jobApplicationRepository.getJobApplicationById(id)
                    .filterNotNull()
                    .first()
                    .let { job ->
                        _changingJobApplication.value = job
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

 }