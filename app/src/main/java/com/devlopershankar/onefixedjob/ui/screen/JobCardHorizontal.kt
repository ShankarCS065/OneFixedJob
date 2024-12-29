// JobCardHorizontal.kt
package com.devlopershankar.onefixedjob.ui.components

import androidx.compose.foundation.Image
import com.devlopershankar.onefixedjob.ui.model.Opportunity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.devlopershankar.onefixedjob.R

@Composable
fun JobCardHorizontal(
    opportunity: Opportunity,
    navController: NavController
) {
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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = opportunity.roleName,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = opportunity.companyName,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
