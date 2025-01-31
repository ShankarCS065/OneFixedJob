// OpportunityCard.kt
package com.devlopershankar.onefixedjob.ui.components

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.devlopershankar.onefixedjob.R
import com.devlopershankar.onefixedjob.ui.model.Opportunity
import android.util.Log

@Composable
fun OpportunityCard(
    opportunity: Opportunity,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    isAdmin: Boolean = false
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Opportunity Image with Error Handling
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(
                        if (opportunity.imageUrl.isNotBlank() && isValidUrl(opportunity.imageUrl)) {
                            opportunity.imageUrl
                        } else {
                            R.drawable.ic_image_placeholder // Fallback image
                        }
                    )
                    .crossfade(true)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_broken_image)
                    .build(),
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
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                // Display Batch if available
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
                            // Validate the applyLink before launching
                            if (isValidUrl(opportunity.applyLink)) {
                                val intent =
                                    Intent(Intent.ACTION_VIEW, Uri.parse(opportunity.applyLink))
                                context.startActivity(intent)
                            } else {
                                Log.e("OpportunityCard", "Invalid Apply Link URL: ${opportunity.applyLink}")
                                // Optionally, show a Toast or handle invalid URL
                            }
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
                if (isAdmin) {
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

