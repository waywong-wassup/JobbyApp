package com.jobapplicationapp.jobby.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeJobApplicationRepository : JobApplicationRepository {

    private val jobApplicationsFlow = MutableStateFlow<List<JobApplication>>(emptyList())
    var shouldThrowError = false
    override fun getAllJobApplications(): Flow<List<JobApplication>> {
        return jobApplicationsFlow
    }

    override suspend fun addJobApplication(jobApplication: JobApplication) {
        if (shouldThrowError) throw Exception("Save error!")
        //why use update here? to create a brand new list to signal to UI that there is changes.
        //list.add wouldn't work because it doesn't tell ui changes has changed. so update is used.
        jobApplicationsFlow.update { jobList ->
            val newId = jobList.size + 1
            // assign new id
            val jobWithNewId = jobApplication.copy(jobApplicationId = newId)

            jobList + jobWithNewId
        }
    }

    override suspend fun updateJobApplication(jobApplication: JobApplication) {
        // take current list and update the item with the same id
        jobApplicationsFlow.update { list ->
            //list.map - go through each jobapplicationid and check if the id matches, if id match, then new jobApplication is replacing it. if not, then keeping the instance as is.
            list.map {
                if (it.jobApplicationId == jobApplication.jobApplicationId) jobApplication else it
            }
        }
    }


    override suspend fun deleteJobApplication(jobApplication: JobApplication) {
        jobApplicationsFlow.update { list ->
            list.filterNot { it.jobApplicationId == jobApplication.jobApplicationId } }

    }

    override fun getJobApplicationById(id: Int): Flow<JobApplication?> {
        //go through all items in the list and check id matches, return the one that matches
        return jobApplicationsFlow.map { list ->
            list.find { it.jobApplicationId == id }
        }
    }

}