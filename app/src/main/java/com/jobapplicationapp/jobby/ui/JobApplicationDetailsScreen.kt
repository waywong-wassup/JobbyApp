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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jobapplicationapp.jobby.R
import com.jobapplicationapp.jobby.data.JobApplication
import com.jobapplicationapp.jobby.data.Progress
import com.jobapplicationapp.jobby.data.User
import com.jobapplicationapp.jobby.ui.components.DeleteConfirmationDialog
import com.jobapplicationapp.jobby.ui.theme.AppTheme
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import androidx.compose.material3.DatePicker
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.util.Locale.getDefault
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.input.KeyboardCapitalization
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf


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
    var showDeleteDialog by remember { mutableStateOf(false) }
    var datePickerTarget by remember { mutableStateOf<String?>(null) }
    val datePickerState = rememberDatePickerState()

    //delete dialog to overlay on top of details screen
    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onConfirmDelete = {
                showDeleteDialog = false
                jobApplicationViewModel.deleteCurrentJobApplication()
                onConfirmDelete()
            },
            onDismissDelete = { showDeleteDialog = false }
        )
    }

    if (datePickerTarget != null) {
        DatePickerDialog(
            onDismissRequest = { datePickerTarget = null }, // Clear target on close
            confirmButton = {
                TextButton(onClick = {
                    val selectedDateInMillis = datePickerState.selectedDateMillis
                    if (selectedDateInMillis != null) {
                        val dateStr = SimpleDateFormat("ddMMyyyy", getDefault())
                            .format(java.util.Date(selectedDateInMillis))

                        // The "Switcher" logic:
                        jobApplicationViewModel.updateJobDetailFieldsUiStates {
                            when (datePickerTarget) {
                                "appliedDate" -> copy(appliedDate = dateStr)
                                "postedDate" -> copy(applicationPostedDate = dateStr)
                                else -> this
                            }
                        }
                    }
                    datePickerTarget = null // Close the dialog
                }) { Text("Confirm") }
            }
        ) { DatePicker(state = datePickerState) }
    }
    Scaffold(
        topBar = {
            JobApplicationDetailsTopBar(
                onBackClick = onBackClick,
                onDeleteIconClick = {
                    showDeleteDialog = true
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
            onDatePickerClick = { datePickerTarget = it },
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
            IconButton(onClick = { onDeleteIconClick() }
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
    onDatePickerClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val job by jobApplicationViewModel.changingJobApplication.collectAsState()
    val currentJob = job ?: return
    val requiredFieldMessage = "Required"
    val uriHandler = LocalUriHandler.current

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
                supportingText = if (jobTitleError) {
                    { Text(requiredFieldMessage) } // show the text only when there's an error
                } else {
                    null
                },// to not reserve any space
                onValueChange = {
                    jobApplicationViewModel.updateJobDetailFieldsUiStates { copy(jobTitle = it) }
                    onJobTitleChange(it)
                },
                label = { Text(stringResource(R.string.job_title)) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(jobTitleFocusRequester)
                    .testTag("jobTitleError")
            )

            OutlinedTextField(
                value = currentJob.companyName,
                isError = companyNameError,
                supportingText = if (companyNameError) {
                    { Text(requiredFieldMessage) }
                } else {
                    null
                },
                onValueChange = {
                    jobApplicationViewModel.updateJobDetailFieldsUiStates { copy(companyName = it) }
                    onCompanyNameChange(it)
                },
                label = { Text(stringResource(R.string.company_name)) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(companyNameFocusRequester)
                    .testTag("companyNameError")
            )

            OutlinedTextField(
                value = currentJob.location ?: "",
                onValueChange = {
                    jobApplicationViewModel.updateJobDetailFieldsUiStates {
                        copy(
                            location = it
                        )
                    }
                },
                label = { Text(stringResource(R.string.location)) },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = currentJob.applicationURL ?: "",
                onValueChange = {
                    jobApplicationViewModel.updateJobDetailFieldsUiStates {
                        copy(
                            applicationURL = it
                        )
                    }
                },
                label = { Text(stringResource(R.string.application_url)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = {
                        val url = currentJob.applicationURL
                        if (!url.isNullOrBlank()) {
                            val fullUrl =
                                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                    "https://$url"
                                } else {
                                    url
                                }
                            uriHandler.openUri(fullUrl)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = "Open Link"
                        )
                    }

                }
            )
            OutlinedTextField(
                value = currentJob.appliedDate ?: "",
                onValueChange = { input ->
                    val cleanInput = input.filter { it.isDigit() }
                    if (cleanInput.length <= 8) {
                        jobApplicationViewModel.updateJobDetailFieldsUiStates {
                            copy(appliedDate = cleanInput)
                        }
                    }
                },
                label = { Text(stringResource(R.string.applied_date)) },
                placeholder = { Text("dd/mm/yyyy") },
                trailingIcon = {
                    IconButton(onClick = { onDatePickerClick("appliedDate") }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Open Calendar")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ), // Show number pad
                visualTransformation = DateTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                // format to 10000 to 10,000 etc
                value = currentJob.salary?.let { "%,d".format(it) } ?: "",
                onValueChange = { input ->
                    val unformattedSalary = input.replace(",", "")
                    if (unformattedSalary.isEmpty()) {
                        jobApplicationViewModel.updateJobDetailFieldsUiStates { copy(salary = null) }
                    } else {
                        jobApplicationViewModel.updateJobDetailFieldsUiStates { copy(salary = unformattedSalary.toLong()) }
                    }
                },
                label = { Text(stringResource(R.string.salary)) },
                modifier = Modifier.fillMaxWidth(),
                prefix = { Text("$") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

        }


        FormSection(title = "Application Progress") {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = currentJob.progress,
                        onValueChange = { },
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
                                    jobApplicationViewModel.updateJobDetailFieldsUiStates {
                                        copy(
                                            progress = progress.progressPhase
                                        )
                                    }
                                    expanded = false
                                }
                            )
                        }
                    }
                }

            }

            OutlinedTextField(
                value = currentJob.applicationPostedDate ?: "",
                onValueChange = { input ->
                    val cleanInput = input.filter { it.isDigit() }
                    if (cleanInput.length <= 8) {
                        jobApplicationViewModel.updateJobDetailFieldsUiStates {
                            copy(applicationPostedDate = cleanInput)
                        }
                    }
                },
                label = { Text(stringResource(R.string.posted_date)) },
                placeholder = { Text("dd/mm/yyyy") },
                trailingIcon = {
                    IconButton(onClick = { onDatePickerClick("postedDate") }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Open Calendar")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                visualTransformation = DateTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = currentJob.jobType ?: "",
                onValueChange = {
                    jobApplicationViewModel.updateJobDetailFieldsUiStates {
                        copy(
                            jobType = it
                        )
                    }
                },
                label = { Text(stringResource(R.string.job_type)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

        }

        FormSection(title = "Contact Information") {

            OutlinedTextField(
                value = currentJob.contactName ?: "",
                onValueChange = {
                    jobApplicationViewModel.updateJobDetailFieldsUiStates {
                        copy(
                            contactName = it
                        )
                    }
                },
                label = { Text(stringResource(R.string.contact_name)) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
            )
            OutlinedTextField(
                value = currentJob.contactDetails ?: "",
                onValueChange = {
                    jobApplicationViewModel.updateJobDetailFieldsUiStates {
                        copy(
                            contactDetails = it
                        )
                    }
                },
                label = { Text(stringResource(R.string.contact_details)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

        }

        FormSection(title = "Additional Notes") {
            OutlinedTextField(
                value = currentJob.notes ?: "",
                onValueChange = { jobApplicationViewModel.updateJobDetailFieldsUiStates { copy(notes = it) } },
                label = { Text(stringResource(R.string.notes)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
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
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        content() // the composable like text fields in a section
    }
}

/**
 * To transform the manually entered date field to 'dd/MM/yyyy'
 */
class DateTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // Take the raw text (e.g., "20092026")
        val input = text.text
        var out = ""

        // Add slashes at the right spots
        for (i in input.indices) {
            out += input[i]
            if (i == 1 || i == 3) out += "/"
        }

        // This part tells Compose how to move the cursor correctly
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 3) return offset + 1
                if (offset <= 8) return offset + 2
                return 10
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset - 1
                if (offset <= 10) return offset - 2
                return 8
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}


class DummyJobRepository : com.jobapplicationapp.jobby.data.JobApplicationRepository {
    override fun getAllJobApplications(userId: String) =
        MutableStateFlow(emptyList<JobApplication>()).asStateFlow()

    override suspend fun addJobApplication(job: JobApplication) {}
    override suspend fun updateJobApplication(job: JobApplication) {}
    override suspend fun deleteJobApplication(job: JobApplication) {}
    override fun getJobApplicationById(id: String) = flowOf(null)
    override fun getUnsyncedJobApplications(userId: String) =
       MutableStateFlow(emptyList<JobApplication>()).asStateFlow()
}

class DummyUserRepository : com.jobapplicationapp.jobby.data.UserRepository {
    override suspend fun deleteUser(user: User) {}
    override suspend fun addUser(user: User) {}
    override fun getCurrentUser(userId: String) = flowOf(null)
}

class DummyUserPreferencesRepository : com.jobapplicationapp.jobby.data.UserPreferencesRepository {
    override val isSyncEnabled: Flow<Boolean> = flowOf(false)

    override suspend fun setIsSyncEnabled(isSyncEnabled: Boolean) {}
}

@Preview(showBackground = true)
@Composable
fun JobApplicationDetailsScreenPreview() {
    val dummyJobApplicationViewModel = remember {
        JobApplicationViewModel(DummyJobRepository()).apply {
            selectJob(JobApplication.sampleJobApplication[1])
        }
    }
    val dummyUserViewModel = remember {
        UserViewModel(
            userRepository = DummyUserRepository(),
            jobApplicationRepository = DummyJobRepository(),
            userPreferencesRepository = DummyUserPreferencesRepository()
        )
    }

    AppTheme {
        JobApplicationDetailsScreen(
            jobApplicationViewModel = dummyJobApplicationViewModel,
            userViewModel = dummyUserViewModel
        )
    }
}

