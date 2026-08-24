package com.jobapplicationapp.jobby.ui

import com.jobapplicationapp.jobby.data.JobApplication



sealed interface JobApplicationUiState {
    object Loading : JobApplicationUiState
    data class Success(val jobApplications: List<JobApplication>) : JobApplicationUiState
    object Error : JobApplicationUiState
}