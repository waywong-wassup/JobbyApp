package com.jobapplicationapp.jobby.ui

import androidx.lifecycle.ViewModel
import com.jobapplicationapp.jobby.data.JobApplicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.lifecycle.viewModelScope
import com.jobapplicationapp.jobby.data.JobApplication
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class JobApplicationViewModel (
    private val jobApplicationRepository: JobApplicationRepository
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

    fun updateJobTitleFieldStates(newTitle: String) {
        _changingJobApplication.update {
            it?.copy(jobTitle = newTitle)
        }
    }

    fun updateCompanyNameFieldStates(newCompanyName: String) {
        _changingJobApplication.update {
            it?.copy(companyName = newCompanyName)
        }

    }

    fun updateLocationFieldStates(newLocation: String) {
        _changingJobApplication.update {
            it?.copy(location = newLocation)
        }
    }

    fun updateApplicationUrlFieldStates(newApplicationUrl: String) {
        _changingJobApplication.update {
            it?.copy(location = newApplicationUrl)
        }
    }

    fun updateProgressFieldStates(newProgress: String) {
        _changingJobApplication.update {
            it?.copy(progress = newProgress)
        }
    }

    fun updateSalaryFieldStates(newSalary: String) {
        _changingJobApplication.update {
            it?.copy(salary = newSalary.toLongOrNull())
        }
    }

    fun updatePostedDateFieldStates(newPostedDate: String) {
        _changingJobApplication.update {
            it?.copy(applicationPostedDate = newPostedDate)
        }
    }

    fun updateJobTypeFieldStates(newJobType: String) {
        _changingJobApplication.update {
            it?.copy(jobType = newJobType)
        }
    }

    fun updateContactNameFieldStates(newContactName: String) {
        _changingJobApplication.update {
            it?.copy(contactName = newContactName)
        }
    }

    fun updateContactDetailsFieldStates(newContactDetails: String) {
        _changingJobApplication.update {
            it?.copy(contactDetails = newContactDetails)
        }
    }

    fun updateNotesFieldStates(newNotes: String) {
        _changingJobApplication.update {
            it?.copy(notes = newNotes)
        }
    }

    fun saveJobApplicationChange() {
        val changes = _changingJobApplication.value
        if (changes != null) {
            viewModelScope.launch {
                jobApplicationRepository.updateJobApplication(changes)
            }
        }
    }

    fun selectJob(job: JobApplication) {
        _changingJobApplication.value = job
    }


 }