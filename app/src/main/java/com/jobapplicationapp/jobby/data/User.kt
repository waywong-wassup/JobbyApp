package com.jobapplicationapp.jobby.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class to represent user
 */
@Entity(tableName = "User")
data class User(

    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,
    val firstName: String,
    val lastName: String
) {
    companion object {
        val sampleUser = User(1, "Job", "Seeker")

    }
}