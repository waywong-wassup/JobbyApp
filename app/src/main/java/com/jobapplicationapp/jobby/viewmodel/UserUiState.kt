package com.jobapplicationapp.jobby.viewmodel

import com.jobapplicationapp.jobby.data.model.User
sealed interface UserUiState {
    object Loading : UserUiState
    data class Success (val user: User) : UserUiState
    object Error : UserUiState
}