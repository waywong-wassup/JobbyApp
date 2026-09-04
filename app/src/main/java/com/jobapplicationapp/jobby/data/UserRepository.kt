package com.jobapplicationapp.jobby.data

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun deleteUser(user: User)
    suspend fun addUser(user: User)
    fun getCurrentUser(userId: Int): Flow<User?>
}