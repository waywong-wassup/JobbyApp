package com.jobapplicationapp.jobby.viewmodel

import app.cash.turbine.test
import com.google.firebase.auth.FirebaseUser
import com.jobapplicationapp.jobby.data.FakeAuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var viewModel: AuthViewModel
    private val mockFirebaseUser: FirebaseUser = mock(FirebaseUser::class.java) //use Mockito to create a mock FirebaseUser (which is a third-party SDK class)

    @Before
    fun setup() {
        fakeAuthRepository = FakeAuthRepository()
        viewModel = AuthViewModel(fakeAuthRepository)
    }

    @Test
    fun authUiState_whenUserIsNull_emitsErrorState() = runTest {
        viewModel.authUiState.test {
            val item = awaitItem()
            assertEquals(AuthUiState.Error, item)
        }
    }

    @Test
    fun authUiState_whenUserIsLoggedIn_emitsSuccessState() = runTest {
        fakeAuthRepository.setCurrentUser(mockFirebaseUser)
        viewModel.authUiState.test {
            val item = awaitItem()
            assertTrue(item is AuthUiState.Success)
            assertEquals(mockFirebaseUser, (item as AuthUiState.Success).user)
        }
    }

    @Test
    fun currentUser_whenNotLoggedIn_emitsNull() = runTest {
        viewModel.currentUser.test {
            val user = awaitItem()
            assertNull(user)
        }
    }

    @Test
    fun currentUser_whenLoggedIn_emitsFirebaseUser() = runTest {
        fakeAuthRepository.setCurrentUser(mockFirebaseUser)
        viewModel.currentUser.test {
            val user = awaitItem()
            assertEquals(mockFirebaseUser, user)
        }
    }

    @Test
    fun signIn_successful_callsRepositoryAndReturnsSuccessResult() = runTest {
        var resultReceived: Result<Unit>? = null

        viewModel.signIn("test@example.com", "password123") { result ->
            resultReceived = result
        }

        assertEquals("test@example.com", fakeAuthRepository.signInCalledWith?.first)
        assertEquals("password123", fakeAuthRepository.signInCalledWith?.second)
        assertTrue(resultReceived?.isSuccess == true)
    }

    @Test
    fun signIn_failure_returnsFailureResult() = runTest {
        fakeAuthRepository.shouldReturnError = true
        var resultReceived: Result<Unit>? = null

        viewModel.signIn("test@example.com", "wrongpassword") { result ->
            resultReceived = result
        }

        assertTrue(resultReceived?.isFailure == true)
    }

    @Test
    fun signUp_successful_callsRepositoryAndReturnsSuccessResult() = runTest {
        var resultReceived: Result<Unit>? = null

        viewModel.signUp("new@example.com", "pass1234", "John", "Doe") { result ->
            resultReceived = result
        }

        val signUpParams = fakeAuthRepository.signUpCalledWith
        assertEquals("new@example.com", signUpParams?.email)
        assertEquals("pass1234", signUpParams?.password)
        assertEquals("John", signUpParams?.firstName)
        assertEquals("Doe", signUpParams?.lastName)
        assertTrue(resultReceived?.isSuccess == true)
    }

    @Test
    fun signUp_failure_returnsFailureResult() = runTest {
        fakeAuthRepository.shouldReturnError = true
        var resultReceived: Result<Unit>? = null

        viewModel.signUp("new@example.com", "pass1234", "John", "Doe") { result ->
            resultReceived = result
        }

        assertTrue(resultReceived?.isFailure == true)
    }

    @Test
    fun signOut_callsRepositorySignOutAndClearsUser() = runTest {
        fakeAuthRepository.setCurrentUser(mockFirebaseUser)

        viewModel.signOut()

        assertTrue(fakeAuthRepository.signOutCalled)
        viewModel.currentUser.test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun signInWithGoogle_successful_callsRepositoryAndReturnsSuccessResult() = runTest {
        var resultReceived: Result<Unit>? = null

        viewModel.signInWithGoogle("google_id_token_123") { result ->
            resultReceived = result
        }

        assertEquals("google_id_token_123", fakeAuthRepository.googleSignInCalledWithToken)
        assertTrue(resultReceived?.isSuccess == true)
    }

    @Test
    fun signInWithGoogle_failure_returnsFailureResult() = runTest {
        fakeAuthRepository.shouldReturnError = true
        var resultReceived: Result<Unit>? = null

        viewModel.signInWithGoogle("invalid_token") { result ->
            resultReceived = result
        }

        assertTrue(resultReceived?.isFailure == true)
    }
}
