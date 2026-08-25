package com.jobapplicationapp.jobby.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jobapplicationapp.jobby.R
import com.jobapplicationapp.jobby.data.JobApplication
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.asStateFlow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobApplicationDetailsScreen(
    jobApplicationViewModel: JobApplicationViewModel,
    onBackClick: () -> Unit = {},
    onDiscardClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            JobApplicationDetailsTopBar(onBackClick = onBackClick)
        },
        bottomBar = {
            JobApplicationDetailsBottomBar(
                jobApplicationViewModel = jobApplicationViewModel,
                onDiscardClick = onDiscardClick,
                onSaveClick = onSaveClick
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        JobApplicationDetailsForm(
            jobApplicationViewModel = jobApplicationViewModel,
            modifier = Modifier.padding(innerPadding)
                .fillMaxSize()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobApplicationDetailsTopBar(onBackClick: () -> Unit = {}) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.job_application_details),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_to_list)
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFFDCD0FF)
        )
    )
}

@Composable
fun JobApplicationDetailsBottomBar(
    jobApplicationViewModel: JobApplicationViewModel,
    onDiscardClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    Surface(
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onDiscardClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = stringResource(R.string.discard))
            }
            Button(
                onClick = onSaveClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = stringResource(R.string.save))
            }
        }
    }
}




@Composable
fun JobApplicationDetailsForm(
    jobApplicationViewModel: JobApplicationViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val job by jobApplicationViewModel.changingJobApplication.collectAsState()
    val currentJob = job ?: return

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FormSection(title = "Job Information") {
            OutlinedTextField(
                value = currentJob.jobTitle,
                onValueChange = { jobApplicationViewModel.updateJobDetailFieldsUiStates { copy(jobTitle = it) } },
                label = { Text(stringResource(R.string.job_title)) },
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = currentJob.companyName,
                    onValueChange = { jobApplicationViewModel.updateJobDetailFieldsUiStates { copy(companyName = it) } },
                    label = { Text(stringResource(R.string.company_name)) },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = currentJob.location ?: "",
                    onValueChange = { jobApplicationViewModel.updateJobDetailFieldsUiStates { copy(location = it) } },
                    label = { Text(stringResource(R.string.location)) },
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) }
                )
            }
            OutlinedTextField(
                value = currentJob.applicationURL ?: "",
                onValueChange = { jobApplicationViewModel.updateJobDetailFieldsUiStates { copy(applicationURL = it) } },
                label = { Text(stringResource(R.string.application_url)) },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = {  }) {
                        Icon(
                            imageVector = Icons.Default.Info, //TODO - import openInNew vector
                            contentDescription = "Open link"
                        )
                    }
                }
            )
        }


        FormSection(title = "Application Progress") {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = currentJob.progress,
                    onValueChange = { jobApplicationViewModel.updateJobDetailFieldsUiStates { copy(progress = it) } },
                    label = { Text(stringResource(R.string.progress)) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = currentJob.salary?.toString() ?: "",
                    onValueChange = { jobApplicationViewModel.updateJobDetailFieldsUiStates { copy(salary = it.toLongOrNull()) } },
                    label = { Text(stringResource(R.string.salary)) },
                    modifier = Modifier.weight(1f),
                    prefix = { Text("$") }
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)){
                OutlinedTextField(
                    value = currentJob.applicationPostedDate ?: "",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null
                        )
                    },
                    onValueChange = { jobApplicationViewModel.updateJobDetailFieldsUiStates { copy(applicationPostedDate = it) } },
                    label = { Text(stringResource(R.string.posted_date)) },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = currentJob.jobType ?: "",
                    onValueChange = { jobApplicationViewModel.updateJobDetailFieldsUiStates { copy(jobType = it) } },
                    label = { Text(stringResource(R.string.job_type)) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        FormSection(title = "Contact Information") {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = currentJob.contactName ?: "",
                    onValueChange = { jobApplicationViewModel.updateJobDetailFieldsUiStates { copy(contactName = it) } },
                    label = { Text(stringResource(R.string.contact_name)) },
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                )
                OutlinedTextField(
                    value = currentJob.contactDetails ?: "",
                    onValueChange = { jobApplicationViewModel.updateJobDetailFieldsUiStates { copy(contactDetails = it) } },
                    label = { Text(stringResource(R.string.contact_details)) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        FormSection(title = "Additional Notes") {
            OutlinedTextField(
                value = currentJob.notes ?: "",
                onValueChange = { jobApplicationViewModel.updateJobDetailFieldsUiStates { copy(notes = it) } },
                label = { Text(stringResource(R.string.notes)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 4
            )
        }
    }
}

@Composable
fun FormSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        content() // the composable like text fields in a section
        Spacer(modifier = Modifier.height(8.dp))
        //HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
    }
}

class DummyRepository : com.jobapplicationapp.jobby.data.JobApplicationRepository {
    override fun getAllJobApplications() = kotlinx.coroutines.flow.MutableStateFlow(emptyList<JobApplication>()).asStateFlow()
    override suspend fun addJobApplication(job: JobApplication) {}
    override suspend fun updateJobApplication(job: JobApplication) {}
    override suspend fun deleteJobApplication(job: JobApplication) {}
    override fun getJobApplicationById(id: Int) = kotlinx.coroutines.flow.flowOf(null)
}

@Preview (showBackground = true)
@Composable
fun JobApplicationDetailsScreenPreview() {
    val dummyViewModel = remember {
        JobApplicationViewModel(DummyRepository()).apply {

            selectJob(JobApplication.sampleJobApplication[1])
        }
    }
    MaterialTheme {
        JobApplicationDetailsScreen(jobApplicationViewModel = dummyViewModel)
    }
}

