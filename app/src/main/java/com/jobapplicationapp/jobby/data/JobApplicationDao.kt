package com.jobapplicationapp.jobby.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface JobApplicationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addJobApplication(jobApplication: JobApplication)

    @Update
    suspend fun updateJobApplication(jobApplication: JobApplication)

    @Delete
    suspend fun deleteJobApplication(jobApplication: JobApplication)

    @Query("SELECT * from JobApplication WHERE userId = :userId ORDER BY jobApplicationId ASC")
    fun getAllJobApplications(userId: String): Flow<List<JobApplication>>

    @Query("SELECT * from JobApplication WHERE jobApplicationId = :id")
    fun getJobApplicationById(id: String): Flow<JobApplication?>
}