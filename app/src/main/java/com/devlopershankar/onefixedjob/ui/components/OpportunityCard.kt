// OpportunityCard.kt
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.devlopershankar.onefixedjob.R
@Composable
fun OpportunityCard(
    opportunity: Opportunity,
    onClick: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
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
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = opportunity.roleName, style = MaterialTheme.typography.titleMedium)
                Text(text = opportunity.companyName, style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = { onEdit() }) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit Opportunity"
                )
            }
        }
    }
}
