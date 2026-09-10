package com.jobapplicationapp.jobby.data

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

/**
 * interface for authentication operations
 * current it is wire up to Firebase
 */
interface AuthRepository {
    //check if user is logged in
    val currentUser: Flow<FirebaseUser?>

    //sign in with email and password, we are expecting the result if sign in is successful or not
    suspend fun signInWithEmailAndPassword(email: String, password: String) : Result<Unit>

    suspend fun signUpWithEmailAndPassword(email: String, password: String) : Result <Unit>

    // for sign out
    suspend fun signOut()
}