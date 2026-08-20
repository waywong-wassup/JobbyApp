package com.jobapplicationapp.jobby.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
 * Hard coding data for now as placeholder TODO - remove hardcoded data
 * Data class to represent the information user enter for a job application
 */
@Entity(tableName = "JobApplication")
data class JobApplication(
    @PrimaryKey(autoGenerate = true)
    val jobApplicationId: Int = 0,
    val jobTitle: String,
    val companyName: String,
    val salary: Long?, // save as cents and convert to decimal points later
    val applicationURL: String?,
    val progress: String,
    val contactName: String?,
    val contactDetails: String?,
    val jobType: String?,
    val applicationPostedDate: String?,
    val notes: String?
)

///*
// * List of job applications TODO - remove hardcoded data
// */
//val jobApplications = listOf(
//    JobApplication("Janitor", "Mom's basement", "5", "www.hiremeplz.co.nz","Applied","Aunty", "022123456", "full time", "today", "hope I get hired"),
//    JobApplication("Librarian","Library", "5", "www.hiremeplz.co.nz","Applied","Aunty", "022123456", "part time", "today", "hope I get hired"
//    )
//)
