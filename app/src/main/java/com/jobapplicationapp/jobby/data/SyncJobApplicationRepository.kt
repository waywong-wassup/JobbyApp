package com.jobapplicationapp.jobby.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * A repository for coordinates local (Room) and remote (Firestore) data sources.
 * It follows the Offline-First principle: UI to always reads from local data.
 * Changes are saved locally first, then synced to remote if enabled.
 * Note that both localRepository and remoteRepostiroy variable is set to JobApplicationRepository
 * for interface
 */
class SyncJobApplicationRepository(
    private val localRepository: JobApplicationRepository,
    private val remoteRepository: JobApplicationRepository,
    private val externalScope: CoroutineScope
) : JobApplicationRepository {

    var isSyncEnabled: Boolean = false
    private var isSyncing: Boolean = false

    override fun getAllJobApplications(userId: String): Flow<List<JobApplication>> {
        if (isSyncEnabled && userId != OFFLINE_USER_ID) {
            externalScope.launch {
                if (!isSyncing) {
                    isSyncing = true
                    val getJobsFromRemote = remoteRepository.getAllJobApplications(userId)
                    getJobsFromRemote.collect { remoteJobs ->
                        remoteJobs.forEach { remoteJob ->
                            remoteRepository.updateJobApplication(remoteJob.copy(isSynced = true))
                            localRepository.updateJobApplication(remoteJob)
                        }
                    }
                    isSyncing = false
                }
            }
        }
        return localRepository.getAllJobApplications(userId)
    }

    override suspend fun addJobApplication(jobApplication: JobApplication) {
        val jobWithMetadata = jobApplication.copy(
            lastModified = System.currentTimeMillis(),
            isSynced = false
        )
        localRepository.addJobApplication(jobWithMetadata)
        
        if (isSyncEnabled && jobApplication.userId != OFFLINE_USER_ID) {
            trySync(jobWithMetadata)
        }
    }

    override suspend fun updateJobApplication(jobApplication: JobApplication) {
        addJobApplication(jobApplication) //same action as addJobApplication
    }

    override suspend fun deleteJobApplication(jobApplication: JobApplication) {
        localRepository.deleteJobApplication(jobApplication)
        if (isSyncEnabled && jobApplication.userId != OFFLINE_USER_ID) {
            externalScope.launch {
                try {
                    remoteRepository.deleteJobApplication(jobApplication)
                } catch (e: Exception) {
                    // Log error or retry later
                }
            }
        }
    }

    override fun getJobApplicationById(id: String): Flow<JobApplication?> {
        return localRepository.getJobApplicationById(id)
    }

    private fun trySync(job: JobApplication) {
        externalScope.launch {
            try {
                remoteRepository.addJobApplication(job.copy(isSynced = true))
                // Update local status if successful
                localRepository.updateJobApplication(job.copy(isSynced = true))
            } catch (e: Exception) {
                // If sync fails, it remains isSynced = false for later retry
            }
        }
    }
}
