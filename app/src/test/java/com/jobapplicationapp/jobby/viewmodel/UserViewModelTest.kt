package com.jobapplicationapp.jobby.viewmodel


import app.cash.turbine.test
import com.jobapplicationapp.jobby.data.FakeJobApplicationRepository
import com.jobapplicationapp.jobby.data.FakeUserRepository
import com.jobapplicationapp.jobby.data.model.User
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

    val testUser1 = User("1", "testFirstName", "testLastName")
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var fakeJobRepository: FakeJobApplicationRepository
    private lateinit var userViewModel: UserViewModel

    @Before
    fun setup() {
        fakeUserRepository = FakeUserRepository()
        fakeJobRepository = FakeJobApplicationRepository()
        userViewModel = UserViewModel(fakeUserRepository, fakeJobRepository, FakeUserPreferencesRepository())
        userViewModel.setUserId("1")
    }

    @Test
    fun userViewModel_withNoUserLoaded_returnNull() {
        val result = userViewModel.changingUserDetails.value
        assertNull(result)
    }

    @Test
    fun userUiState_userNotFoundInDatabase_returnsErrorState() = runTest {
        val emptyViewModel = UserViewModel(fakeUserRepository, fakeJobRepository, FakeUserPreferencesRepository())
        emptyViewModel.setUserId("99")

        emptyViewModel.userUiState.test {
            assertEquals(UserUiState.Error, awaitItem())
        }
    }

    @Test
    fun updateUserDraft_withValidInput_userDraftUpdated() {
        runTest {
            userViewModel.saveUserToDatabase(testUser1)
        }
        userViewModel.startEditingUser(testUser1)
        userViewModel.updateUserDraft(firstName = "newFirstName", lastName = "newLastName")
        val result = userViewModel.changingUserDetails.value
        assertNotNull(result)
        assertEquals("1", result?.userId)
        assertEquals("newFirstName", result?.firstName)
        assertEquals("newLastName", result?.lastName)
    }

    @Test
    fun updateUserDraft_noUserLoadedInitially_initialisedWithCurrentId() = runTest {
        assertNull(userViewModel.changingUserDetails.value)
        userViewModel.updateUserDraft(firstName = "newFirstName", lastName = "newLastName")
        val result = userViewModel.changingUserDetails.value
        assertNotNull(result)
        assertEquals("1", result?.userId)
        assertEquals("newFirstName", result?.firstName)
        assertEquals("newLastName", result?.lastName)
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
        fakeUserRepository.getCurrentUser("1").test {
            val user = awaitItem()
            assertEquals(testUser1, user)
        }
    }

    @Test
    fun saveUserDraft_insertExistingUser_userUpdatedInRepository() = runTest {
        userViewModel.saveUserToDatabase(testUser1)
        fakeUserRepository.getCurrentUser("1").test {
            val user = awaitItem()
            assertEquals(testUser1, user)
        }
        userViewModel.startEditingUser(testUser1)
        userViewModel.updateUserDraft(firstName = "newFirstName", lastName = "newLastName")
        userViewModel.saveUserDraft()
        fakeUserRepository.getCurrentUser("1").test {
            val user = awaitItem()
            assertNotNull(user)
            assertEquals("1", user?.userId)
            assertEquals("newFirstName", user?.firstName)
            assertEquals("newLastName", user?.lastName)
        }
    }
}

class FakeUserPreferencesRepository : com.jobapplicationapp.jobby.data.repository.UserPreferencesRepository {
    override val isSyncEnabled: kotlinx.coroutines.flow.Flow<Boolean> = kotlinx.coroutines.flow.flowOf(false)
    override suspend fun setIsSyncEnabled(isSyncEnabled: Boolean) {}
}
