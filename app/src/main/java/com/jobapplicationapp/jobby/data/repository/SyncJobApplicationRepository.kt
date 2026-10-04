package com.jobapplicationapp.jobby.data.repository

import android.util.Log
import com.jobapplicationapp.jobby.data.model.JobApplication
import com.jobapplicationapp.jobby.data.model.OFFLINE_USER_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * A repository that coordinates local (Room) and remote (Firestore) data sources.
 *
 * Follows the Offline-First principle: the UI always reads from the local database.
 * Changes are saved locally first and asynchronously synced to remote when sync is enabled.
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

    // Kick off coroutine to watch for changes in sync setting when the repository is initialized (in app container)
    init {
        // Keep the local flag updated for synchronous add/delete operations
        externalScope.launch {
            userPreferencesRepository.isSyncEnabled.collect { enabled ->
                isSyncEnabled = enabled
            }
        }

        // Centralized Sync Manager: watches for both User ID and Sync Setting - with combine operator
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

    /**
     * Executes a centralized bidirectional synchronization cycle for [userId].
     *
     * 1. **PUSH**: Gathers all unsynced local records and pushes them to remote.
     * 2. **PULL**: Fetches remote records and updates the local database.
     *
     * Uses [isSyncing] as a check to prevent overlapping sync cycles.
     */
    private suspend fun runSyncCycle(userId: String) {
        // Avoid multiple sync cycles running concurrently
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
            // Reset sync flag after completion
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