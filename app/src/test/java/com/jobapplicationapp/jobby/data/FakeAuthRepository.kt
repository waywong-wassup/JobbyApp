package com.jobapplicationapp.jobby.data

import com.google.firebase.auth.FirebaseUser
import com.jobapplicationapp.jobby.data.remote.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeAuthRepository : AuthRepository {

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    override val currentUser: Flow<FirebaseUser?> = _currentUser.asStateFlow()

    var shouldReturnError = false
    var signInCalledWith: Pair<String, String>? = null
    var signUpCalledWith: SignUpParams? = null
    var googleSignInCalledWithToken: String? = null
    var signOutCalled = false

    data class SignUpParams(
        val email: String,
        val password: String,
        val firstName: String,
        val lastName: String
    )

    fun setCurrentUser(user: FirebaseUser?) {
        _currentUser.value = user
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<Unit> {
        signInCalledWith = Pair(email, password)
        return if (shouldReturnError) {
            Result.failure(Exception("Sign in failed"))
        } else {
            Result.success(Unit)
        }
    }

    override suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): Result<Unit> {
        signUpCalledWith = SignUpParams(email, password, firstName, lastName)
        return if (shouldReturnError) {
            Result.failure(Exception("Sign up failed"))
        } else {
            Result.success(Unit)
        }
    }

    override suspend fun signOut() {
        signOutCalled = true
        _currentUser.value = null
    }

    override suspend fun signInWithGoogle(idToken: String): Result<Unit> {
        googleSignInCalledWithToken = idToken
        return if (shouldReturnError) {
            Result.failure(Exception("Google sign in failed"))
        } else {
            Result.success(Unit)
        }
    }
}
