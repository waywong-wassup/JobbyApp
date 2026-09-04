package com.jobapplicationapp.jobby.ui


import app.cash.turbine.test
import com.jobapplicationapp.jobby.data.FakeUserRepository
import com.jobapplicationapp.jobby.data.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.test.runTest
import org.junit.Before

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    val testUser1 = User(1, "testFirstName", "testLastName")
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var userViewModel: UserViewModel

    @Before
    fun setup() {
        fakeUserRepository = FakeUserRepository()
        userViewModel = UserViewModel(fakeUserRepository,0)
    }

    @Test
    fun userViewModel_withNoUserLoaded_returnNull() {
        val result = userViewModel.changingUserDetails.value
        assertNull(result)
    }

    @Test
    fun userUiState_userNotFoundInDatabase_returnsErrorState() = runTest {
        val emptyViewModel = UserViewModel(fakeUserRepository, userId = 99)

        emptyViewModel.userUiState.test {
            assertEquals(UserUiState.Error, awaitItem())
        }
    }

    @Test
    fun updateUserDraft_withValidInput_userDraftUpdated() {
        userViewModel.saveUserToDatabase(testUser1)
        userViewModel.startEditingUser(testUser1)
        userViewModel.updateUserDraft(firstName = "newFirstName", lastName = "newLastName")
        val result = userViewModel.changingUserDetails.value
        assertEquals(User(1, "newFirstName", "newLastName"), result)
    }

    @Test
    fun updateUserDraft_noUserLoadedInitially_initialisedWithDefaultId() = runTest {
        assertNull(userViewModel.changingUserDetails.value)
        userViewModel.updateUserDraft(firstName = "newFirstName", lastName = "newLastName")
        val result = userViewModel.changingUserDetails.value
        assertNotNull(result)
        assertEquals(User(1, "newFirstName", "newLastName"), result)
    }

    @Test
    fun updateUserDraft_withEmptyNames_updatesSuccessfully() {
        userViewModel.startEditingUser(testUser1)
        userViewModel.updateUserDraft(firstName = "", lastName = "")
        val result = userViewModel.changingUserDetails.value
        assertEquals("", result?.firstName)
        assertEquals("", result?.lastName)
    }

    @Test
    fun saveUserDraft_insertNewUser_userAddedToRepository() = runTest {
        userViewModel.saveUserToDatabase(testUser1)
        fakeUserRepository.getCurrentUser(1).test {
            val user = awaitItem()
            assertEquals(testUser1, user)
        }
    }

    @Test
    fun saveUserDraft_insertExistingUser_userUpdatedInRepository() = runTest {
        userViewModel.saveUserToDatabase(testUser1)
        fakeUserRepository.getCurrentUser(1).test {
            val user = awaitItem()
            assertEquals(testUser1, user)
        }
        userViewModel.startEditingUser(testUser1)
        userViewModel.updateUserDraft(firstName = "newFirstName", lastName = "newLastName")
        userViewModel.saveUserDraft()
        fakeUserRepository.getCurrentUser(1).test {
            val user = awaitItem()
            assertEquals(User(1, "newFirstName", "newLastName"), user)
        }
    }


}