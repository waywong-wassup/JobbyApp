package com.jobapplicationapp.jobby.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.jobapplicationapp.jobby.data.OFFLINE_USER_ID
import com.jobapplicationapp.jobby.data.User
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object StartScreenRoute
@Serializable
object JobApplicationListScreenRoute
@Serializable
data class JobApplicationDetailsScreenRoute (val id: String)

@Composable
fun JobbyAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory),
    jobApplicationViewModel: JobApplicationViewModel = viewModel(factory = AppViewModelProvider.Factory),
    userViewModel: UserViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val userUiState by authViewModel.currentUser.collectAsState()
    val localUserUiState by userViewModel.userUiState.collectAsState()
    val scope = rememberCoroutineScope()

    // first handle Firebase Login
    LaunchedEffect(userUiState) {
        if (userUiState != null) { 
            val uid = userUiState!!.uid
            jobApplicationViewModel.setUserId(uid)
            userViewModel.setUserId(uid)

            userViewModel.saveUserToDatabase( // then save this user to DB
                User(
                    userId = uid,
                    firstName = userUiState!!.displayName?.substringBefore(" ") ?: "New",
                    lastName = userUiState!!.displayName?.substringAfter(" ", "")?.ifBlank { "User" } ?: "User"
                )
            )
            // if has user logged in/signed in when app launch, skip StartScreen and go to ListScreen directly
            navController.navigate(JobApplicationListScreenRoute) {
                //and ensure StartScreen is not in backstack
                popUpTo(StartScreenRoute) { inclusive = true }
            }
        } else {
            // If no Firebase user, try to load the offline user
            jobApplicationViewModel.setUserId(OFFLINE_USER_ID)
            userViewModel.setUserId(OFFLINE_USER_ID)
        }
    }

    // handle Auto-Skip for Offline User
    LaunchedEffect(localUserUiState) {
        // If we found a local user (either Guest or Firebase) and we are currently on the Start screen
        if (localUserUiState is UserUiState.Success && 
            navController.currentDestination?.route?.contains("StartScreenRoute") == true) {
            
            navController.navigate(JobApplicationListScreenRoute) {
                popUpTo(StartScreenRoute) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (userUiState == null) StartScreenRoute else JobApplicationListScreenRoute, //initial launch destination
        modifier = modifier
    ) {
        composable<StartScreenRoute> {
            StartScreen(
                onLoginSuccess = {
                    navController.navigate(JobApplicationListScreenRoute) {
                        popUpTo(StartScreenRoute) { inclusive = true } // remove StartScreen from backstack, inclusive = true means including remove StartScreen
                    }
                },
                onSkipLogin = {
                    // set offline user id if user choose not to login
                    jobApplicationViewModel.setUserId(OFFLINE_USER_ID)
                    userViewModel.setUserId(OFFLINE_USER_ID)

                    scope.launch {
                        userViewModel.saveUserToDatabase(
                            User(User.guestUser.userId, User.guestUser.firstName, User.guestUser.lastName)
                        )
                    }

                    navController.navigate(JobApplicationListScreenRoute) {
                        popUpTo(StartScreenRoute) { inclusive = true }
                    }
                }
            )
        }
        composable<JobApplicationListScreenRoute> {
            JobApplicationListScreen(
                jobApplicationViewModel = jobApplicationViewModel,
                userViewModel = userViewModel,
                onEditClick = { id -> navController.navigate(JobApplicationDetailsScreenRoute(id)) },
                onAddClick = {
                    navController.navigate(JobApplicationDetailsScreenRoute("0"))
                }
            )
        }
        composable<JobApplicationDetailsScreenRoute> { backStack ->
            val details: JobApplicationDetailsScreenRoute = backStack.toRoute()

            //LaunchedEffect, for running this code just once when this screen first appear
            LaunchedEffect(details.id) {
                jobApplicationViewModel.loadJobApplication(details.id)
            }

            JobApplicationDetailsScreen(
                jobApplicationViewModel = jobApplicationViewModel,
                userViewModel = userViewModel,
                onBackClick = { navController.popBackStack() },
                onDiscardClick = { navController.popBackStack() },
                onSaveClick = {
                    navController.popBackStack()
                },
                onDeleteClick = {
                    navController.popBackStack()
                },
                onConfirmDelete = {
                    navController.popBackStack()
                }

            )
        }
    }
}

