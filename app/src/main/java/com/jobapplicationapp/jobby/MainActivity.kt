package com.jobapplicationapp.jobby

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.jobapplicationapp.jobby.data.model.OFFLINE_USER_ID
import com.jobapplicationapp.jobby.ui.screens.JobApplicationListScreen
import com.jobapplicationapp.jobby.ui.JobbyAppNavHost
import com.jobapplicationapp.jobby.ui.theme.AppTheme
import com.jobapplicationapp.jobby.viewmodel.AppViewModelProvider
import com.jobapplicationapp.jobby.viewmodel.AuthUiState
import com.jobapplicationapp.jobby.viewmodel.AuthViewModel
import com.jobapplicationapp.jobby.viewmodel.UserUiState
import com.jobapplicationapp.jobby.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels { AppViewModelProvider.Factory }
    private val userViewModel: UserViewModel by viewModels { AppViewModelProvider.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Check firebase auth first
        // If no Firebase user, trigger offline Room user loading
        lifecycleScope.launch {
            authViewModel.authUiState.collect { authState ->
                if (authState is AuthUiState.Error) {
                    userViewModel.setUserId(OFFLINE_USER_ID)
                }
            }
        }

        // Hold splash screen until BOTH Auth and Room offline user checks complete
        splashScreen.setKeepOnScreenCondition {
            val authState = authViewModel.authUiState.value
            val userState = userViewModel.userUiState.value

            when (authState) {
                is AuthUiState.Loading -> true
                is AuthUiState.Success -> false
                // if Auth user not found, then splashScreen state depends on userState
                // if UserUiState = Loading, then splashScreen persist
                // if UserUiState != Loading, then splashScreen dismiss
                is AuthUiState.Error -> userState is UserUiState.Loading
            }
        }

        enableEdgeToEdge()
        setContent {
            AppTheme {
                JobbyAppNavHost(
                    authViewModel = authViewModel,
                    userViewModel = userViewModel
                )
            }
        }
    }
}

/**
 * Composable for app
 */
@Composable
fun JobbyApp(modifier: Modifier = Modifier) {
    JobApplicationListScreen(modifier = modifier)
}