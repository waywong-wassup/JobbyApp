package com.jobapplicationapp.jobby.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jobapplicationapp.jobby.JobbyApplication
import kotlinx.coroutines.flow.first

private const val TAG = "SyncWorker"

/**
 * A worker that finds all unsynced local data and pushes it to Firebase.
 */
class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val app = applicationContext as JobbyApplication
        val userId = inputData.getString("userId") ?: return Result.failure()
        val repository = app.container.jobApplicationRepository
        
        return try {
            // 1. Get all local jobs
            val allJobs = repository.getAllJobApplications(userId).first()
            
            // 2. Filter unsynced ones
            val unsyncedJobs = allJobs.filter { !it.isSynced }
            
            Log.d(TAG, "Syncing ${unsyncedJobs.size} jobs for user $userId")
            
            // 3. Re-save them through the repository 
            unsyncedJobs.forEach { job ->
                repository.updateJobApplication(job)
            }
            
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing jobs: ${e.message}", e)
            Result.retry()
        }
    }
}
