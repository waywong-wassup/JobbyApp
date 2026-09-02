package com.jobapplicationapp.jobby.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jobapplicationapp.jobby.R
import com.jobapplicationapp.jobby.data.JobApplication
import com.jobapplicationapp.jobby.data.User
import com.jobapplicationapp.jobby.ui.theme.AppTheme
import com.jobapplicationapp.jobby.ui.theme.onWarningContainerDark
import com.jobapplicationapp.jobby.ui.theme.onWarningContainerLight
import com.jobapplicationapp.jobby.ui.theme.warningContainerDark
import com.jobapplicationapp.jobby.ui.theme.warningContainerLight


@Composable
fun JobApplicationListScreen(
    viewModel: JobApplicationViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier,
    onEditClick: (Int) -> Unit = {},
    onAddClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            JobbyTopBar()
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
            {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Job Application"
                )
            }

        }
    ) { innerPadding ->
        when(uiState){
            is JobApplicationUiState.Loading -> {}
            is JobApplicationUiState.Success ->{
                JobApplicationList(
                    jobApplications = (uiState as JobApplicationUiState.Success).jobApplications,
                    onEditClick = onEditClick,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            is JobApplicationUiState.Error -> {
                Text(text = "Error loading job applications")
            }
        }
    }
}

@Composable
fun JobbyAppIcon(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier
            .padding(8.dp)
            .size(40.dp)
            .clip(CircleShape),
        painter = painterResource(R.drawable.app_icon),
        contentScale = ContentScale.Crop,
        contentDescription = stringResource(R.string.app_name),
    )
}

@Composable
fun JobbyAppUserName(user: User, modifier: Modifier = Modifier) {
    Text(
        text = "${user.firstName} ${user.lastName}",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobbyTopBar() {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                JobbyAppIcon()
                Column {
                    Text(
                        text = "Jobby",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    JobbyAppUserName(user = User.sampleUser)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
        )
    )
}


@Composable
fun JobApplicationList(
    jobApplications: List<JobApplication>,
    onEditClick: (Int) -> Unit,
    modifier: Modifier = Modifier
){
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(jobApplications.size) { i ->
            JobApplicationCard(
                jobApplication = jobApplications[i],
                onEditClick = { onEditClick(jobApplications[i].jobApplicationId) }
            )
        }
    }
}

@Composable
fun JobApplicationCard(
    jobApplication: JobApplication,
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit = {}
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        onClick = onEditClick
    ) {

        Box(modifier = Modifier.fillMaxWidth()) {
            val (containerColor, contentColor) = getProgressColor(jobApplication.progress)
            Surface(
                color = containerColor,
                shape = RoundedCornerShape(
                    bottomStart = 24.dp,
                    topEnd = 12.dp
                ),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .width(100.dp)
                    .height(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center){
                    Text(
                        text = jobApplication.progress,
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                //Job Title
                Text(
                    text = jobApplication.jobTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(end = 88.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //Company name
                    Text(
                        text = jobApplication.companyName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(16.dp))

                    //Location field,  check if location is not null, when null, don't render this at all
                    if (jobApplication.location!!.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            //display Home icon if it is a remote job, ignore cases
                            if (jobApplication.location?.trim()
                                    .equals("Remote", ignoreCase = true)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = jobApplication.location!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun getProgressColor(progress: String): Pair<Color, Color> {
    val isDark = isSystemInDarkTheme()
    return when (progress) {
        "Accepted" -> {
            Color(0xFF93C572) to MaterialTheme.colorScheme.onTertiaryContainer
        }
        "Offered" -> {
            MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.onTertiary
        }
        "Interview", "Screen Call" -> {
            if (isDark) warningContainerDark to onWarningContainerDark
            else warningContainerLight to onWarningContainerLight
        }
        "Rejected" -> {
            Color(0xFFFF746C) to MaterialTheme.colorScheme.onErrorContainer
        }
        "To Apply" -> {
            MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
        }
        else -> {

            MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        }
    }
}


@Preview
@Composable
fun JobApplicationCardPreview() {
    AppTheme{
    JobApplicationCard(jobApplication = JobApplication.sampleJobApplication[0]
    )}
}

//@Preview
@Composable
private fun JobApplicationListPreview() {
    JobApplicationList(jobApplications = JobApplication.sampleJobApplication, onEditClick = {})
}

//@Preview
@Composable
private fun JobbyTopBarPreview() {
    JobbyTopBar()
}

@Preview(showBackground = true, heightDp = 400)
@Composable
fun JobApplicationListScreenPreview() {
    val dummyViewModel = remember { JobApplicationViewModel(DummyJobRepository(), DummyUserRepository()) }
    AppTheme {
        JobApplicationListScreen(viewModel = dummyViewModel)
    }

}