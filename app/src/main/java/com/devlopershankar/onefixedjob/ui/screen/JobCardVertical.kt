// JobCardVertical.kt
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
import androidx.navigation.NavController
import com.devlopershankar.onefixedjob.R
import com.devlopershankar.onefixedjob.navigation.Screens

@Composable
fun JobCardVertical(job: JobInfo, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable {
                // Navigate to JobDetailScreen with applyLink
                navController.navigate("${Screens.JobDetailScreen.route}/${job.applyLink}")
            },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_job),
                contentDescription = "Job Logo",
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 8.dp),
                tint = MaterialTheme.colorScheme.primary // Optional: icon tint
            )
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    job.companyName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(job.roleName, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = "Apply",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable {
                        // Handle Apply action (e.g., open link)
                    }
            )
        }
    }
}
