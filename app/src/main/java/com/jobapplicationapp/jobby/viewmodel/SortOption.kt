package com.jobapplicationapp.jobby.viewmodel

import com.jobapplicationapp.jobby.data.model.JobApplication

/**
 * Defines all available sorting criteria for Job Applications.
 * @param displayName text shown in the UI menu.
 */
enum class SortOption (val displayName: String){
    LAST_MODIFIED_DESC("Last Modified: Newest"),
    LAST_MODIFIED_ASC("Last Modified: Oldest"),
    DATE_APPLIED_DESC("Date Applied: Newest"),
    DATE_APPLIED_ASC("Date Applied: Oldest"),
    JOB_TITLE_ASC("Job Title A-Z"),
    SALARY_DESC("Salary (High to Low)"),
    PROGRESS("Progress")
}

/**
 * Extension function to apply sorting to a list of job applications.
 */
fun List<JobApplication>.sortJobApplications(sortOption: SortOption): List<JobApplication> {
    return when (sortOption) {
        SortOption.LAST_MODIFIED_DESC -> sortedByDescending { it.lastModified }
        SortOption.LAST_MODIFIED_ASC -> sortedBy { it.lastModified }
        SortOption.DATE_APPLIED_DESC -> sortedWith(
            compareByDescending<JobApplication, String?>(nullsLast()) { it.applicationPostedDate }
        )
        SortOption.DATE_APPLIED_ASC -> sortedWith(
            compareBy<JobApplication,String?>(nullsLast()) { it.applicationPostedDate }
        )
        SortOption.JOB_TITLE_ASC -> sortedBy { it.jobTitle }
        SortOption.SALARY_DESC -> sortedByDescending { it.salary ?: 0L} //if salary is null, set to 0L for sorting purpose
        SortOption.PROGRESS -> sortedBy { it.progress }
    }
}
