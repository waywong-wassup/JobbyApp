package com.jobapplicationapp.jobby.data

import com.jobapplicationapp.jobby.data.model.JobApplication
import com.jobapplicationapp.jobby.data.repository.JobApplicationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update

class FakeJobApplicationRepository : JobApplicationRepository {

    private val jobApplicationsFlow = MutableStateFlow<List<JobApplication>>(emptyList())
    var shouldThrowError = false

    override fun getAllJobApplications(userId: String): Flow<List<JobApplication>> = flow {
        if (shouldThrowError) {
            throw Exception("Fake error")
        }
        jobApplicationsFlow.collect { list ->
            emit(list.filter { it.userId == userId })
        }
    }

    override suspend fun addJobApplication(jobApplication: JobApplication) {
        if (shouldThrowError) throw Exception("Save error!")
        //why use update here? to create a brand new list to signal to UI that there is changes.
        //list.add wouldn't work because it doesn't tell ui changes has changed. so update is used.
        jobApplicationsFlow.update { jobList ->
            jobList.filterNot { it.jobApplicationId == jobApplication.jobApplicationId} + jobApplication
        }
    }

    override suspend fun updateJobApplication(jobApplication: JobApplication) {
        // take current list and update the item with the same id
        addJobApplication(jobApplication)
    }


    override suspend fun deleteJobApplication(jobApplication: JobApplication) {
        jobApplicationsFlow.update { list ->
            list.filterNot { it.jobApplicationId == jobApplication.jobApplicationId } }

    }

    override fun getJobApplicationById(id: String): Flow<JobApplication?> {
        //go through all items in the list and check id matches, return the one that matches
        return jobApplicationsFlow.map { list ->
            list.find { it.jobApplicationId == id }
        }
    }

    override fun getUnsyncedJobApplications(userId: String): Flow<List<JobApplication>> {
        return jobApplicationsFlow.map { list ->
            list.filter { it.userId == userId && !it.isSynced }
        }
    }
}
