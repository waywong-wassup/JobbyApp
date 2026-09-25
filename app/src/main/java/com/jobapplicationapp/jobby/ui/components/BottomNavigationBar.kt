package com.jobapplicationapp.jobby.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onNavigateToApplicationsScreen: () -> Unit,
    onNavigateToSummaryScreen: () -> Unit
) {
    //styling
    val itemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,            // Icon inside selected pill
        selectedTextColor = MaterialTheme.colorScheme.onPrimary,          // Selected label text
        indicatorColor = MaterialTheme.colorScheme.onPrimary,             // White pill background for selected item
        unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f), // Inverted muted icon
        unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)  // Inverted muted text
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary, // to match TopBar primary color
        tonalElevation = 0.dp,
        modifier = Modifier
            .padding(start = 64.dp, end = 64.dp, top = 16.dp, bottom = 16.dp)
            .height(76.dp)
            .clip(RoundedCornerShape(28.dp))
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.ListAlt, contentDescription = "Applications") },
            label = { Text("Applications") },
            selected = currentRoute?.contains("JobApplicationListScreenRoute") == true,
            onClick = onNavigateToApplicationsScreen,
            colors = itemColors,
            modifier = Modifier.padding(top = 8.dp)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Analytics, contentDescription = "Summary") },
            label = { Text("Summary") },
            selected = currentRoute?.contains("SummaryScreenRoute") == true,
            onClick = onNavigateToSummaryScreen,
            colors = itemColors,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}