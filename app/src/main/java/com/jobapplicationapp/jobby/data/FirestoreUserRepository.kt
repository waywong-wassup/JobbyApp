package com.jobapplicationapp.jobby.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreUserRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : UserRepository {

    private val usersCollection = firestore.collection("users")

    override suspend fun deleteUser(user: User) {
        usersCollection.document(user.userId).delete().await()
    }

    override suspend fun addUser(user: User) {
        usersCollection.document(user.userId).set(user).await()
    }

    override fun getCurrentUser(userId: String): Flow<User?> = callbackFlow {
        val subscription = usersCollection.document(userId).addSnapshotListener { snapshot, _ ->
            val user = snapshot?.toObject(User::class.java)
            trySend(user)
        }
        awaitClose { subscription.remove() }
    }
}
