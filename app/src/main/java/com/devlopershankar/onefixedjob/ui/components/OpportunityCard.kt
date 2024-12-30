// OpportunityCard.kt
package com.devlopershankar.onefixedjob.ui.components

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
import coil.compose.rememberImagePainter
import com.devlopershankar.onefixedjob.R
import com.devlopershankar.onefixedjob.ui.model.Opportunity
import android.content.Intent
import android.net.Uri

@Composable
fun OpportunityCard(
    opportunity: Opportunity,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    isAdmin: Boolean // New parameter
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
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
                contentDescription = "${opportunity.roleName} Logo",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Column for Role, Company, and Batch
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = opportunity.companyName,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = opportunity.roleName,
                    style = MaterialTheme.typography.titleMedium
                )

                // Display Batch
                if (opportunity.batch.isNotBlank()) {
                    Text(
                        text = "Batch: ${opportunity.batch}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Apply Button
            IconButton(
                onClick = {
                    if (opportunity.applyLink.isNotBlank()) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(opportunity.applyLink))
                        context.startActivity(intent)
                    }
                },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInBrowser,
                    contentDescription = "Apply",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Optional: Indicate if the opportunity is recommended
            if (opportunity.isRecommended) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Recommended",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Edit Button if the user is admin
            if (isAdmin) { // Use isAdmin to conditionally display the Edit icon
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { onEdit() }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Opportunity",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
