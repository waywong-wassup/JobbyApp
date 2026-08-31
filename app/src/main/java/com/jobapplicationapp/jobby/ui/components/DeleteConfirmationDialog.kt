package com.jobapplicationapp.jobby.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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
                Button(onClick = onConfirmDelete) {
                    Text(text = "Delete")
                }
            },
            dismissButton = {
                Button(onClick = onDismissDelete) {
                    Text(text = "Dismiss")
                }
            }
        )
    }