// InternshipDetailScreen.kt
package com.devlopershankar.onefixedjob.ui.screen

import com.devlopershankar.onefixedjob.ui.model.Opportunity
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.devlopershankar.onefixedjob.ui.viewmodel.OpportunityViewModel
import com.devlopershankar.onefixedjob.ui.components.OpportunityDetailContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InternshipDetailScreen(navController: NavController, internshipId: String?) {
    val opportunityViewModel: OpportunityViewModel = viewModel()
    var opportunity by remember { mutableStateOf<Opportunity?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(internshipId) {
        if (!internshipId.isNullOrEmpty()) {
            val fetchedOpportunity = opportunityViewModel.getOpportunityById(internshipId)
            if (fetchedOpportunity != null) {
                opportunity = fetchedOpportunity
            } else {
                errorMessage = "Internship not found."
            }
            isLoading = false
        } else {
            errorMessage = "Invalid Internship ID."
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Internship Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator()
                errorMessage != null -> Text(text = errorMessage!!)
                opportunity != null -> {
                    OpportunityDetailContent(opportunity = opportunity!!)
                }
            }
        }
    }
}
