// JobCardHorizontal.kt
package com.devlopershankar.onefixedjob.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.devlopershankar.onefixedjob.R
import com.devlopershankar.onefixedjob.ui.model.Opportunity

@Composable
fun JobCardHorizontal(
    opportunity: Opportunity,
    navController: NavController,
    onEdit: (() -> Unit)? = null, // Make onEdit nullable
    isAdmin: Boolean = false // Default to false
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable {
                // Navigate to Opportunity Detail Screen based on type
                when (opportunity.type) {
                    "Job" -> navController.navigate("job_detail/${opportunity.id}")
                    "Internship" -> navController.navigate("internship_detail/${opportunity.id}")
                    "Course" -> navController.navigate("course_detail/${opportunity.id}")
                    "Practice" -> navController.navigate("practice_detail/${opportunity.id}")
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Opportunity Image
            Image(
                painter = rememberImagePainter(
                    data = opportunity.imageUrl,
                    builder = {
                        crossfade(true)
                        placeholder(R.drawable.ic_image_placeholder) // Ensure this drawable exists
                        error(R.drawable.ic_broken_image) // Ensure this drawable exists
                    }
                ),
                contentDescription = "Company Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(
                    text = opportunity.companyName,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = opportunity.roleName,
                    style = MaterialTheme.typography.titleSmall
                )
                // Display Batch
                if (opportunity.batch.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Batch: ${opportunity.batch}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Optional: Indicate if the opportunity is recommended
                if (opportunity.isRecommended) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Recommended",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Apply Button
                if (opportunity.applyLink.isNotBlank()) {
                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(opportunity.applyLink))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInBrowser,
                            contentDescription = "Apply",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Edit Button if the user is admin
                if (isAdmin && onEdit != null) {
                    IconButton(onClick = { onEdit() }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Opportunity",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
