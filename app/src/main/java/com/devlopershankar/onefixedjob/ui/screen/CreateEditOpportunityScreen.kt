// CreateEditOpportunityScreen.kt
package com.devlopershankar.onefixedjob.ui.screen

import com.devlopershankar.onefixedjob.ui.model.Opportunity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.devlopershankar.onefixedjob.ui.viewmodel.OpportunityViewModel
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.IOException
import com.devlopershankar.onefixedjob.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditOpportunityScreen(
    navController: NavController,
    isEdit: Boolean,
    opportunityType: String? = null, // Required for creation
    opportunityId: String? = null, // Required for editing
    isAdmin: Boolean, // Changed from State<Boolean> to Boolean
    viewModel: OpportunityViewModel = viewModel()
) {

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var companyName by remember { mutableStateOf("") }
    var roleName by remember { mutableStateOf("") }
    var applyLink by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var existingOpportunity by remember { mutableStateOf<Opportunity?>(null) }

    val context = LocalContext.current


    if (!isAdmin) {
        // If the user is not an admin, show an access denied message and navigate back
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("You do not have permission to access this screen.")
                navController.popBackStack()
            }
        }
        return
    }


    // Fetch existing opportunity data if in edit mode
    LaunchedEffect(opportunityId) {
        if (isEdit && opportunityId != null) {
            val fetchedOpportunity = viewModel.getOpportunityById(opportunityId)
            fetchedOpportunity?.let {
                existingOpportunity = it
                companyName = it.companyName
                roleName = it.roleName
                applyLink = it.applyLink
                description = it.description
                // Image handling can be enhanced to display existing images
                // For example, set imageUri if imageUrl is available
                // Here, we'll leave imageUri as null to allow user to change image
            } ?: run {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Opportunity not found.")
                    navController.popBackStack()
                }
            }
        }
    }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEdit) "Edit Opportunity" else "Create Opportunity") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                Image(
                    painter = rememberImagePainter(
                        data = existingOpportunity?.imageUrl,
                        builder = {
                            crossfade(true)
                            placeholder(R.drawable.ic_image_placeholder)
                            error(R.drawable.ic_broken_image)
                        }
                    ),
                    contentDescription = "Existing Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            if (isUploading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            Button(
                onClick = {
                    coroutineScope.launch {
                        // Validate inputs
                        if (companyName.isBlank() || roleName.isBlank() || applyLink.isBlank()) {
                            snackbarHostState.showSnackbar("Please fill in all required fields.")
                            return@launch
                        }

                        // If image is selected, upload it to Firebase Storage
                        if (imageUri != null) {
                            isUploading = true
                            try {
                                val storageRef = FirebaseStorage.getInstance().reference
                                    .child("opportunity_images/${System.currentTimeMillis()}_${imageUri?.lastPathSegment}")
                                storageRef.putFile(imageUri!!).await()
                                val downloadUri = storageRef.downloadUrl.await()

                                val opportunity = Opportunity(
                                    id = if (isEdit) existingOpportunity?.id ?: "" else "",
                                    type = opportunityType ?: "Job",
                                    companyName = companyName,
                                    roleName = roleName,
                                    applyLink = applyLink,
                                    description = description,
                                    imageUrl = downloadUri.toString(),
                                    timestamp = if (isEdit) existingOpportunity?.timestamp
                                        ?: Timestamp.now() else Timestamp.now()
                                )
                                if (isEdit) {
                                    viewModel.updateOpportunity(
                                        opportunity = opportunity,
                                        onSuccess = {
                                            isUploading = false
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Opportunity updated successfully!")
                                                navController.popBackStack()
                                            }
                                        },
                                        onFailure = { errorMsg ->
                                            isUploading = false
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Update failed: $errorMsg")
                                            }
                                        }
                                    )
                                } else {
                                    viewModel.addOpportunity(
                                        opportunity = opportunity,
                                        onSuccess = {
                                            isUploading = false
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Opportunity created successfully!")
                                                navController.popBackStack()
                                            }
                                        },
                                        onFailure = { errorMsg ->
                                            isUploading = false
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Creation failed: $errorMsg")
                                            }
                                        }
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                isUploading = false
                                snackbarHostState.showSnackbar("Image upload failed: ${e.message}")
                            }
                        } else {
                            // No image selected; proceed without uploading
                            val opportunity = Opportunity(
                                id = if (isEdit) opportunityId ?: "" else "",
                                type = opportunityType ?: "Job",
                                companyName = companyName,
                                roleName = roleName,
                                applyLink = applyLink,
                                description = description,
                                imageUrl = existingOpportunity?.imageUrl ?: "",
                                timestamp = if (isEdit) existingOpportunity?.timestamp
                                    ?: Timestamp.now() else Timestamp.now()
                            )
                            if (isEdit) {
                                viewModel.updateOpportunity(
                                    opportunity = opportunity,
                                    onSuccess = {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Opportunity updated successfully!")
                                        }
                                        navController.popBackStack()
                                    },
                                    onFailure = { errorMsg ->
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Update failed: $errorMsg")
                                        }
                                    }
                                )
                            } else {
                                viewModel.addOpportunity(
                                    opportunity = opportunity,
                                    onSuccess = {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Opportunity created successfully!")
                                        }
                                        navController.popBackStack()
                                    },
                                    onFailure = { errorMsg ->
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Creation failed: $errorMsg")
                                        }
                                    }
                                )
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text(if (isEdit) "Update Opportunity" else "Create Opportunity")
            }
        }
    }
}