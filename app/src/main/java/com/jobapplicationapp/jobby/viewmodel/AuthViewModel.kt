package com.jobapplicationapp.jobby.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.jobapplicationapp.jobby.data.remote.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
): ViewModel() {
    val authUiState: StateFlow<AuthUiState> = authRepository.currentUser
        .map { user ->
            if (user != null) AuthUiState.Success(user) else AuthUiState.Error
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AuthUiState.Loading
        )

    val currentUser: StateFlow<FirebaseUser?> = authRepository.currentUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun signIn(email: String, password: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.signInWithEmailAndPassword(email, password)
            onResult(result)
        }
    }

    fun signUp(email: String, password: String, firstName: String, lastName: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.signUpWithEmailAndPassword(email, password, firstName, lastName)
            onResult(result)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    //handles both sign in and sign up when using google
    fun signInWithGoogle(idToken: String, onResult: (Result<Unit>) -> Unit){
        viewModelScope.launch {
            val result = authRepository.signInWithGoogle(idToken)
            onResult(result)
        }
    }


}