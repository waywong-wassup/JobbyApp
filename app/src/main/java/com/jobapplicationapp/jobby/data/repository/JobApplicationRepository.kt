package com.jobapplicationapp.jobby.data.repository

import com.jobapplicationapp.jobby.data.model.JobApplication
import kotlinx.coroutines.flow.Flow

/*
note to self
this file is the interface, the contract on the function should do.
this is so same function for cloud and local save can be implemented separately without the need to change the 'interface'/name in this file
 */
interface JobApplicationRepository {
    fun getAllJobApplications(userId: String): Flow<List<JobApplication>>
    suspend fun addJobApplication(jobApplication: JobApplication)
    suspend fun updateJobApplication(jobApplication: JobApplication)
    suspend fun deleteJobApplication(jobApplication: JobApplication)
    fun getJobApplicationById(id: String): Flow<JobApplication?>
    fun getUnsyncedJobApplications(userId: String): Flow<List<JobApplication>>
}