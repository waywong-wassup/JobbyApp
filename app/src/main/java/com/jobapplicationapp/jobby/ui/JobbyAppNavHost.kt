package com.jobapplicationapp.jobby.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.jobapplicationapp.jobby.JobbyApplication
import com.jobapplicationapp.jobby.data.OFFLINE_USER_ID
import com.jobapplicationapp.jobby.data.User
import com.jobapplicationapp.jobby.ui.components.LoadingScreen
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
    val authUiState by authViewModel.authUiState.collectAsState()
    val localUserUiState by userViewModel.userUiState.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Wait until the initial auth check is finished
    if (authUiState is AuthUiState.Loading) {
        LoadingScreen()
        return
    }

    // handle Firebase Login status changes
    LaunchedEffect(authUiState) {
        if (authUiState is AuthUiState.Success) { 
            val user = (authUiState as AuthUiState.Success).user
            val uid = user.uid
            jobApplicationViewModel.setUserId(uid)
            userViewModel.setUserId(uid)

            // Start background sync
            (context.applicationContext as JobbyApplication).scheduleSync(uid)

            userViewModel.saveUserToDatabase( // then save this user to DB
                User(
                    userId = uid,
                    firstName = user.displayName?.substringBefore(" ") ?: "New",
                    lastName = user.displayName?.substringAfter(" ", "")?.ifBlank { "User" } ?: "User"
                )
            )
            
            // Only navigate if we are on the start screen to avoid duplicate screens
            if (navController.currentDestination?.route?.contains("StartScreenRoute") == true) {
                navController.navigate(JobApplicationListScreenRoute) {
                    //and ensure StartScreen is not in backstack
                    popUpTo(StartScreenRoute) { inclusive = true }
                }
            }
        } else if (authUiState is AuthUiState.Error) {
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
        startDestination = if (authUiState is AuthUiState.Success) JobApplicationListScreenRoute else StartScreenRoute, //initial launch destination
        modifier = modifier
    ) {
        composable<StartScreenRoute> {
            StartScreen(
                onLoginSuccess = {
                    navController.navigate(JobApplicationListScreenRoute) {
                        popUpTo(StartScreenRoute) { inclusive = true }
                    // remove StartScreen from backstack, inclusive = true means including remove StartScreen
                    }
                },
                onSkipLogin = { firstName, lastName ->
                    // set offline user id if user choose not to login
                    jobApplicationViewModel.setUserId(OFFLINE_USER_ID)
                    userViewModel.setUserId(OFFLINE_USER_ID)

                    scope.launch {
                        userViewModel.saveUserToDatabase(
                            User(
                                userId = OFFLINE_USER_ID,
                                firstName = firstName.ifBlank { "Offline" },
                                lastName = lastName.ifBlank { "User" }
                            )
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

