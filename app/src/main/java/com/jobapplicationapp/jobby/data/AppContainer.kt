package com.jobapplicationapp.jobby.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.jobapplicationapp.jobby.data.local.AppDatabase
import com.jobapplicationapp.jobby.data.local.OfflineJobApplicationRepository
import com.jobapplicationapp.jobby.data.local.OfflineUserRepository
import com.jobapplicationapp.jobby.data.remote.AuthRepository
import com.jobapplicationapp.jobby.data.remote.FirebaseAuthRepository
import com.jobapplicationapp.jobby.data.remote.FirestoreJobApplicationRepository
import com.jobapplicationapp.jobby.data.repository.DataStoreUserPreferencesRepository
import com.jobapplicationapp.jobby.data.repository.JobApplicationRepository
import com.jobapplicationapp.jobby.data.repository.SyncJobApplicationRepository
import com.jobapplicationapp.jobby.data.repository.UserPreferencesRepository
import com.jobapplicationapp.jobby.data.repository.UserRepository
import kotlinx.coroutines.MainScope


interface AppContainer {
    val jobApplicationRepository: JobApplicationRepository
    val userRepository: UserRepository
    val authRepository: AuthRepository
    val userPreferencesRepository: UserPreferencesRepository
}

private const val USER_PREFERENCES_NAME = "user_preferences"
private val Context.dataStore by preferencesDataStore(name = USER_PREFERENCES_NAME)

class AppDataContainer(private val context: Context) : AppContainer {

    private val applicationScope = MainScope()

    override val jobApplicationRepository: JobApplicationRepository by lazy {
        val local =
            OfflineJobApplicationRepository(AppDatabase.getDatabase(context).jobApplicationDao())
        val remote = FirestoreJobApplicationRepository(Firebase.firestore, Firebase.auth)
        SyncJobApplicationRepository(local, remote, userPreferencesRepository, applicationScope)
    }

    override val userRepository: UserRepository by lazy {
        OfflineUserRepository(AppDatabase.getDatabase(context).userDao())
    }

    override val authRepository: AuthRepository by lazy {
        FirebaseAuthRepository(Firebase.auth)
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        DataStoreUserPreferencesRepository(context.dataStore)
    }

}
