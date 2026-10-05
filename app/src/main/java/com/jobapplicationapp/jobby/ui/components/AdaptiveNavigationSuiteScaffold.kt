package com.jobapplicationapp.jobby.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType

@Composable
fun AdaptiveNavigationSuiteScaffold(
    currentRoute: String?,
    showNavigation: Boolean,
    onNavigateToApplicationsScreen: () -> Unit,
    onNavigateToSummaryScreen: () -> Unit,
    content: @Composable () -> Unit,
) {
    //styling
    val itemColors = NavigationSuiteDefaults.itemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
            indicatorColor = MaterialTheme.colorScheme.onPrimary,
            unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
            unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
        ),
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
            indicatorColor = MaterialTheme.colorScheme.onPrimary,
            unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
            unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
        )
    )

    val layoutType = if (showNavigation) {
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())
    } else {
        NavigationSuiteType.None
    }

    // Only apply topPadding when in NavigationRail, to minimise space when in Phone mode
    val topPadding = if (layoutType == NavigationSuiteType.NavigationRail) 60.dp else 0.dp

    NavigationSuiteScaffold(
        layoutType = layoutType,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = MaterialTheme.colorScheme.primary,
            navigationBarContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationRailContainerColor = MaterialTheme.colorScheme.primary,
            navigationRailContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        navigationSuiteItems = {
            if (showNavigation){
                item(
                    label = { Text("Applications") },
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.ListAlt,
                            contentDescription = "Applications"
                        )
                    },
                    selected = currentRoute?.contains("JobApplicationListScreenRoute") == true,
                    onClick = onNavigateToApplicationsScreen,
                    colors = itemColors,
                    modifier = Modifier.padding(top = topPadding)
                )
                item(
                    label = { Text("Summary") },
                    icon = {
                        Icon(
                            Icons.Default.Analytics,
                            contentDescription = "Summary"
                        )
                    },
                    selected = currentRoute?.contains("SummaryScreenRoute") == true,
                    onClick = onNavigateToSummaryScreen,
                    colors = itemColors
                )
            }
        },
        content = content
    )
//    NavigationBar(
//        containerColor = MaterialTheme.colorScheme.primary, // to match TopBar primary color
//        tonalElevation = 0.dp,
//        modifier = Modifier
//            .height(88.dp)
//    ) {
//        NavigationBarItem(
//            icon = { Icon(Icons.AutoMirrored.Filled.ListAlt, contentDescription = "Applications") },
//            label = { Text("Applications") },
//            selected = currentRoute?.contains("JobApplicationListScreenRoute") == true,
//            onClick = onNavigateToApplicationsScreen,
//            colors = itemColors,
//            modifier = Modifier.padding(top = 8.dp)
//        )
//        NavigationBarItem(
//            icon = { Icon(Icons.Default.Analytics, contentDescription = "Summary") },
//            label = { Text("Summary") },
//            selected = currentRoute?.contains("SummaryScreenRoute") == true,
//            onClick = onNavigateToSummaryScreen,
//            colors = itemColors,
//            modifier = Modifier.padding(top = 8.dp)
//        )
//    }
}