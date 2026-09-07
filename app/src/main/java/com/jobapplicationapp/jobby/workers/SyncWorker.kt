package com.jobapplicationapp.jobby.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jobapplicationapp.jobby.JobbyApplication
import kotlinx.coroutines.flow.first

/**
 * A worker that finds all unsynced local data and pushes it to Firebase.
 */
class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val app = applicationContext as JobbyApplication
        val repository = app.container.jobApplicationRepository
        
        return try {
            // 1. Get all local jobs
            val allJobs = repository.getAllJobApplications().first()
            
            // 2. Filter unsynced ones (this is simple logic, can be more complex)
            val unsyncedJobs = allJobs.filter { !it.isSynced }
            
            // 3. Re-save them through the repository 
            // In SyncJobApplicationRepository, saving triggers a sync if enabled.
            // Or we can manually call the remote repository here.
            
            // For now, we assume if this worker runs, we want to force a sync.
            unsyncedJobs.forEach { job ->
                repository.updateJobApplication(job)
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
