package com.jobapplicationapp.jobby.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
object JobApplicationListScreenRoute
@Serializable
data class JobApplicationDetailsScreenRoute (val id: Int)

@Composable
fun JobbyAppNavHost(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = JobApplicationListScreenRoute,
        modifier = modifier
    ) {
        composable<JobApplicationListScreenRoute> {
            JobApplicationListScreen(
                onEditClick = { id -> navController.navigate(JobApplicationDetailsScreenRoute(id)) },
                onAddClick = {
                    navController.navigate(JobApplicationDetailsScreenRoute(0))
                }
            )
        }
        composable<JobApplicationDetailsScreenRoute> { backStack ->
            val details: JobApplicationDetailsScreenRoute = backStack.toRoute()

            val viewModel: JobApplicationViewModel = viewModel(
                factory = AppViewModelProvider.Factory
            )

            //LaunchedEffect, for running this code just once when this screen first appear
            LaunchedEffect(details.id) {
                viewModel.loadJobApplication(details.id)
            }

            JobApplicationDetailsScreen(
                jobApplicationViewModel = viewModel,
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

