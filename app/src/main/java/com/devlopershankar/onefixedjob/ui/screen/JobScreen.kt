// JobScreen.kt
package com.devlopershankar.onefixedjob.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.devlopershankar.onefixedjob.navigation.Screens
import com.devlopershankar.onefixedjob.ui.components.OpportunityCard
import com.devlopershankar.onefixedjob.ui.components.CreateEditOpportunityDialog
import com.devlopershankar.onefixedjob.ui.model.Opportunity
import com.devlopershankar.onefixedjob.ui.viewmodel.OpportunityViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobScreen(
    navController: NavController,
    isAdmin: Boolean,
    viewModel: OpportunityViewModel = viewModel()
) {
    // Collect all jobs
    val allJobs by viewModel.recommendedJobs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }
    var selectedOpportunity by remember { mutableStateOf<Opportunity?>(null) }

    // Snackbar Host for showing messages
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect events from ViewModel to show Snackbars
    LaunchedEffect(viewModel) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is OpportunityViewModel.UiEvent.ShowToast -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is OpportunityViewModel.UiEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is OpportunityViewModel.UiEvent.AddSuccess -> {
                    snackbarHostState.showSnackbar("Opportunity created successfully!")
                }
                is OpportunityViewModel.UiEvent.UpdateSuccess -> {
                    snackbarHostState.showSnackbar("Opportunity updated successfully!")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Job Opportunities") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                actions = {
                    if (isAdmin) {
                        IconButton(onClick = {
                            selectedOpportunity = null
                            isEditMode = false
                            showDialog = true
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Create Job"
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                error != null -> {
                    Text(
                        text = error ?: "An unknown error occurred.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                allJobs.isEmpty() -> {
                    Text(
                        text = "No Job Opportunities Found.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(allJobs) { job ->
                            OpportunityCard(
                                opportunity = job,
                                onClick = {
                                    // Navigate to Job Detail Screen
                                    navController.navigate(Screens.jobDetail(job.id))
                                },
                                onEdit = {
                                    if (isAdmin) {
                                        selectedOpportunity = job
                                        isEditMode = true
                                        showDialog = true
                                    }
                                },
                                isAdmin = isAdmin
                            )
                        }
                    }
                }
            }

            // Create/Edit Opportunity Dialog
            if (showDialog) {
                CreateEditOpportunityDialog(
                    isEdit = isEditMode,
                    existingOpportunity = selectedOpportunity,
                    currentType = "Job", // Pass the current type here
                    onDismiss = {
                        showDialog = false
                        isEditMode = false
                        selectedOpportunity = null
                    },
                    onSave = { opportunity, imageUri ->
                        coroutineScope.launch {
                            viewModel.addOrUpdateOpportunity(opportunity, imageUri, isEditMode)
                        }
                    }
                )
            }
        }
    }
}
