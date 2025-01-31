// CreateEditOpportunityDialog.kt
package com.devlopershankar.onefixedjob.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.devlopershankar.onefixedjob.R
import com.devlopershankar.onefixedjob.ui.model.Opportunity
import com.google.firebase.Timestamp
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditOpportunityDialog(
    isEdit: Boolean,
    existingOpportunity: Opportunity?,
    currentType: String, // New parameter to accept the selected type
    onDismiss: () -> Unit,
    onSave: (Opportunity, Uri?) -> Unit
) {
    var companyName by remember { mutableStateOf(existingOpportunity?.companyName ?: "") }
    var roleName by remember { mutableStateOf(existingOpportunity?.roleName ?: "") }
    var applyLink by remember { mutableStateOf(existingOpportunity?.applyLink ?: "") }
    var description by remember { mutableStateOf(existingOpportunity?.description ?: "") }
    var isRecommended by remember { mutableStateOf(existingOpportunity?.isRecommended ?: false) }

    // Image states
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    // New Fields
    var batch by remember { mutableStateOf(existingOpportunity?.batch ?: "") }
    var jobType by remember { mutableStateOf(existingOpportunity?.jobType ?: "Full-time") }

    val context = LocalContext.current

    // Launcher to pick image
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                imageUri = it
                // Convert Uri to Bitmap for preview
                val bitmap = try {
                    val inputStream = context.contentResolver.openInputStream(it)
                    android.graphics.BitmapFactory.decodeStream(inputStream)
                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                }
                imageBitmap = bitmap
            }
        }
    )

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(if (isEdit) "Edit Opportunity" else "Create Opportunity") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()) // Make scrollable if content is large
            ) {
                OutlinedTextField(
                    value = companyName,
                    onValueChange = { companyName = it },
                    label = { Text("Company Name") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    )
                )
                OutlinedTextField(
                    value = roleName,
                    onValueChange = { roleName = it },
                    label = { Text("Role Name") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    )
                )
                OutlinedTextField(
                    value = applyLink,
                    onValueChange = { applyLink = it },
                    label = { Text("Apply Link (URL)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri
                    )
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Batch Field
                OutlinedTextField(
                    value = batch,
                    onValueChange = { batch = it },
                    label = { Text("Batch") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Job Type Dropdown
                var expandedJobType by remember { mutableStateOf(false) }
                val jobTypes = listOf("Full-time", "Part-time", "Hybrid", "Remote")

                ExposedDropdownMenuBox(
                    expanded = expandedJobType,
                    onExpandedChange = { expandedJobType = it }
                ) {
                    OutlinedTextField(
                        value = jobType,
                        onValueChange = { /* No-op */ },
                        readOnly = true,
                        label = { Text("Job Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedJobType) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedJobType,
                        onDismissRequest = { expandedJobType = false }
                    ) {
                        jobTypes.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(text = selectionOption) },
                                onClick = {
                                    jobType = selectionOption
                                    expandedJobType = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                // Image Picker
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { imageLauncher.launch("image/*") }) {
                        Icon(
                            imageVector = Icons.Filled.AddAPhoto,
                            contentDescription = "Add Image"
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = imageUri?.lastPathSegment
                            ?: if (isEdit && existingOpportunity?.imageUrl?.isNotEmpty() == true) "Existing Image" else "No Image Selected"
                    )
                }
                // Display selected image or existing image
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap!!.asImageBitmap(),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else if (isEdit && existingOpportunity?.imageUrl?.isNotEmpty() == true) {
                    // Display existing image using Coil
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(existingOpportunity?.imageUrl)
                            .crossfade(true)
                            .placeholder(R.drawable.ic_image_placeholder)
                            .error(R.drawable.ic_broken_image)
                            .build(),
                        contentDescription = "Existing Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                // Toggle for isRecommended
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(text = "Feature on Dashboard")
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = isRecommended,
                        onCheckedChange = { isRecommended = it }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                // Pass the collected data back to the parent via onSave
                onSave(
                    Opportunity(
                        id = if (isEdit) existingOpportunity?.id ?: "" else "",
                        type = currentType, // Use the passed 'type' parameter
                        companyName = companyName,
                        roleName = roleName,
                        applyLink = applyLink,
                        description = description,
                        imageUrl = existingOpportunity?.imageUrl ?: "", // The parent will handle image upload
                        timestamp = if (isEdit) existingOpportunity?.timestamp
                            ?: Timestamp.now() else Timestamp.now(),
                        isRecommended = isRecommended,
                        batch = existingOpportunity?.batch ?: "", // Retain existing batch or default
                        jobType = existingOpportunity?.jobType ?: "Full-time" // Retain existing jobType or default
                    ),
                    imageUri
                )
            }) {
                Text(if (isEdit) "Update" else "Create")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
