//// JobScreen.kt
//package com.devlopershankar.onefixedjob.ui.screen
//
//import com.devlopershankar.onefixedjob.ui.model.Opportunity
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.Edit
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import com.devlopershankar.onefixedjob.ui.components.OpportunityCard
//import com.devlopershankar.onefixedjob.ui.components.CreateEditOpportunityDialog
//import com.devlopershankar.onefixedjob.ui.viewmodel.OpportunityViewModel
//import kotlinx.coroutines.launch
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun JobScreen(
//    navController: NavController,
//    isAdmin: Boolean,
//    viewModel: OpportunityViewModel = viewModel()
//) {
//    val jobs by viewModel.opportunities.collectAsState()
//    val isLoading by viewModel.isLoading.collectAsState()
//    val error by viewModel.error.collectAsState()
//
//    var showDialog by remember { mutableStateOf(false) }
//    var isEditMode by remember { mutableStateOf(false) }
//    var selectedOpportunity by remember { mutableStateOf<Opportunity?>(null) }
//
//    // Fetch jobs when the screen is first displayed
//    LaunchedEffect(Unit) {
//        viewModel.getOpportunitiesByType("Job")
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Job Opportunities") },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(
//                            imageVector = Icons.Filled.ArrowBack,
//                            contentDescription = "Back",
//                            modifier = Modifier.size(24.dp)
//                        )
//                    }
//                },
//                actions = {
//                    if (isAdmin) {
//                        IconButton(onClick = { showDialog = true }) {
//                            Icon(
//                                imageVector = Icons.Filled.Edit,
//                                contentDescription = "Create Job"
//                            )
//                        }
//                    }
//                }
//            )
//        }
//    ) { innerPadding ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//        ) {
//            // Temporary Debugging UI
//            Text(text = "Admin Status: $isAdmin", style = MaterialTheme.typography.bodySmall)
//
//            when {
//                isLoading -> {
//                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//                }
//                error != null -> {
//                    Text(
//                        text = error ?: "An unknown error occurred.",
//                        color = MaterialTheme.colorScheme.error,
//                        modifier = Modifier.align(Alignment.Center)
//                    )
//                }
//                jobs.isEmpty() -> {
//                    Text(
//                        text = "No Job Opportunities Found.",
//                        modifier = Modifier.align(Alignment.Center)
//                    )
//                }
//                else -> {
//                    LazyColumn(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(16.dp),
//                        verticalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        items(jobs) { job ->
//                            OpportunityCard(
//                                opportunity = job,
//                                onClick = {
//                                    // Navigate to Job Detail Screen
//                                    navController.navigate("job_detail/${job.id}")
//                                },
//                                onEdit = {
//                                    if (isAdmin) {
//                                        selectedOpportunity = job
//                                        isEditMode = true
//                                        showDialog = true
//                                    }
//                                }
//                            )
//                        }
//                    }
//                }
//            }
//
//            // Create/Edit Opportunity Dialog
//            if (showDialog) {
//                CreateEditOpportunityDialog(
//                    isEdit = isEditMode,
//                    existingOpportunity = selectedOpportunity,
//                    onDismiss = {
//                        showDialog = false
//                        isEditMode = false
//                        selectedOpportunity = null
//                    },
//                    onSave = { opportunity ->
//                        if (isEditMode) {
//                            viewModel.updateOpportunity(
//                                opportunity = opportunity,
//                                onSuccess = {
//                                    showDialog = false
//                                    isEditMode = false
//                                    selectedOpportunity = null
//                                },
//                                onFailure = { errorMsg ->
//                                    // Handle failure (e.g., show Snackbar)
//                                    // Example:
//                                    // coroutineScope.launch { scaffoldState.snackbarHostState.showSnackbar("Update failed: $errorMsg") }
//                                }
//                            )
//                        } else {
//                            viewModel.addOpportunity(
//                                opportunity = opportunity,
//                                onSuccess = {
//                                    showDialog = false
//                                },
//                                onFailure = { errorMsg ->
//                                    // Handle failure (e.g., show Snackbar)
//                                    // Example:
//                                    // coroutineScope.launch { scaffoldState.snackbarHostState.showSnackbar("Creation failed: $errorMsg") }
//                                }
//                            )
//                        }
//                    }
//                )
//            }
//        }
//    }
//}

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
    val allJobs by viewModel.allJobs.collectAsState()
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
                                        navController.navigate(Screens.editOpportunity(job.id))
                                    }
                                }
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
                    onDismiss = {
                        showDialog = false
                        isEditMode = false
                        selectedOpportunity = null
                    },
                    onSave = { opportunity ->
                        coroutineScope.launch {
                            if (isEditMode) {
                                viewModel.updateOpportunity(
                                    opportunity = opportunity,
                                    onSuccess = {
                                        // Success handled via eventFlow
                                    },
                                    onFailure = { errorMsg ->
                                        // Failure handled via eventFlow
                                    }
                                )
                            } else {
                                viewModel.addOpportunity(
                                    opportunity = opportunity,
                                    onSuccess = {
                                        // Success handled via eventFlow
                                    },
                                    onFailure = { errorMsg ->
                                        // Failure handled via eventFlow
                                    }
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}
