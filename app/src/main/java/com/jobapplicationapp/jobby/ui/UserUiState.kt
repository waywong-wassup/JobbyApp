package com.jobapplicationapp.jobby.ui

import com.jobapplicationapp.jobby.data.User
sealed interface UserUiState {
    object Loading : UserUiState
    data class Success (val user: User) : UserUiState
    object Error : UserUiState
}