package com.jobapplicationapp.jobby.model

import androidx.room.PrimaryKey

/**
 * Data class to represent user
 */
data class User(
    // PrimaryId for user not required now until firebase database is set up
    //    @PrimaryKey(autoGenerate = true)
    //    val userId: Int = 0,
    val firstName: String,
    val lastName: String
)
