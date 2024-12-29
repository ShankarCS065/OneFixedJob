// JobCardVertical.kt
package com.devlopershankar.onefixedjob.ui.components

import com.devlopershankar.onefixedjob.ui.model.Opportunity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.devlopershankar.onefixedjob.R

@Composable
fun JobCardVertical(
    opportunity: Opportunity,
    navController: NavController,
    isAdmin: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // Navigate to Opportunity Detail Screen based on type
                when (opportunity.type) {
                    "Job" -> navController.navigate("job_detail/${opportunity.id}")
                    "Internship" -> navController.navigate("internship_detail/${opportunity.id}")
                    "Course" -> navController.navigate("course_detail/${opportunity.id}")
                    "Practice" -> navController.navigate("practice_detail/${opportunity.id}")
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Image(
                painter = rememberImagePainter(
                    data = opportunity.imageUrl,
                    builder = {
                        crossfade(true)
                        placeholder(R.drawable.ic_image_placeholder)
                        error(R.drawable.ic_broken_image)
                    }
                ),
                contentDescription = "Company Logo",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .align(Alignment.CenterVertically),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = opportunity.roleName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = opportunity.companyName,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = opportunity.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
            }
            if (isAdmin) {
                IconButton(onClick = {
                    // Navigate to Edit Opportunity Screen
                    navController.navigate("edit_opportunity/${opportunity.id}")
                }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit Opportunity",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
