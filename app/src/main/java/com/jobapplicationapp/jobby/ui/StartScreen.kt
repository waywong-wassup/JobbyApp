package com.jobapplicationapp.jobby.ui

import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import android.util.Log.e
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CustomCredential
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.jobapplicationapp.jobby.R
import com.jobapplicationapp.jobby.ui.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
fun StartScreen(
    authViewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onLoginSuccess: () -> Unit = {},
    onSkipLogin: (String, String) -> Unit = { _, _ -> }
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?> (null)}
    var isSignUpMode by remember { mutableStateOf(false) }
    var isGuestMode by remember { mutableStateOf(false) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        //top part with title and icon, which shows up in all modes.
        JobbyAppIcon(modifier = Modifier.size(80.dp))
        Text(
            text = "Jobby",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Your Job Search Buddy",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.height(48.dp))

        // display first name and last name if in sign up mode OR guest mode
        if (isSignUpMode || isGuestMode) {
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Email and password fields, only if not in guest mode
        if (!isGuestMode) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Error message display
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Action Button: Login, Create Account, or Start as Guest
        OutlinedButton(
            onClick = {
                when {
                    isGuestMode -> {
                        onSkipLogin(firstName, lastName)
                    }
                    isSignUpMode -> {
                        authViewModel.signUp(email, password, firstName, lastName) { result ->
                            if (result.isSuccess) {
                                onLoginSuccess()
                            } else {
                                errorMessage = result.exceptionOrNull()?.message ?: "Sign Up Failed"
                            }
                        }
                    }
                    else -> {
                        authViewModel.signIn(email, password) { result ->
                            if (result.isSuccess) onLoginSuccess()
                            else errorMessage = result.exceptionOrNull()?.message ?: "Login Failed"
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = when {
                    isGuestMode -> "Start as Guest"
                    isSignUpMode -> "Create Account"
                    else -> "Login"
                },
                fontSize = 16.sp,
                modifier = Modifier.padding(8.dp)
            )
        }

        // Toggle between modes
        if (!isGuestMode) {
            TextButton(onClick = { isSignUpMode = !isSignUpMode }) {
                Text(if (isSignUpMode) "Already have an account? Login" else "Don't have an account? Sign Up")
            }
        } else {
            TextButton(onClick = { isGuestMode = false }) {
                Text("Back to Login/Sign Up")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // OR Divider - only if not in guest mode
        if (!isGuestMode) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(
                    text = " OR ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Social Login
            LoginGoogleAccountField(authViewModel = authViewModel,onLoginSuccess = onLoginSuccess)

            Spacer(modifier = Modifier.height(16.dp))

            // Skip Login
            SkipLogin(onClick = { isGuestMode = true })
        }
    }
}

@Composable
fun SkipLogin(onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(
            text = "Continue without an account",
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun LoginGoogleAccountField(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit) {
    //to handle SignInWithGoogle
    val scope = rememberCoroutineScope() //for coroutine scope, asynchronous nature of sign in task
    val context = LocalContext.current //for credential manager to show pop up
    val googleWebClientId = stringResource(R.string.google_web_client_id)

    OutlinedButton(
        onClick = {
            scope.launch {
                try {
                    val credentialManager = CredentialManager.create(context)

                    //create the google option(using web client id)
                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(googleWebClientId)
                        .build()

                    //create the credential request
                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    //show the pop up
                    val result = credentialManager.getCredential(context, request)

                    //extract the token
                    val credential = result.credential
                    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken

                        //tell the viewModel to sign in
                        authViewModel.signInWithGoogle(idToken) { loginResult ->
                            if (loginResult.isSuccess) onLoginSuccess()
                        }
                    }
                } catch (e: Exception) {
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_google_logo),
                contentDescription = "Google Logo",
                modifier = Modifier.size(20.dp),
                tint = androidx.compose.ui.graphics.Color.Unspecified
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text("Sign in with Google", color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StartScreenPreview() {
    AppTheme {
        StartScreen()
    }
}
