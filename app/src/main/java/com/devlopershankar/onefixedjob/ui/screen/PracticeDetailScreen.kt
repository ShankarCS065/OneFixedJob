// Example: PracticeDetailScreen.kt
package com.devlopershankar.onefixedjob.ui.screen

import androidx.compose.foundation.layout.*
import com.devlopershankar.onefixedjob.ui.components.OpportunityDetailContent
import com.devlopershankar.onefixedjob.ui.model.Opportunity
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeDetailScreen(navController: NavController, practiceId: String?) {
    val opportunityViewModel: OpportunityViewModel = viewModel()
    var opportunity by remember { mutableStateOf<Opportunity?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(practiceId) {
        if (!practiceId.isNullOrEmpty()) {
            val fetchedOpportunity = opportunityViewModel.getOpportunityById(practiceId)
            if (fetchedOpportunity != null && fetchedOpportunity.type == "Practice") {
                opportunity = fetchedOpportunity
            } else {
                errorMessage = "Practice not found."
            }
            isLoading = false
        } else {
            errorMessage = "Invalid Practice ID."
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Practice Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
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
