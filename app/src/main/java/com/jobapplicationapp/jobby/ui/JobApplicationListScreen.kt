package com.jobapplicationapp.jobby.ui

import android.content.res.Resources
import androidx.compose.foundation.Image

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.rememberNavController
import com.jobapplicationapp.jobby.R
import com.jobapplicationapp.jobby.data.JobApplication
import com.jobapplicationapp.jobby.data.User
import com.jobapplicationapp.jobby.ui.theme.AppTheme


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
            containerColor = MaterialTheme.colorScheme.secondary,
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
fun JobApplicationCard(jobApplication: JobApplication, modifier: Modifier = Modifier, onEditClick: () -> Unit = {}) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.primaryContainer
//            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = jobApplication.jobTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = jobApplication.companyName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    // check if location is not null, when null, don't render this at all
                    if (jobApplication.location!!.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
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

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically

            ) {
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Text(
                        text = jobApplication.progress,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { onEditClick() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Job Application",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun JobApplicationCardPreview() {
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