// OpportunityDetailContent.kt
package com.devlopershankar.onefixedjob.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.devlopershankar.onefixedjob.ui.model.Opportunity
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.devlopershankar.onefixedjob.R
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.clickable

@Composable
fun OpportunityDetailContent(opportunity: Opportunity) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Make content scrollable
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Opportunity Image
        if (opportunity.imageUrl.isNotEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(opportunity.imageUrl)
                    .crossfade(true)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_broken_image)
                    .build(),
                contentDescription = "${opportunity.roleName} Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }

        // Company Name
        Text(
            text = opportunity.companyName,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.Black
        )

        // Role Name
        Text(
            text = opportunity.roleName,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Color.DarkGray
        )

        // Job Type (if applicable)
        if (opportunity.type == "Job") {
            Text(
                text = "Job Type: ${opportunity.jobType}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }

        // Batch (if applicable)
        if (opportunity.batch.isNotBlank()) {
            Text(
                text = "Batch: ${opportunity.batch}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }

        // Description
        Text(
            text = opportunity.description,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )

        // "Apply Here:" Label
        Text(
            text = "Apply Here:",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = Color.Black
        )

        // Apply Link String
        Text(
            text = opportunity.applyLink,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    // Open the link in a browser
                    if (isValidUrl(opportunity.applyLink)) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(opportunity.applyLink))
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "Invalid Apply Link", Toast.LENGTH_SHORT).show()
                    }
                },
            textAlign = TextAlign.Start
        )

        // "Apply" Button
        Button(
            onClick = {
                if (isValidUrl(opportunity.applyLink)) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(opportunity.applyLink))
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Invalid Apply Link", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.align(Alignment.Start),
            colors = ButtonDefaults.buttonColors(Color.Green)
        ) {
            Text(
                text = "Apply",
                color = Color.Black,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
            )
        }


        // Timestamp
        Text(
            text = "Posted on: ${opportunity.timestamp.toDate()}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        // If recommended, show a badge
        if (opportunity.isRecommended) {
            Badge(
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = "Recommended")
            }
        }
    }
}


