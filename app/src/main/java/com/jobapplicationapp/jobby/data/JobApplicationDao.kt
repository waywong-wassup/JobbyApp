package com.jobapplicationapp.jobby.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface JobApplicationDao {
    @Insert
    suspend fun insert(jobApplication: JobApplication)

    @Update
    suspend fun update(jobApplication: JobApplication)

    @Delete
    suspend fun delete(jobApplication: JobApplication)

    @Query("SELECT * from JobApplication ORDER BY jobApplicationId ASC")
    fun getAllJobApplications()
}