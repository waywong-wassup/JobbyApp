package com.jobapplicationapp.jobby.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobapplicationapp.jobby.data.User
import com.jobapplicationapp.jobby.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.ThreadLocalRandom.current

class UserViewModel(
    private val userRepository: UserRepository,
    userId: Int
) : ViewModel() {
   val userUiState : StateFlow<UserUiState> = userRepository.getCurrentUser(userId)
       .map {user ->
           if(user != null) UserUiState.Success(user) else UserUiState.Error
       }
       .catch {emit(UserUiState.Error)}
       .stateIn(
           scope = viewModelScope,
           started = SharingStarted.WhileSubscribed(5_000),
           initialValue = UserUiState.Loading,
       )

    //for reflecting change on ui
    private val _changingUserDetails = MutableStateFlow<User?> (null)
    val changingUserDetails = _changingUserDetails.asStateFlow()

    fun startEditingUser(user: User) {
        _changingUserDetails.value = user
    }

    fun updateUserDraft(firstName: String, lastName: String) {
        _changingUserDetails.update { currentUser ->
            (currentUser ?: User(userId = 1, firstName = "", lastName = ""))
                .copy(firstName = firstName, lastName = lastName)
        }
    }

    fun saveUserDraft() {
        val changes = _changingUserDetails.value
        if (changes != null) {
            viewModelScope.launch {
                try{
                    saveUserToDatabase(changes)
                } catch (e: Exception) {
                    println("Error saving user: ${e.message}")
                }
            }
        }

    }

    fun saveUserToDatabase(user: User) {
        viewModelScope.launch {
            userRepository.addUser(user)
        }
    }



}