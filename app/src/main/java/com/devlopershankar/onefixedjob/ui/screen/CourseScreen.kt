//// CourseScreen.kt
//package com.devlopershankar.onefixedjob.ui.screen
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import com.devlopershankar.onefixedjob.navigation.Screens
//import com.devlopershankar.onefixedjob.ui.components.OpportunityCard
//import com.devlopershankar.onefixedjob.ui.viewmodel.OpportunityViewModel
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CourseScreen(
//    navController: NavController,
//    viewModel: OpportunityViewModel = viewModel()
//) {
//    val allCourses by viewModel.opportunities.collectAsState()
//    val courses = allCourses.filter { it.type == "Course" }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Courses") },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(
//                            imageVector = Icons.Default.ArrowBack,
//                            contentDescription = "Back"
//                        )
//                    }
//                }
//            )
//        }
//    ) { innerPadding ->
//        if (courses.isEmpty()) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(innerPadding),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(text = "No Courses Available.", style = MaterialTheme.typography.bodyLarge)
//            }
//        } else {
//            LazyColumn(
//                contentPadding = innerPadding,
//                modifier = Modifier.fillMaxSize(),
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                items(courses) { course ->
//                    OpportunityCard(
//                        opportunity = course,
//                        onClick = {
//                            navController.navigate(Screens.courseDetail(course.id))
//                        },
//                        onEdit = {
//                            // Implement edit functionality if admin
//                            navController.navigate(Screens.editOpportunity(course.id))
//                        }
//                    )
//                }
//            }
//        }
//    }
//}

// CourseScreen.kt
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
fun CourseScreen(
    navController: NavController,
    isAdmin: Boolean,
    viewModel: OpportunityViewModel = viewModel()
) {
    // Collect all courses
    val allCourses by viewModel.allCourses.collectAsState()
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
                title = { Text("Courses") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
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
                                contentDescription = "Create Course"
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
                allCourses.isEmpty() -> {
                    Text(
                        text = "No Courses Available.",
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
                        items(allCourses) { course ->
                            OpportunityCard(
                                opportunity = course,
                                onClick = {
                                    navController.navigate(Screens.courseDetail(course.id))
                                },
                                onEdit = {
                                    if (isAdmin) {
                                        selectedOpportunity = course
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
