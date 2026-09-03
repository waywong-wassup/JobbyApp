package com.jobapplicationapp.jobby.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jobapplicationapp.jobby.ui.theme.AppTheme

@Composable
fun DeleteConfirmationDialog (
    modifier: Modifier = Modifier,
    onDismissDelete: () -> Unit = {},
    onConfirmDelete: () -> Unit = {}
){
        AlertDialog(
            onDismissRequest = onDismissDelete,
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text(text = "Confirm Delete?") },

            text = { Text("Are you sure you want to delete this job application? This action cannot be undone." ) },
            confirmButton = {
               Row {
                   Button(onClick = onDismissDelete,
                       modifier = Modifier.weight(1f)) {
                       Text(text = "Dismiss")
                   }
                   Spacer(modifier = Modifier.padding(16.dp))
                   Button(
                       onClick = onConfirmDelete,
                       modifier = Modifier.weight(1f)
                   ) {
                       Text(text = "Delete")
                   }
                }
            },
            dismissButton = null

        )
    }

@Preview(showBackground = true)
@Composable
fun DeleteConfirmationDialogPreview() {
   AppTheme {
       Box(
           modifier = Modifier.fillMaxSize(),
           contentAlignment = androidx.compose.ui.Alignment.Center
       ) {
           DeleteConfirmationDialog()
       }
   }
}