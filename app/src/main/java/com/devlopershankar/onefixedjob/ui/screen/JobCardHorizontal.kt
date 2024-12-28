// JobCardHorizontal.kt
package com.devlopershankar.onefixedjob.ui.screen

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.devlopershankar.onefixedjob.navigation.Screens

data class JobInfo(
    val companyName: String,
    val roleName: String,
    val applyLink: String
)

@Composable
fun JobCardHorizontal(job: JobInfo, navController: NavController) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .height(100.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = job.companyName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = job.roleName,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Apply",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable {
                        val encodedUrl = Uri.encode(job.applyLink)
                        navController.navigate(Screens.JobDetailScreen.createRoute(encodedUrl))
                    }
            )
        }
    }
}
