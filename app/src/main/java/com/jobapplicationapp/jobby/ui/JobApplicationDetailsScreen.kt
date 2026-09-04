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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jobapplicationapp.jobby.R
import com.jobapplicationapp.jobby.data.JobApplication
import com.jobapplicationapp.jobby.data.Progress
import com.jobapplicationapp.jobby.data.User
import com.jobapplicationapp.jobby.ui.components.DeleteConfirmationDialog
import com.jobapplicationapp.jobby.ui.theme.AppTheme
import kotlinx.coroutines.flow.asStateFlow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobApplicationDetailsScreen(
    jobApplicationViewModel: JobApplicationViewModel,
    userViewModel: UserViewModel,
    onBackClick: () -> Unit = {},
    onDiscardClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onConfirmDelete: () -> Unit = {},
    onDismissDelete: () -> Unit = {}
) {

    val jobTitleFocusRequester = remember { FocusRequester() }
    val companyNameFocusRequester = remember { FocusRequester() }
    var jobTitleError by remember { mutableStateOf(false) }
    var companyNameError by remember { mutableStateOf(false) }
    var showDeleteDialog by remember {mutableStateOf(false)}

    //delete dialog to overlay on top of details screen
    if (showDeleteDialog) {
        DeleteConfirmationDialog (
            onConfirmDelete = {
                showDeleteDialog = false
                jobApplicationViewModel.deleteCurrentJobApplication()
                onConfirmDelete()
            },
            onDismissDelete = { showDeleteDialog = false}
        )
    }

    Scaffold(
        topBar = {
            JobApplicationDetailsTopBar(
                onBackClick = onBackClick,
                onDeleteIconClick = { showDeleteDialog = true
                })
        },
        bottomBar = {
            JobApplicationDetailsBottomBar(
                onDiscardClick = onDiscardClick,
                onSaveClick = {
                    val error = jobApplicationViewModel.validateInput()
                    when (error) {
                        JobApplicationViewModel.ValidationError.JOB_TITLE_REQUIRED -> {
                            jobTitleError = true
                            jobTitleFocusRequester.requestFocus()
                        }
                        JobApplicationViewModel.ValidationError.COMPANY_NAME_REQUIRED -> {
                            companyNameError = true
                            companyNameFocusRequester.requestFocus()
                        }
                        JobApplicationViewModel.ValidationError.NONE -> {
                            jobApplicationViewModel.saveJobApplicationChange()
                            onSaveClick()
                        }
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        JobApplicationDetailsForm(
            jobTitleFocusRequester = jobTitleFocusRequester,
            companyNameFocusRequester = companyNameFocusRequester,
            jobTitleError = jobTitleError,
            companyNameError = companyNameError,
            onJobTitleChange = { jobTitleError = it.isEmpty() },
            onCompanyNameChange = { companyNameError = it.isEmpty() },
            jobApplicationViewModel = jobApplicationViewModel,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobApplicationDetailsTopBar(
    onBackClick: () -> Unit = {},
    onDeleteIconClick: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.job_application_details),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_to_list),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        actions = {
            IconButton(onClick = {onDeleteIconClick()}
            )
            {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    )
}

@Composable
fun JobApplicationDetailsBottomBar(
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




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobApplicationDetailsForm(
    jobTitleFocusRequester: FocusRequester,
    companyNameFocusRequester: FocusRequester,
    jobTitleError: Boolean,
    companyNameError: Boolean,
    onJobTitleChange: (String) -> Unit,
    onCompanyNameChange: (String) -> Unit,
    jobApplicationViewModel: JobApplicationViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val job by jobApplicationViewModel.changingJobApplication.collectAsState()
    val currentJob = job ?: return
    val requiredFieldMessage = "Required"


    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FormSection(title = "Job Information") {
            OutlinedTextField(
                value = currentJob.jobTitle,
                isError = jobTitleError,
                supportingText = {
                    if (jobTitleError) {
                        Text(requiredFieldMessage)
                    }
                },
                onValueChange = {
                    jobApplicationViewModel.updateJobDetailFieldsUiStates { copy(jobTitle = it) }
                    onJobTitleChange(it)
                },
                label = { Text(stringResource(R.string.job_title)) },
                modifier = Modifier.fillMaxWidth()
                                    .focusRequester(jobTitleFocusRequester)
            )

            OutlinedTextField(
                value = currentJob.companyName,
                isError = companyNameError,
                supportingText = {
                    if (companyNameError) {
                        Text(requiredFieldMessage)
                    }
                },
                onValueChange = {
                    jobApplicationViewModel.updateJobDetailFieldsUiStates { copy(companyName = it) }
                    onCompanyNameChange(it)
                                },
                label = { Text(stringResource(R.string.company_name)) },
                modifier = Modifier.fillMaxWidth()
                    .focusRequester(companyNameFocusRequester)
            )
            OutlinedTextField(
                value = currentJob.location ?: "",
                onValueChange = { jobApplicationViewModel.updateJobDetailFieldsUiStates { copy(location = it) } },
                label = { Text(stringResource(R.string.location)) },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = currentJob.applicationURL ?: "",
                onValueChange = { jobApplicationViewModel.updateJobDetailFieldsUiStates { copy(applicationURL = it) } },
                label = { Text(stringResource(R.string.application_url)) },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = {  }) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "Open link"
                        )
                    }
                }
            )
        }


        FormSection(title = "Application Progress") {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.weight(1f)
                ){
                    OutlinedTextField(
                        value = currentJob.progress,
                        onValueChange = {  },
                        readOnly = true,
                        label = { Text(stringResource(R.string.progress)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        //link menu to text field
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        Progress.entries.forEach { progress ->
                            DropdownMenuItem(
                                text = { Text(progress.progressPhase) },
                                onClick = {
                                    jobApplicationViewModel.updateJobDetailFieldsUiStates { copy(progress = progress.progressPhase) }
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    // format to 10000 to 10,000 etc
                    value = currentJob.salary?.let { "%,d".format(it) } ?: "",
                    onValueChange = { input ->
                        val unformattedSalary = input.replace(",","")
                        if(unformattedSalary.isEmpty()){
                            jobApplicationViewModel.updateJobDetailFieldsUiStates { copy(salary = null) }
                        } else {
                            jobApplicationViewModel.updateJobDetailFieldsUiStates { copy(salary = unformattedSalary.toLong()) }
                        }
                    },
                    label = { Text(stringResource(R.string.salary)) },
                    modifier = Modifier.weight(1f),
                    prefix = { Text("$") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

                OutlinedTextField(
                    value = currentJob.applicationPostedDate ?: "",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null
                        )
                    },
                    onValueChange = { jobApplicationViewModel.updateJobDetailFieldsUiStates { copy(applicationPostedDate = it) } },
                    label = { Text(stringResource(R.string.posted_date), fontSize = 16.sp) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = currentJob.jobType ?: "",
                    onValueChange = { jobApplicationViewModel.updateJobDetailFieldsUiStates { copy(jobType = it) } },
                    label = { Text(stringResource(R.string.job_type)) },
                    modifier = Modifier.fillMaxWidth()
                )

        }

        FormSection(title = "Contact Information") {

                OutlinedTextField(
                    value = currentJob.contactName ?: "",
                    onValueChange = { jobApplicationViewModel.updateJobDetailFieldsUiStates { copy(contactName = it) } },
                    label = { Text(stringResource(R.string.contact_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                )
                OutlinedTextField(
                    value = currentJob.contactDetails ?: "",
                    onValueChange = { jobApplicationViewModel.updateJobDetailFieldsUiStates { copy(contactDetails = it) } },
                    label = { Text(stringResource(R.string.contact_details)) },
                    modifier = Modifier.fillMaxWidth()
                )

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




class DummyJobRepository : com.jobapplicationapp.jobby.data.JobApplicationRepository {
    override fun getAllJobApplications() = kotlinx.coroutines.flow.MutableStateFlow(emptyList<JobApplication>()).asStateFlow()
    override suspend fun addJobApplication(job: JobApplication) {}
    override suspend fun updateJobApplication(job: JobApplication) {}
    override suspend fun deleteJobApplication(job: JobApplication) {}
    override fun getJobApplicationById(id: Int) = kotlinx.coroutines.flow.flowOf(null)
}
class DummyUserRepository : com.jobapplicationapp.jobby.data.UserRepository {
    override suspend fun deleteUser(user: User) {}
    override suspend fun addUser(user: User) {}
    override fun getCurrentUser(userId: Int) = kotlinx.coroutines.flow.flowOf(null)
}

@Preview (showBackground = true)
@Composable
fun JobApplicationDetailsScreenPreview() {
    val dummyJobApplicationViewModel = remember {
        JobApplicationViewModel(DummyJobRepository()).apply {
            selectJob(JobApplication.sampleJobApplication[1])
        }
    }
    val dummyUserViewModel = remember {
        UserViewModel(DummyUserRepository(), 1)
    }

    AppTheme{
        JobApplicationDetailsScreen(jobApplicationViewModel = dummyJobApplicationViewModel, userViewModel = dummyUserViewModel)
    }
}

