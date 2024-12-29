// QuickActionCard.kt
package com.devlopershankar.onefixedjob.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun QuickActionCard(
    action: String,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable {
                // Navigate to the corresponding opportunity screen based on action
                when (action) {
                    "Job" -> navController.navigate("job_screen")
                    "Internship" -> navController.navigate("internship_screen")
                    "Course" -> navController.navigate("course_screen")
                    "Practice" -> navController.navigate("practice_screen")
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = action, style = MaterialTheme.typography.titleLarge)
        }
    }
}
