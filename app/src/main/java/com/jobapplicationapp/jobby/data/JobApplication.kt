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
    var jobTitle: String,
    var companyName: String,
    var location: String?,
    var salary: Long?, // save as cents and convert to decimal points later
    var applicationURL: String?,
    var progress: String,
    var contactName: String?,
    var contactDetails: String?,
    var jobType: String?,
    var applicationPostedDate: String?,
    var notes: String?
)  {
    companion object {
        val sampleJobApplication = listOf(
        JobApplication(1,"Janitor", "Mom's basement","Auckland", 5, "www.hiremeplz.co.nz","Applied","Aunty", "022123456", "full time", "today", "hope I get hired"),
        JobApplication(2,"Librarian","Library","",5, "www.hiremeplz.co.nz","Applied","Aunty", "022123456", "part time", "today", "hope I get hired")
        )
    }
}

enum class Progress (val progressPhase: String) {
    TOAPPLY("To Apply"),
    APPLIED("Applied"),
    SCREENCALL("Screen Call"),
    INTERVIEW("Interview"),
    OFFERED("Offered"),
    REJECTED("Rejected"),
    ACCEPTED("Accepted")
}
