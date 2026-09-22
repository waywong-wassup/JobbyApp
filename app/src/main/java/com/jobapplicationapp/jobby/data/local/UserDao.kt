package com.jobapplicationapp.jobby.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.jobapplicationapp.jobby.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Upsert //handle update + insert
    suspend fun insertUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * from User where userId = :userId")
    fun getCurrentUsers(userId: String): Flow<User?>
}