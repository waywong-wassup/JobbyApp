package com.jobapplicationapp.jobby.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jobapplicationapp.jobby.data.model.Progress
import com.jobapplicationapp.jobby.viewmodel.JobApplicationUiState
import com.jobapplicationapp.jobby.viewmodel.JobApplicationViewModel
import androidx.compose.material3.IconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    jobApplicationViewModel: JobApplicationViewModel,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val jobUiState by jobApplicationViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Job Search Summary", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Applications",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
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

                if (jobs.isEmpty()) {
                    Box(
                        modifier = modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No Applications Yet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Add job applications to track your metrics, callback rates, and interview progress!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    val totalJobs = jobs.size
                    val applied = jobs.count { it.progress == Progress.APPLIED.progressPhase || it.progress == Progress.TOAPPLY.progressPhase }
                    val activeInterviews = jobs.count {
                        it.progress == Progress.INTERVIEW.progressPhase || it.progress == Progress.SCREENCALL.progressPhase
                    }
                    val offers = jobs.count {
                        it.progress == Progress.OFFERED.progressPhase || it.progress == Progress.ACCEPTED.progressPhase
                    }
                    val rejected = jobs.count { it.progress == Progress.REJECTED.progressPhase }
                    val ghosted = jobs.count { it.progress == Progress.GHOSTED.progressPhase }
                    val callbackRate = if (totalJobs > 0) (activeInterviews + offers) * 100 / totalJobs else 0

                    val salaries = jobs.mapNotNull { it.salary }.filter { it > 0 }

                    LazyColumn(
                        modifier = modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
//                        item {
//                            Text("Overview", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
//                        }

                        // Metric Cards Grid
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                MetricCard(
                                    title = "Total Applications",
                                    value = "$totalJobs",
                                    icon = Icons.Default.Description,
                                    containerColor = Color(0xFF2563EB), // Rich Cobalt Blue
                                    contentColor = Color.White,
                                    modifier = Modifier.weight(1f)
                                )
                                MetricCard(
                                    title = "Interviews",
                                    value = "$activeInterviews",
                                    icon = Icons.Default.VideoCall,
                                    containerColor = Color(0xFF6D28D9), // Rich Deep Purple
                                    contentColor = Color.White,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                MetricCard(
                                    title = "Offers",
                                    value = "$offers",
                                    icon = Icons.Default.EmojiEvents,
                                    containerColor = Color(0xFF059669), // Rich Emerald Green
                                    contentColor = Color.White,
                                    modifier = Modifier.weight(1f)
                                )
                                MetricCard(
                                    title = "Callback Rate",
                                    value = "$callbackRate%",
                                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                                    containerColor = Color(0xFFD97706), // Rich Warm Amber
                                    contentColor = Color.White,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                MetricCard(
                                    title = "Rejected",
                                    value = "$rejected",
                                    icon = Icons.Default.Cancel,
                                    containerColor = Color(0xFFE11D48), // Rich Crimson Rose
                                    contentColor = Color.White,
                                    modifier = Modifier.weight(1f)
                                )
                                MetricCard(
                                    title = "Ghosted",
                                    value = "$ghosted",
                                    icon = Icons.Default.VisibilityOff,
                                    containerColor = Color(0xFF475569), // Rich Dark Slate
                                    contentColor = Color.White,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        item {
                            ApplicationPieChartCard(
                                applied = applied,
                                interviews = activeInterviews,
                                offers = offers,
                                rejected = rejected,
                                ghosted = ghosted,
                                total = totalJobs
                            )
                        }

                        // Salary Insights Card
                        if (salaries.isNotEmpty()) {
                            item {
                                SalaryInsightsCard(salaries = salaries)
                            }
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
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor.copy(alpha = 0.9f)
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}

@Composable
fun ApplicationPieChartCard(
    applied: Int,
    interviews: Int,
    offers: Int,
    rejected: Int,
    ghosted: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    if (total == 0) return //exit function immediately when no jobs are found

    val slices = listOf(
        Triple("Applied", applied, Color(0xFF2563EB)),
        Triple("Interview", interviews, Color(0xFF6D28D9)),
        Triple("Offer", offers, Color(0xFF059669)),
        Triple("Rejected", rejected, Color(0xFFE11D48)),
        Triple("Ghosted", ghosted, Color(0xFF475569))
    ).filter { it.second > 0 } //only display a segment when the value is greater than 0

    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Application Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Left: Pie Chart Canvas
                Canvas(modifier = Modifier.size(130.dp)) {
                    var startAngle = -90f
                    slices.forEach { slice ->
                        val sweepAngle = (slice.second.toFloat() / total.toFloat()) * 360f
                        drawArc(
                            color = slice.third,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true
                        )
                        startAngle += sweepAngle
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Right: Legend
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    slices.forEach { slice ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(slice.third, shape = CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${slice.first} (${slice.second})",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SalaryInsightsCard(
    salaries: List<Long>,
    modifier: Modifier = Modifier
) {
    val avgSalary = salaries.average().toLong()
    val maxSalary = salaries.maxOrNull() ?: 0L

    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Salary Insights",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Average Salary Target", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = if (avgSalary > 0) "$${"%,d".format(avgSalary)}" else "$0",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Highest Listing", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = if (maxSalary > 0) "$${"%,d".format(maxSalary)}" else "$0",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
