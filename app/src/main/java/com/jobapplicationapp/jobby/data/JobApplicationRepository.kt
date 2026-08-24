package com.jobapplicationapp.jobby.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface JobApplicationRepository {
    fun getAllJobApplications(): StateFlow<List<JobApplication>>
    suspend fun addJobApplication(jobApplication: JobApplication)
    suspend fun updateJobApplication(jobApplication: JobApplication)
    suspend fun deleteJobApplication(jobApplication: JobApplication)
}