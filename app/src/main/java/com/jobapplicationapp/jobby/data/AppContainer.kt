package com.jobapplicationapp.jobby.data

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.MainScope

interface AppContainer {
    val jobApplicationRepository: JobApplicationRepository
    val userRepository: UserRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    private val applicationScope = MainScope()

    override val jobApplicationRepository: JobApplicationRepository by lazy {
        val local = OfflineJobApplicationRepository(AppDatabase.getDatabase(context).jobApplicationDao())
        val remote = FirestoreJobApplicationRepository(Firebase.firestore, Firebase.auth)
        SyncJobApplicationRepository(local, remote, applicationScope)
    }

    override val userRepository: UserRepository by lazy {
        OfflineUserRepository(AppDatabase.getDatabase(context).userDao())
    }

}
