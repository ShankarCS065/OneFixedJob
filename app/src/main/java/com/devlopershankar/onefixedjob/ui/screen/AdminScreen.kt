// AdminScreen.kt
package com.devlopershankar.onefixedjob.ui.screen

import com.devlopershankar.onefixedjob.ui.model.Opportunity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.devlopershankar.onefixedjob.R
import com.devlopershankar.onefixedjob.ui.viewmodel.OpportunityViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    navController: NavController,
    opportunityViewModel: OpportunityViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // UI states
    var type by remember { mutableStateOf("Job") } // Default type
    var companyName by remember { mutableStateOf("") }
    var roleName by remember { mutableStateOf("") }
    var applyLink by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Collect events from the ViewModel
    LaunchedEffect(key1 = opportunityViewModel) {
        opportunityViewModel.eventFlow.collectLatest { event ->
            when (event) {
                is OpportunityViewModel.UiEvent.ShowToast -> {
                    // Show Snackbar
                    snackbarHostState.showSnackbar(event.message)
                }
                is OpportunityViewModel.UiEvent.ShowError -> {
                    // Show error message in Snackbar
                    snackbarHostState.showSnackbar(event.message)
                }
                is OpportunityViewModel.UiEvent.AddSuccess -> {
                    // Show success message in Snackbar and clear fields
                    snackbarHostState.showSnackbar("Opportunity added successfully!")
                    companyName = ""
                    roleName = ""
                    applyLink = ""
                    description = ""
                }
                is OpportunityViewModel.UiEvent.UpdateSuccess -> {
                    // Handle update success if necessary
                    snackbarHostState.showSnackbar("Opportunity updated successfully!")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Panel") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text("Add New Opportunity", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            // Dropdown for Opportunity Type
            var expanded by remember { mutableStateOf(false) }
            val types = listOf("Job", "Internship", "Course", "Practice")

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    readOnly = true,
                    label = { Text("Opportunity Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    types.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(text = selectionOption) },
                            onClick = {
                                type = selectionOption
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Company Name
            OutlinedTextField(
                value = companyName,
                onValueChange = { companyName = it },
                label = { Text("Company Name") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Role Name
            OutlinedTextField(
                value = roleName,
                onValueChange = { roleName = it },
                label = { Text("Role Name") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Apply Link
            OutlinedTextField(
                value = applyLink,
                onValueChange = { applyLink = it },
                label = { Text("Apply Link (URL)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Add Button
            Button(
                onClick = {
                    // Validate inputs
                    if (companyName.isBlank() || roleName.isBlank() || applyLink.isBlank()) {
                        // Show error message in Snackbar
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Please fill in all required fields.")
                        }
                    } else {
                        coroutineScope.launch {
                            val opportunity = Opportunity(
                                type = type,
                                companyName = companyName,
                                roleName = roleName,
                                applyLink = applyLink,
                                description = description,
                                imageUrl = "", // Handle image upload separately if needed
                                timestamp = Timestamp.now()
                            )
                            opportunityViewModel.addOpportunity(
                                opportunity = opportunity,
                                onSuccess = {
                                    // Handle success (already emitting event in ViewModel)
                                },
                                onFailure = { errorMsg ->
                                    // Handle failure (already emitting event in ViewModel)
                                }
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text("Add Opportunity")
            }
        }
    }
}
