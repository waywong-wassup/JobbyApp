package com.jobapplicationapp.jobby.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.FlowPreview
import com.jobapplicationapp.jobby.R
import com.jobapplicationapp.jobby.data.JobApplication
import com.jobapplicationapp.jobby.data.User


@Composable
fun JobApplicationListScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            JobbyTopBar()
        }
    ) { innerPadding ->
        JobApplicationList(
            jobApplications = JobApplication.sampleJobApplication, //placeholder data
            modifier = Modifier.padding(innerPadding)
        )
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
        text = user.firstName
    )
    Text(
        text = " "
    )
    Text(
        text = user.lastName
    )
}

@Composable
fun JobbyTopBar(){
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFDCD0FF))
    ){
        Text(
            text = "Jobby",
            modifier = Modifier
                .padding(start = 8.dp, top = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            JobbyAppIcon()
            JobbyAppUserName(user = User.sampleUser,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1F)
            )
        }
    }
}


@Composable
fun JobApplicationList(
    jobApplications: List<JobApplication>,
    modifier: Modifier = Modifier
){
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement =  Arrangement.spacedBy(16.dp)
    ){
        items(jobApplications.size){ index ->
            JobApplicationCard(
                jobApplication = jobApplications[index]
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobApplicationCard(jobApplication: JobApplication, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    ) {
        Row(modifier = Modifier.padding(8.dp)){
            Column() {
                Text(
                    text = jobApplication.jobTitle,
                )
                Card {
                    Row{
                        Text(
                            text = jobApplication.companyName
                        )
                        if(jobApplication.location != null){
                            Text(
                                text = " • ${jobApplication.location}"
                            )
                        }

                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))

            Column(modifier = Modifier
                .padding(8.dp)
                .wrapContentSize()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = jobApplication.progress,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    IconButton(
                        onClick = { /* Action */ },
                        modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Job Application",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

//@Preview
@Composable
private fun JobApplicationCardPreview() {
    JobApplicationCard(jobApplication = JobApplication.sampleJobApplication[0]
    )
}

//@Preview
@Composable
private fun JobApplicationListPreview() {
    JobApplicationList(jobApplications = JobApplication.sampleJobApplication)
}

//@Preview
@Composable
private fun JobbyTopBarPreview() {
    JobbyTopBar()
}

@Preview
@Composable
fun JobApplicationListScreenPreview() {
    JobApplicationListScreen()
}