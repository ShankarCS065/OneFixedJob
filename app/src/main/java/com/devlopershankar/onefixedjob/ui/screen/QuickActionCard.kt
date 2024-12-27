package com.devlopershankar.onefixedjob.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.navigation.NavController
import com.devlopershankar.onefixedjob.R
import com.devlopershankar.onefixedjob.navigation.Screens

@Composable
fun QuickActionCard(
    action: String,
    navController: NavController,
    modifier: Modifier = Modifier // Accept Modifier as a parameter
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable {
                // Handle navigation based on action
                when (action) {
                    "Job" -> navController.navigate(Screens.JobScreen.route)
                    "Internship" -> navController.navigate(Screens.InternshipScreen.route)
                    "Course" -> navController.navigate(Screens.CourseScreen.route)
                    "Practice" -> navController.navigate(Screens.PracticeScreen.route)
                }
            },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = action, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
