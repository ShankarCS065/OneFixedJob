// JobDetailScreen.kt
package com.devlopershankar.onefixedjob.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.devlopershankar.onefixedjob.R
import com.devlopershankar.onefixedjob.ui.util.OpenUrlInBrowserIntent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(navController: NavController, applyLink: String?) {
    // Validate the applyLink
    if (applyLink.isNullOrBlank()) {
        // Show an error message or navigate back
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Job Detail") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_back),
                                contentDescription = "Back",
                                modifier = Modifier.size(24.dp)
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
                Text(text = "Invalid application link.")
            }
        }
    } else {
        // Open the applyLink using Intent
        OpenUrlInBrowserIntent(url = applyLink)
    }
}
