package com.jobapplicationapp.jobby.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserDetailsBottomSheet(
    defaultFirstname: String,
    defaultLastName: String,
    onDismiss: () -> Unit,
    onSave: (firstName: String, lastName: String) -> Unit,
    initialSyncEnabled: Boolean,
    onSyncToggle: (enabled: Boolean) -> Unit,
    syncToggleEnabled: Boolean = true,
    onLogout: () -> Unit
) {
    var firstName by remember { mutableStateOf(defaultFirstname) }
    var lastName by remember { mutableStateOf(defaultLastName) }
    var syncEnabled by remember { mutableStateOf(initialSyncEnabled) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 32.dp, top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){
            Text(
                text = "Edit User Details",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, capitalization = KeyboardCapitalization.Words)
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name")},
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, capitalization = KeyboardCapitalization.Words)
            )

            Button (
                onClick = { onSave(firstName, lastName) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ){
                Text(text = "Save")
            }

            OutlinedButton(
                onClick = {
                    onDismiss()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(text = "Log Out")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Enable Cloud Sync",
                    color = if (syncToggleEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                )
                Switch(
                    checked = syncEnabled,
                    onCheckedChange = {
                        syncEnabled = it //update UI
                        onSyncToggle(it) //update viewmodel
                    },
                    enabled = syncToggleEnabled // Use the parameter here
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditUserDetailsBottomSheetPreview() {
    EditUserDetailsBottomSheet(
        defaultFirstname = "Harry",
        defaultLastName = "Potter",
        onDismiss = {},
        onSave = { _, _ -> },
        initialSyncEnabled = true,
        onSyncToggle = {},
        onLogout = {}
    )
}