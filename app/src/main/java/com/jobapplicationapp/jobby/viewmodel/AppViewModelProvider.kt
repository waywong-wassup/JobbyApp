package com.jobapplicationapp.jobby.viewmodel

import androidx.lifecycle.viewmodel.initializer
import com.jobapplicationapp.jobby.JobbyApplication

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.viewModelFactory

//point app to view model
object AppViewModelProvider {
    val Factory = viewModelFactory {

        initializer {
            AuthViewModel(
                jobbyApplication().container.authRepository
            )
        }

        initializer {
            JobApplicationViewModel(
                jobbyApplication().container.jobApplicationRepository
            )
        }
        initializer {
            UserViewModel(
                jobbyApplication().container.userRepository,
                jobbyApplication().container.jobApplicationRepository,
                jobbyApplication().container.userPreferencesRepository
            )
        }
    }
}

//for locating JobbyApplication.kt
fun CreationExtras.jobbyApplication(): JobbyApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as JobbyApplication)