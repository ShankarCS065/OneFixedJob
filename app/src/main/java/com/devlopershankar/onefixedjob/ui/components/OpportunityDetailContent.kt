// OpportunityDetailContent.kt
package com.devlopershankar.onefixedjob.ui.components

import com.devlopershankar.onefixedjob.ui.model.Opportunity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.Alignment
import com.devlopershankar.onefixedjob.R
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource

@Composable
fun OpportunityDetailContent(opportunity: Opportunity) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = opportunity.roleName, style = MaterialTheme.typography.headlineMedium)
        Text(text = "Company: ${opportunity.companyName}", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
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
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Description:", style = MaterialTheme.typography.titleSmall)
        Text(text = opportunity.description, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Apply Here:", style = MaterialTheme.typography.titleSmall)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = opportunity.applyLink,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable {
                        // Open the apply link in the browser
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(opportunity.applyLink))
                        context.startActivity(intent)
                    }
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.OpenInBrowser,
                contentDescription = "Open Link",
                modifier = Modifier
                    .size(16.dp)
                    .clickable {
                        // Open the apply link in the browser
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(opportunity.applyLink))
                        context.startActivity(intent)
                    }
            )
        }
    }
}
