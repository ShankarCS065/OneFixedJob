// CreateEditOpportunityDialog.kt
package com.devlopershankar.onefixedjob.ui.components

import com.devlopershankar.onefixedjob.ui.model.Opportunity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditOpportunityDialog(
    isEdit: Boolean = false,
    existingOpportunity: Opportunity? = null,
    onDismiss: () -> Unit,
    onSave: (Opportunity) -> Unit
) {
    // State variables for form fields
    var companyName by remember { mutableStateOf(existingOpportunity?.companyName ?: "") }
    var roleName by remember { mutableStateOf(existingOpportunity?.roleName ?: "") }
    var applyLink by remember { mutableStateOf(existingOpportunity?.applyLink ?: "") }
    var description by remember { mutableStateOf(existingOpportunity?.description ?: "") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Launcher to pick image from gallery
    val launcher = rememberLauncherForActivityResult(
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
        confirmButton = {
            TextButton(
                onClick = {
                    coroutineScope.launch {
                        // Validate inputs
                        if (companyName.isBlank() || roleName.isBlank() || applyLink.isBlank()) {
                            // Show error message (e.g., using a Snackbar or Toast)
                            // For simplicity, using a Toast here
                            android.widget.Toast.makeText(
                                context,
                                "Please fill in all required fields.",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                            return@launch
                        }

                        // If an image is selected, upload it to Firebase Storage
                        if (imageUri != null) {
                            isUploading = true
                            try {
                                val storageRef = FirebaseStorage.getInstance().reference
                                    .child("opportunity_images/${System.currentTimeMillis()}_${imageUri?.lastPathSegment}")
                                storageRef.putFile(imageUri!!).await()
                                val downloadUri = storageRef.downloadUrl.await()

                                val opportunity = Opportunity(
                                    id = existingOpportunity?.id ?: "",
                                    type = existingOpportunity?.type ?: "Job", // Ensure type is set
                                    companyName = companyName,
                                    roleName = roleName,
                                    applyLink = applyLink,
                                    description = description,
                                    imageUrl = downloadUri.toString(),
                                    timestamp = existingOpportunity?.timestamp ?: Timestamp.now()
                                )
                                onSave(opportunity)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                // Show error message
                                android.widget.Toast.makeText(
                                    context,
                                    "Image upload failed: ${e.message}",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            } finally {
                                isUploading = false
                            }
                        } else {
                            // No image selected; proceed without uploading
                            val opportunity = Opportunity(
                                id = existingOpportunity?.id ?: "",
                                type = existingOpportunity?.type ?: "Job", // Ensure type is set
                                companyName = companyName,
                                roleName = roleName,
                                applyLink = applyLink,
                                description = description,
                                imageUrl = existingOpportunity?.imageUrl ?: "",
                                timestamp = existingOpportunity?.timestamp ?: Timestamp.now()
                            )
                            onSave(opportunity)
                        }
                        onDismiss()
                    }
                }
            ) {
                if (isEdit) {
                    Text("Update")
                } else {
                    Text("Create")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        },
        title = {
            if (isEdit) {
                Text("Edit Opportunity")
            } else {
                Text("Create Opportunity")
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = companyName,
                    onValueChange = { companyName = it },
                    label = { Text("Company Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = roleName,
                    onValueChange = { roleName = it },
                    label = { Text("Role Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = applyLink,
                    onValueChange = { applyLink = it },
                    label = { Text("Apply Link (URL)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Image Picker
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { launcher.launch("image/*") }) {
                        Icon(
                            imageVector = Icons.Filled.AddAPhoto,
                            contentDescription = "Add Image"
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = imageUri?.lastPathSegment ?: "No Image Selected")
                }
                // Display selected image
                imageBitmap?.let { bitmap ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                if (isUploading) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        }
    ) // Correctly close AlertDialog with ')'
}
