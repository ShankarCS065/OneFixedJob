// CourseDetailScreen.kt
package com.devlopershankar.onefixedjob.ui.screen

import com.devlopershankar.onefixedjob.ui.components.OpportunityDetailContent

// Inside your composable

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
import coil.compose.rememberImagePainter
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import com.devlopershankar.onefixedjob.R
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(navController: NavController, courseId: String?) {
    val opportunityViewModel: OpportunityViewModel = viewModel()
    var opportunity by remember { mutableStateOf<Opportunity?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(courseId) {
        if (!courseId.isNullOrEmpty()) {
            val fetchedOpportunity = opportunityViewModel.getOpportunityById(courseId)
            if (fetchedOpportunity != null && fetchedOpportunity.type == "Course") {
                opportunity = fetchedOpportunity
            } else {
                errorMessage = "Course not found."
            }
            isLoading = false
        } else {
            errorMessage = "Invalid Course ID."
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Course Details") },
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

