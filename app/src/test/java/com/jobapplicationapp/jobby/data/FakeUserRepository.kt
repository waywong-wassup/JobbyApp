package com.jobapplicationapp.jobby.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeUserRepository : UserRepository {

    private val usersFlow = MutableStateFlow<List<User>>(emptyList())
    override suspend fun updateUser(user: User) {
        usersFlow.update { currentList ->
            currentList.map {
                if (it.userId == user.userId) user else it
            }
        }
    }

    override suspend fun deleteUser(user: User) {
        usersFlow.update { list -> list.filterNot { it.userId == user.userId } }
    }

    override suspend fun addUser(user: User) {
        usersFlow.update { it + user }
    }

    override fun getCurrentUser(userId: Int): Flow<User?> {
        return usersFlow.map { list ->
            list.find { it.userId == userId }
        }
    }

}