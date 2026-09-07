package com.jobapplicationapp.jobby.ui

import androidx.lifecycle.viewmodel.initializer
import com.jobapplicationapp.jobby.JobbyApplication

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.viewModelFactory

//point app to view model
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            JobApplicationViewModel(
                jobbyApplication().container.jobApplicationRepository,
                "1" //temp sample user for now TODO: replace this when Firebase is ready
            )
        }
        initializer {
            UserViewModel(
                jobbyApplication().container.userRepository,
                "1" //temp sample user for now TODO: replace this when Firebase is ready
            )
        }
    }
}

//for locating JobbyApplication.kt
fun CreationExtras.jobbyApplication(): JobbyApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as JobbyApplication)