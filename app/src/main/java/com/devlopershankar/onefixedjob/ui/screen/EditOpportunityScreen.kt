// EditOpportunityScreen.kt
package com.devlopershankar.onefixedjob.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.devlopershankar.onefixedjob.ui.viewmodel.OpportunityViewModel
import com.devlopershankar.onefixedjob.ui.viewmodel.UserProfileViewModel

@Composable
fun EditOpportunityScreen(
    navController: NavController,
    opportunityId: String?,
    userProfileViewModel: UserProfileViewModel = viewModel(),
    opportunityViewModel: OpportunityViewModel = viewModel()
) {
    // Observe the isAdmin state using property delegation
    val isAdmin by userProfileViewModel.isAdmin.collectAsState()

    if (opportunityId.isNullOrEmpty()) {
        // Handle null or empty ID, possibly navigate back or show an error message
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Invalid Opportunity ID", style = MaterialTheme.typography.headlineMedium)
        }
    } else {
        // Utilize the existing CreateEditOpportunityScreen composable in edit mode
        CreateEditOpportunityScreen(
            navController = navController,
            isEdit = true,
            opportunityId = opportunityId,
            opportunityType = null, // Opportunity type can be fetched from the opportunity data within CreateEditOpportunityScreen
            isAdmin = isAdmin,
            viewModel = opportunityViewModel
        )
    }
}
