//// CreateOpportunityScreen.kt
//package com.devlopershankar.onefixedjob.ui.screen
//
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import com.devlopershankar.onefixedjob.ui.viewmodel.OpportunityViewModel
//import com.devlopershankar.onefixedjob.ui.viewmodel.UserProfileViewModel
//
//@Composable
//fun CreateOpportunityScreen(
//    navController: NavController,
//    type: String,
//    userProfileViewModel: UserProfileViewModel = viewModel(),
//    opportunityViewModel: OpportunityViewModel = viewModel()
//) {
//    // Observe the isAdmin state using property delegation
//    val isAdmin by userProfileViewModel.isAdmin.collectAsState()
//
//    CreateEditOpportunityScreen(
//        navController = navController,
//        isEdit = false,
//        opportunityType = type,
//        opportunityId = null,
//        isAdmin = isAdmin,
//        viewModel = opportunityViewModel
//    )
//}

// CreateOpportunityScreen.kt
package com.devlopershankar.onefixedjob.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.devlopershankar.onefixedjob.ui.viewmodel.OpportunityViewModel
import com.devlopershankar.onefixedjob.ui.viewmodel.UserProfileViewModel

@Composable
fun CreateOpportunityScreen(
    navController: NavController,
    type: String,
    userProfileViewModel: UserProfileViewModel = viewModel(),
    opportunityViewModel: OpportunityViewModel = viewModel()
) {
    // Observe the isAdmin state using property delegation
    val isAdmin by userProfileViewModel.isAdmin.collectAsState()

    CreateEditOpportunityScreen(
        navController = navController,
        isEdit = false,
        opportunityType = type,
        opportunityId = null,
        isAdmin = isAdmin,
        viewModel = opportunityViewModel
    )
}
