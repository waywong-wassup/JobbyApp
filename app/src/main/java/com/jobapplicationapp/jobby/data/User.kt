package com.jobapplicationapp.jobby.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

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
        val sampleUser = User("1", "Job", "Seeker")

    }
}
