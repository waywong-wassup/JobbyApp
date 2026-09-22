package com.jobapplicationapp.jobby.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/*
 * Data class to represent the information user enter for a job application
 */
@Entity(
    tableName = "JobApplication",
    foreignKeys = [
        ForeignKey(
            entity = User::class, // the parent entity
            parentColumns = ["userId"], // the PK of parent
            childColumns = ["userId"], // the FK for this child table
            onDelete = ForeignKey.CASCADE // delete the child item in child table when parent is deleted
        )
    ],
    indices = [Index("userId")]
)
data class JobApplication(
    @PrimaryKey
    val jobApplicationId: String = UUID.randomUUID().toString(),
    val userId: String = "",
    var jobTitle: String = "",
    var companyName: String = "",
    var appliedDate: String? = null,
    var location: String?= null,
    var salary: Long? = null, // save as cents and convert to decimal points later
    var applicationURL: String? = null,
    var progress: String = "To Apply" ,
    var contactName: String? = null,
    var contactDetails: String? = null,
    var jobType: String? = null,
    var applicationPostedDate: String? = null,
    var notes: String? = null,
    var lastModified: Long = System.currentTimeMillis(),
    var isSynced: Boolean = false
)  {
    companion object {
        val sampleJobApplication = listOf(
        JobApplication("1","1","Janitor", "Mom's basement","","Auckland", 5, "www.hiremeplz.co.nz","Applied","Aunty", "022123456", "full time", "today", "hope I get hired"),
        JobApplication("2","1","Librarian","Library","","",5, "www.hiremeplz.co.nz","Applied","Aunty", "022123456", "part time", "today", "hope I get hired")
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
    ACCEPTED("Accepted"),
    GHOSTED("Ghosted")
}
