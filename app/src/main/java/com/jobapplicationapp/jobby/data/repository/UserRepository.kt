package com.jobapplicationapp.jobby.data.repository

import com.jobapplicationapp.jobby.data.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun deleteUser(user: User)
    suspend fun addUser(user: User)
    fun getCurrentUser(userId: String): Flow<User?>
}