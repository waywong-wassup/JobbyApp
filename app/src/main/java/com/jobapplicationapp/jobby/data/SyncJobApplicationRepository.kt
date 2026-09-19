package com.jobapplicationapp.jobby.data

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * A repository for coordinates local (Room) and remote (Firestore) data sources.
 * It follows the Offline-First principle: UI to always reads from local data.
 * Changes are saved locally first, then synced to remote if enabled.
 */
class SyncJobApplicationRepository(
    private val localRepository: JobApplicationRepository,
    private val remoteRepository: JobApplicationRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val externalScope: CoroutineScope
) : JobApplicationRepository {

    private val TAG = "SyncRepository"
    private var isSyncEnabled: Boolean = false
    private var isSyncing: Boolean = false
    
    // Internal state to track the active user for reactive syncing
    private val _currentUserId = MutableStateFlow<String?>(null)

    init {
        // Keep the local flag updated for synchronous add/delete operations
        externalScope.launch {
            userPreferencesRepository.isSyncEnabled.collect { enabled ->
                isSyncEnabled = enabled
            }
        }

        // Centralized Sync Manager: watches for both User ID and Sync Setting
        externalScope.launch {
            combine(_currentUserId, userPreferencesRepository.isSyncEnabled) { userId, enabled ->
                userId to enabled
            }.collect { (userId, enabled) ->
                if (userId != null && userId != OFFLINE_USER_ID && enabled && !isSyncing) {
                    runSyncCycle(userId)
                }
            }
        }
    }

    private suspend fun runSyncCycle(userId: String) {
        isSyncing = true
        try {
            Log.d(TAG, "Starting centralized sync cycle for user: $userId")
            // 1. PUSH local changes to remote
            localRepository.getUnsyncedJobApplications(userId).firstOrNull()?.forEach { job ->
                trySync(job)
            }

            // 2. PULL remote changes (one-time fetch to sync)
            val remoteJobs = remoteRepository.getAllJobApplications(userId).first()
            remoteJobs.forEach { remoteJob ->
                // Sync remote job to local database
                localRepository.updateJobApplication(remoteJob.copy(isSynced = true))
            }
            Log.d(TAG, "Sync cycle completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error during centralized sync cycle: ${e.message}", e)
        } finally {
            isSyncing = false
        }
    }

    override fun getAllJobApplications(userId: String): Flow<List<JobApplication>> {
        // Update the current user ID to trigger the reactive sync if needed
        _currentUserId.value = userId
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
        addJobApplication(jobApplication)
    }

    override suspend fun deleteJobApplication(jobApplication: JobApplication) {
        localRepository.deleteJobApplication(jobApplication)
        if (isSyncEnabled && jobApplication.userId != OFFLINE_USER_ID) {
            externalScope.launch {
                try {
                    remoteRepository.deleteJobApplication(jobApplication)
                } catch (e: Exception) {
                    Log.e(TAG, "Error deleting job application from remote: ${e.message}", e)
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
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing individual job application: ${e.message}", e)
            }
        }
    }

    override fun getUnsyncedJobApplications(userId: String): Flow<List<JobApplication>> {
        return localRepository.getUnsyncedJobApplications(userId)
    }
}
