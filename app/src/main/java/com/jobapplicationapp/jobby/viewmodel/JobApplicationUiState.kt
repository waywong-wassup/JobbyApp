package com.jobapplicationapp.jobby.viewmodel

import com.jobapplicationapp.jobby.data.model.JobApplication



sealed interface JobApplicationUiState {
    object Loading : JobApplicationUiState
    data class Success(val jobApplications: List<JobApplication>) : JobApplicationUiState
    object Error : JobApplicationUiState
}