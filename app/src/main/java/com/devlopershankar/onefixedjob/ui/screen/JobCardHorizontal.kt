// JobCardHorizontal.kt
package com.devlopershankar.onefixedjob.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.devlopershankar.onefixedjob.R
import com.devlopershankar.onefixedjob.navigation.Screens

@Composable
fun JobCardHorizontal(job: JobInfo, navController: NavController) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .height(100.dp)
            .clickable {
                // Navigate to JobDetailScreen with applyLink
                navController.navigate("${Screens.JobDetailScreen.route}/${job.applyLink}")
            },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                job.companyName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(job.roleName, style = MaterialTheme.typography.bodySmall)
            Text(
                text = "Apply",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    // Handle Apply action (e.g., open link)
                }
            )
        }
    }
}
