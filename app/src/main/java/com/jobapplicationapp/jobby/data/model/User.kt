package com.jobapplicationapp.jobby.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

const val OFFLINE_USER_ID = "offline_user"

/**
 * Data class to represent user
 */
@Entity(tableName = "User")
data class User(

    @PrimaryKey
    val userId: String = UUID.randomUUID().toString(),
    val firstName: String,
    val lastName: String,
    val lastModified: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
) {
    companion object {
        val guestUser = User(userId = OFFLINE_USER_ID, firstName = "Guest", lastName = "User")
        val sampleUser = User("sample_user", "Sampler", "User")

    }
}
