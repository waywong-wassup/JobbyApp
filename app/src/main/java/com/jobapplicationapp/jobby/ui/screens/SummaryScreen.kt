package com.jobapplicationapp.jobby.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jobapplicationapp.jobby.data.model.JobApplication
import com.jobapplicationapp.jobby.data.model.Progress
import com.jobapplicationapp.jobby.viewmodel.JobApplicationUiState
import com.jobapplicationapp.jobby.viewmodel.JobApplicationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    jobApplicationViewModel: JobApplicationViewModel,
    modifier: Modifier = Modifier
) {
    val jobUiState by jobApplicationViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Job Search Summary", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        when (jobUiState) {
            is JobApplicationUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is JobApplicationUiState.Success -> {
                val jobs = (jobUiState as JobApplicationUiState.Success).jobApplications
                val totalJobs = jobs.size
                val activeInterviews = jobs.count {
                    it.progress == Progress.INTERVIEW.progressPhase || it.progress == Progress.SCREENCALL.progressPhase
                }
                val offers = jobs.count {
                    it.progress == Progress.OFFERED.progressPhase || it.progress == Progress.ACCEPTED.progressPhase
                }
                val rejected = jobs.count {
                    it.progress == Progress.REJECTED.progressPhase || it.progress == Progress.GHOSTED.progressPhase }

                LazyColumn(
                    modifier = modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text("Overview", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            MetricCard("Total Applications", "$totalJobs", Modifier.weight(1f))
                            MetricCard("Interviews", "$activeInterviews", Modifier.weight(1f))
                        }
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            MetricCard("Offers", "$offers", Modifier.weight(1f))
                            MetricCard("Callback Rate", if (totalJobs > 0) "${(activeInterviews + offers) * 100 / totalJobs}%" else "0%", Modifier.weight(1f))
                        }
                    }
                }
            }
            is JobApplicationUiState.Error -> {
                Text("Error loading summary")
            }
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
    }
}