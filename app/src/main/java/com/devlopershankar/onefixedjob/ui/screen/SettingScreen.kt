package com.devlopershankar.onefixedjob.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.devlopershankar.onefixedjob.R

@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            SettingsTopAppBar(navController = navController)
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize() // Ensure it takes full screen height and width
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start // Align content to the left
            ) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Divider(color = Color.Black.copy(alpha = 0.5f)) // Semi-transparent divider

                // Example options
                SettingsOption(text = "Option 1")
                SettingsOption(text = "Option 2")
                SettingsOption(text = "Option 3")
            }
        },
        containerColor = Color.White, // Set the background color of the Scaffold
        contentColor = Color.Black // Default content color
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopAppBar(navController: NavController) {
    SmallTopAppBar(
        title = { Text(text = "Settings", color = Color.White) },
        navigationIcon = {
            IconButton(onClick = {
                navController.navigateUp() // Navigate back when clicked
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black // Set icon color to black for contrast
                )
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black, // Set title content color to black
            navigationIconContentColor = Color.Black // Set navigation icon color to black
        )
    )
}

@Composable
fun SettingsOption(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = Color.Black, // Set text color to black
        modifier = Modifier
            .fillMaxWidth() // Make the option text fill the width of the screen
            .padding(vertical = 8.dp)
    )
}
