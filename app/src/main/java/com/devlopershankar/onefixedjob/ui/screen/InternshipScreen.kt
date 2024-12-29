// InternshipScreen.kt
package com.devlopershankar.onefixedjob.ui.screen

import com.devlopershankar.onefixedjob.ui.model.Opportunity
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
import com.devlopershankar.onefixedjob.ui.components.OpportunityCard
import com.devlopershankar.onefixedjob.ui.components.CreateEditOpportunityDialog
import com.devlopershankar.onefixedjob.ui.viewmodel.OpportunityViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InternshipScreen(
    navController: NavController,
    isAdmin: Boolean,
    viewModel: OpportunityViewModel = viewModel()

) {
    val internships by viewModel.opportunities.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }
    var selectedOpportunity by remember { mutableStateOf<Opportunity?>(null) }

    // Fetch internships when the screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.getOpportunitiesByType("Internship")
    }

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
                title = { Text("Internship Opportunities") },
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
                                contentDescription = "Create Internship"
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
                internships.isEmpty() -> {
                    Text(
                        text = "No Internship Opportunities Found.",
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
                        items(internships) { internship ->
                            OpportunityCard(
                                opportunity = internship,
                                onClick = {
                                    // Navigate to Internship Detail Screen
                                    navController.navigate("internship_detail/${internship.id}")
                                },
                                onEdit = {
                                    if (isAdmin) {
                                        selectedOpportunity = internship
                                        isEditMode = true
                                        showDialog = true
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
