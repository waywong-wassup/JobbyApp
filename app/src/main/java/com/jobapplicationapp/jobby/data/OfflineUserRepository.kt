package com.jobapplicationapp.jobby.data


class OfflineUserRepository(private val dao: UserDao) : UserRepository
{
    override suspend fun deleteUser(user: User) {
        dao.deleteUser(user)
    }
    override suspend fun addUser(user: User) {
        dao.insertUser(user)
    }
    override fun getCurrentUser(userId: String) = dao.getCurrentUsers(userId)

}