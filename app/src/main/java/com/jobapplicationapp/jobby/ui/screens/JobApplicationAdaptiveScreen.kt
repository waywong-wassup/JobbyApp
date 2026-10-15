package com.jobapplicationapp.jobby.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jobapplicationapp.jobby.viewmodel.JobApplicationViewModel
import com.jobapplicationapp.jobby.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun JobApplicationAdaptiveScreen(
    jobApplicationViewModel: JobApplicationViewModel,
    userViewModel: UserViewModel,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val baseDirective = calculatePaneScaffoldDirective(currentWindowAdaptiveInfoV2())
    //custom directive to fix issue where text field getting focus automatically when change screen
    val customDirectiveDisableAutoFocus = PaneScaffoldDirective(
        maxHorizontalPartitions = baseDirective.maxHorizontalPartitions,
        horizontalPartitionSpacerSize = baseDirective.horizontalPartitionSpacerSize,
        maxVerticalPartitions = baseDirective.maxVerticalPartitions,
        verticalPartitionSpacerSize = baseDirective.verticalPartitionSpacerSize,
        defaultPanePreferredWidth = baseDirective.defaultPanePreferredWidth,
        defaultPanePreferredHeight = baseDirective.defaultPanePreferredHeight,
        excludedBounds = baseDirective.excludedBounds,
        shouldAutoFocusCurrentDestination = false
    )
    val navigator = rememberListDetailPaneScaffoldNavigator<String>(scaffoldDirective = customDirectiveDisableAutoFocus)
    val currentSelection = navigator.currentDestination?.contentKey

    LaunchedEffect(currentSelection) {
        keyboardController?.hide()
        focusManager.clearFocus()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        NavigableListDetailPaneScaffold(
            navigator = navigator,
            listPane = {
                AnimatedPane {
                    JobApplicationListScreen(
                        jobApplicationViewModel = jobApplicationViewModel,
                        userViewModel = userViewModel,
                        onEditClick = { jobApplicationId ->
                            scope.launch {
                                navigator.navigateTo(
                                    ListDetailPaneScaffoldRole.Detail,
                                    jobApplicationId
                                )
                            }
                        },
                        onAddClick = {
                            scope.launch {
                                navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, "0")
                            }
                        },
                        onLogout = onLogout
                    )
                }
            },
            detailPane = {
                AnimatedPane {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        val selectedId = navigator.currentDestination?.contentKey

                        if (selectedId == null) {
                            EmptyJobDetailPlaceholder()
                        } else {
                            LaunchedEffect(selectedId) {
                                jobApplicationViewModel.loadJobApplication(selectedId)
                            }

                            val isSinglePane = navigator.scaffoldValue[ListDetailPaneScaffoldRole.List] == PaneAdaptedValue.Hidden
                            val handleBack: () -> Unit = {
                                scope.launch {
                                    if (navigator.canNavigateBack()) {
                                        navigator.navigateBack()
                                    }
                                }
                            }

                            JobApplicationDetailsScreen(
                                jobApplicationViewModel = jobApplicationViewModel,
                                showBackNavigation = isSinglePane,
                                onBackClick = handleBack,
                                onDiscardClick = handleBack,
                                onSaveClick = handleBack,
                                onConfirmDelete = handleBack
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun EmptyJobDetailPlaceholder(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(72.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Work,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Job Selected",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Choose or add a job application from the list to view and manage its details.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
