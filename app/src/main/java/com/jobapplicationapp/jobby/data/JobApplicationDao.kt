package com.jobapplicationapp.jobby.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface JobApplicationDao {
    @Insert
    suspend fun addJobApplication(jobApplication: JobApplication)

    @Update
    suspend fun updateJobApplication(jobApplication: JobApplication)

    @Delete
    suspend fun deleteJobApplication(jobApplication: JobApplication)

    @Query("SELECT * from JobApplication ORDER BY jobApplicationId ASC")
    fun getAllJobApplications(): Flow<List<JobApplication>>
}