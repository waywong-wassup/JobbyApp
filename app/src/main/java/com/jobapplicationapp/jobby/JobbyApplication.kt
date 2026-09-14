package com.jobapplicationapp.jobby

import com.jobapplicationapp.jobby.data.AppContainer
import com.jobapplicationapp.jobby.data.AppDataContainer
import android.app.Application
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.jobapplicationapp.jobby.workers.SyncWorker
import java.util.concurrent.TimeUnit

class JobbyApplication : Application() {
    // for holding app container
    lateinit var container: AppContainer

    //override the default onCreate
    override fun onCreate() {
        super.onCreate()
        //where we put appDataContainer in AppContainer.kt so that app has all data to start
        container = AppDataContainer(this)
    }

    /**
     * Schedules a periodic background sync to push local unsynced data to Firestore.
     */
    fun scheduleSync(userId: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .setInputData(Data.Builder().putString("userId", userId).build())
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "FirestoreSync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}
