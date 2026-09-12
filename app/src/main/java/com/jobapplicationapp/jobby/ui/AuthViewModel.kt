package com.jobapplicationapp.jobby.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.jobapplicationapp.jobby.data.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
): ViewModel() {
    val currentUser: StateFlow<FirebaseUser?> = authRepository.currentUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null //Todo - should this be default value?
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
}