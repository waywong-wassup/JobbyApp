package com.jobapplicationapp.jobby

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jobapplicationapp.jobby.ui.JobApplicationListScreen
import com.jobapplicationapp.jobby.ui.JobbyAppNavHost
import com.jobapplicationapp.jobby.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {

                    JobbyAppNavHost()
                }
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