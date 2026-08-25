package com.jobapplicationapp.jobby.data

/*
this is where it actually does the job it says in JobApplicationRepository
 */
class OfflineJobApplicationRepository(private val dao: JobApplicationDao) : JobApplicationRepository {
    override fun getAllJobApplications() = dao.getAllJobApplications()

    override suspend fun addJobApplication(jobApplication: JobApplication) {
        dao.addJobApplication(jobApplication) // actual database work happens here
    }

    override suspend fun updateJobApplication(jobApplication: JobApplication) {
        dao.updateJobApplication(jobApplication)
    }

    override suspend fun deleteJobApplication(jobApplication: JobApplication) {
        dao.deleteJobApplication(jobApplication)
    }

    override fun getJobApplicationById(id: Int) = dao.getJobApplicationById(id)

}