package com.jobapplicationapp.jobby.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeUserRepository : UserRepository {

    private val usersFlow = MutableStateFlow<List<User>>(emptyList())
    var shouldThrowError = false

    override suspend fun deleteUser(user: User) {
        usersFlow.update { list -> list.filterNot { it.userId == user.userId } }
    }

    override suspend fun addUser(user: User) {
        usersFlow.update { list -> list.filterNot {it.userId == user.userId} + user}
    }

    override fun getCurrentUser(userId: String): Flow<User?> {
        return usersFlow.map { list ->
            list.find { it.userId == userId }
        }
    }

}