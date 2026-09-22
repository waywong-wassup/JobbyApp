package com.jobapplicationapp.jobby.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jobapplicationapp.jobby.data.model.JobApplication
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

    @Query("SELECT * FROM JobApplication WHERE userId = :userId AND isSynced = 0")
    fun getUnsyncedJobApplications(userId: String): Flow<List<JobApplication>>
}