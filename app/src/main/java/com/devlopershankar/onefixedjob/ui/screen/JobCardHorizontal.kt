// JobCardHorizontal.kt
package com.devlopershankar.onefixedjob.ui.components

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.devlopershankar.onefixedjob.R
import com.devlopershankar.onefixedjob.ui.model.Opportunity
import android.util.Patterns

@Composable
fun JobCardHorizontal(
    opportunity: Opportunity,
    navController: NavController,
    onEdit: () -> Unit,
    isAdmin: Boolean
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .width(250.dp)
            .clickable {
                // Navigate to opportunity detail based on type
                when (opportunity.type) {
                    "Job" -> navController.navigate("job_detail/${opportunity.id}")
                    "Internship" -> navController.navigate("internship_detail/${opportunity.id}")
                    "Course" -> navController.navigate("course_detail/${opportunity.id}")
                    "Practice" -> navController.navigate("practice_detail/${opportunity.id}")
                    else -> { /* Handle unknown types */ }
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Opportunity Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(opportunity.imageUrl)
                    .crossfade(true)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_broken_image)
                    .build(),
                contentDescription = "${opportunity.roleName} Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Company Name
            Text(
                text = opportunity.companyName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Role Name
            Text(
                text = opportunity.roleName,
                style = MaterialTheme.typography.bodyMedium
            )

            // Batch Information
            if (opportunity.batch.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Batch: ${opportunity.batch}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Apply Button
            Button(
                onClick = {
                    if (opportunity.applyLink.isNotBlank() && isValidUrl(opportunity.applyLink)) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(opportunity.applyLink))
                        context.startActivity(intent)
                    } else {
                        Log.e("JobCardHorizontal", "Invalid Apply Link URL: ${opportunity.applyLink}")
                        Toast.makeText(context, "Invalid Apply Link", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "Apply")
            }

            // If Admin, show Edit button
            if (isAdmin) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onEdit() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text(text = "Edit")
                }
            }
        }
    }
}

/**
 * Utility function to validate URLs.
 */
fun isValidUrl(url: String): Boolean {
    return Patterns.WEB_URL.matcher(url).matches()
}
