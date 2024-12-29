// PracticeDetailScreen.kt
package com.devlopershankar.onefixedjob.ui.screen

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

@Composable
fun OpportunityDetailContent(opportunity: Opportunity) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = opportunity.roleName, style = MaterialTheme.typography.headlineMedium)
        Text(text = "Company: ${opportunity.companyName}", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = rememberImagePainter(
                data = opportunity.imageUrl,
                builder = {
                    crossfade(true)
                    placeholder(R.drawable.ic_image_placeholder)
                    error(R.drawable.ic_broken_image)
                }
            ),
            contentDescription = "Company Logo",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Description:", style = MaterialTheme.typography.titleSmall)
        Text(text = opportunity.description, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Apply Here:", style = MaterialTheme.typography.titleSmall)
        Text(
            text = opportunity.applyLink,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable {
                    // Handle apply link click (e.g., open in browser)
                }
        )
    }
}
