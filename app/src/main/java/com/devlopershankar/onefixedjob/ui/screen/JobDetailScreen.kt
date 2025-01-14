// JobDetailScreen.kt
package com.devlopershankar.onefixedjob.ui.screen

import com.devlopershankar.onefixedjob.ui.components.OpportunityDetailContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import com.devlopershankar.onefixedjob.ui.model.Opportunity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.devlopershankar.onefixedjob.R
import com.devlopershankar.onefixedjob.ui.viewmodel.OpportunityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(navController: NavController, jobId: String?) {
    val opportunityViewModel: OpportunityViewModel = viewModel()
    var opportunity by remember { mutableStateOf<Opportunity?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(jobId) {
        if (!jobId.isNullOrEmpty()) {
            val fetchedOpportunity = opportunityViewModel.getOpportunityById(jobId)
            if (fetchedOpportunity != null && fetchedOpportunity.type == "Job") {
                opportunity = fetchedOpportunity
            } else {
                errorMessage = "Job not found."
            }
            isLoading = false
        } else {
            errorMessage = "Invalid Job ID."
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Job Details") },
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

