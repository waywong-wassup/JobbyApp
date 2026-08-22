package com.jobapplicationapp.jobby.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
 * TODO - setup foreign key to user table for cloud DB later
 * Data class to represent the information user enter for a job application
 */
@Entity(tableName = "JobApplication")
data class JobApplication(
    @PrimaryKey(autoGenerate = true)
    val jobApplicationId: Int = 0,
    val jobTitle: String,
    val companyName: String,
    val location: String?,
    val salary: Long?, // save as cents and convert to decimal points later
    val applicationURL: String?,
    val progress: String,
    val contactName: String?,
    val contactDetails: String?,
    val jobType: String?,
    val applicationPostedDate: String?,
    val notes: String?
)  {
    companion object {
        val sampleJobApplication = listOf(
        JobApplication(1,"Janitor", "Mom's basement","Auckland", 5, "www.hiremeplz.co.nz","Applied","Aunty", "022123456", "full time", "today", "hope I get hired"),
        JobApplication(2,"Librarian","Library","Wellington",5, "www.hiremeplz.co.nz","Applied","Aunty", "022123456", "part time", "today", "hope I get hired")
        )
    }
}

