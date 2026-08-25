package com.jobapplicationapp.jobby.data

import android.content.Context

interface AppContainer {
    val jobApplicationRepository: JobApplicationRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val jobApplicationRepository: JobApplicationRepository by lazy {
        // 1. Get the database
        // 2. Get the DAO
        // 3. Put them inside the OfflineRepository
        OfflineJobApplicationRepository(AppDatabase.getDatabase(context).jobApplicationDao())
    }
}