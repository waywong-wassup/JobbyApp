package com.jobapplicationapp.jobby.data

import android.util.Log.e
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.userProfileChangeRequest

/**
 * implementation of AuthRepository using Firebase.
 */

class FirebaseAuthRepository(
    private val auth: FirebaseAuth
    ) : AuthRepository {
    //converts firebase auth listener to Kotlin flow, flow emit the current user status when user logs in or not
    override val currentUser: Flow<FirebaseUser?> = callbackFlow {
        //initialize value immediately for faster experience
        trySend(auth.currentUser)

        //firebase auth listener, to listen and  receive any changes of the current user like when they logs in, sign up, or session expired.
        //push information to app immediate when user status change
        val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(authListener)

        //close the listener when flow is closed to prevent memory leaks
        awaitClose { auth.removeAuthStateListener(authListener) }
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): Result<Unit> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            val user = auth.currentUser
            val profileUpdates = userProfileChangeRequest {
                displayName = "$firstName $lastName"
            }
            user?.updateProfile(profileUpdates)?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            // If  email is already in use or the password is too weak, fail here
            Result.failure(e)

        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

}