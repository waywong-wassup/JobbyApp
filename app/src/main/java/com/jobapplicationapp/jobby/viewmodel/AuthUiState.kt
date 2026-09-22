package com.jobapplicationapp.jobby.viewmodel

import com.google.firebase.auth.FirebaseUser

sealed interface AuthUiState {
    object Loading : AuthUiState
    data class Success(val user: FirebaseUser) : AuthUiState
    object Error : AuthUiState // Represents "Not Logged In" or an actual error
}