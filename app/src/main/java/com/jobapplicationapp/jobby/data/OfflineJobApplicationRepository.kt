package com.jobapplicationapp.jobby.data

import kotlinx.coroutines.flow.Flow

/*
this is where it actually does the job it says in JobApplicationRepository
 */
class OfflineJobApplicationRepository(private val dao: JobApplicationDao) : JobApplicationRepository {
    override fun getAllJobApplications(userId: String) = dao.getAllJobApplications(userId)

    override suspend fun addJobApplication(jobApplication: JobApplication) {
        dao.addJobApplication(jobApplication) // actual database work happens here
    }

    override suspend fun updateJobApplication(jobApplication: JobApplication) {
        dao.addJobApplication(jobApplication) //same with addJobApplication due to UpSert is used in JobApplicationDao
    }

    override suspend fun deleteJobApplication(jobApplication: JobApplication) {
        dao.deleteJobApplication(jobApplication)
    }

    override fun getJobApplicationById(id: String): Flow<JobApplication?> {
        return dao.getJobApplicationById(id)
    }
    override fun getUnsyncedJobApplications(userId: String): Flow<List<JobApplication>> {
        return dao.getUnsyncedJobApplications(userId)
    }

}