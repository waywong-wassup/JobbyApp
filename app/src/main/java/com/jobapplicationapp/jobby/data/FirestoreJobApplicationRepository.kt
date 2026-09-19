package com.jobapplicationapp.jobby.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await

class FirestoreJobApplicationRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : JobApplicationRepository {

    private fun userJobsCollection(userId: String) = firestore.collection("users")
        .document(userId)
        .collection("jobApplications")

    override fun getAllJobApplications(userId: String): Flow<List<JobApplication>> = callbackFlow {
        val subscription = userJobsCollection(userId).addSnapshotListener { snapshot, _ ->
            //only send the list if it is not from cache.
            if (snapshot != null && !snapshot.metadata.isFromCache) {
                val jobs = snapshot.toObjects<JobApplication>() ?: emptyList()
                trySend(jobs)
            }
        }
        awaitClose { subscription.remove() }
    }

    override suspend fun addJobApplication(jobApplication: JobApplication) {
        userJobsCollection(jobApplication.userId).document(jobApplication.jobApplicationId).set(jobApplication).await()
    }

    override suspend fun updateJobApplication(jobApplication: JobApplication) {
        addJobApplication(jobApplication)
    }

    override suspend fun deleteJobApplication(jobApplication: JobApplication) {
        userJobsCollection(jobApplication.userId).document(jobApplication.jobApplicationId).delete().await()
    }

    override fun getJobApplicationById(id: String): Flow<JobApplication?> = callbackFlow {
        val userId = auth.currentUser?.uid ?: "anonymous"
        val subscription = userJobsCollection(userId).document(id).addSnapshotListener { snapshot, _ ->
            val job = snapshot?.toObject(JobApplication::class.java)
            trySend(job)
        }
        awaitClose { subscription.remove() }
    }

    //no additional login for FireStore because we only want to query offline entries, so just return an empty flow
    override fun getUnsyncedJobApplications(userId: String): Flow<List<JobApplication>> {
        return flowOf(emptyList())
    }
}
