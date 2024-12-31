// AdminScreen.kt
package com.devlopershankar.onefixedjob.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.devlopershankar.onefixedjob.R
import com.devlopershankar.onefixedjob.ui.viewmodel.OpportunityViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.devlopershankar.onefixedjob.ui.model.Opportunity
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    navController: NavController,
    opportunityViewModel: OpportunityViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // UI states
    var type by remember { mutableStateOf("Job") } // Default type
    var companyName by remember { mutableStateOf("") }
    var roleName by remember { mutableStateOf("") }
    var applyLink by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isRecommended by remember { mutableStateOf(false) }

    // Image states
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

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
                    android.graphics.BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                }
                imageBitmap = bitmap
            }
        }
    )

    // Collect events from the ViewModel
    LaunchedEffect(key1 = opportunityViewModel) {
        opportunityViewModel.eventFlow.collectLatest { event ->
            when (event) {
                is OpportunityViewModel.UiEvent.ShowToast -> {
                    // Show Snackbar
                    snackbarHostState.showSnackbar(event.message)
                }
                is OpportunityViewModel.UiEvent.ShowError -> {
                    // Show error message in Snackbar
                    snackbarHostState.showSnackbar(event.message)
                }
                is OpportunityViewModel.UiEvent.AddSuccess -> {
                    // Show success message in Snackbar and clear fields
                    snackbarHostState.showSnackbar("Opportunity added successfully!")
                    companyName = ""
                    roleName = ""
                    applyLink = ""
                    description = ""
                    imageUri = null
                    imageBitmap = null
                    isRecommended = false
                }
                is OpportunityViewModel.UiEvent.UpdateSuccess -> {
                    // Handle update success if necessary
                    snackbarHostState.showSnackbar("Opportunity updated successfully!")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Panel") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
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
            verticalArrangement = Arrangement.Top
        ) {
            Text("Add New Opportunity", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            // Dropdown for Opportunity Type
            var expanded by remember { mutableStateOf(false) }
            val types = listOf("Job", "Internship", "Course", "Practice")

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    readOnly = true,
                    label = { Text("Opportunity Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    types.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(text = selectionOption) },
                            onClick = {
                                type = selectionOption
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Company Name
            OutlinedTextField(
                value = companyName,
                onValueChange = { companyName = it },
                label = { Text("Company Name") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Role Name
            OutlinedTextField(
                value = roleName,
                onValueChange = { roleName = it },
                label = { Text("Role Name") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Apply Link
            OutlinedTextField(
                value = applyLink,
                onValueChange = { applyLink = it },
                label = { Text("Apply Link (URL)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )

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
                        ?: if (imageBitmap != null) "Image Selected" else "No Image Selected"
                )
            }

            // Display selected image preview
            imageBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap,
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Recommended Switch
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Feature on Dashboard")
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = isRecommended,
                    onCheckedChange = { isRecommended = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Add Button
            Button(
                onClick = {
                    // Validate inputs
                    if (companyName.isBlank() || roleName.isBlank() || applyLink.isBlank()) {
                        // Show error message in Snackbar
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Please fill in all required fields.")
                        }
                    } else {
                        coroutineScope.launch {
                            val opportunity = Opportunity(
                                type = type,
                                companyName = companyName,
                                roleName = roleName,
                                applyLink = applyLink,
                                description = description,
                                imageUrl = "", // Will be updated by ViewModel if image is uploaded
                                timestamp = Timestamp.now(),
                                isRecommended = isRecommended
                            )
                            opportunityViewModel.addOrUpdateOpportunity(
                                opportunity = opportunity,
                                imageUri = imageUri, // Pass the selected image URI
                                isEditMode = false // Since we're adding a new opportunity
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text("Add Opportunity")
            }
        }
    }
}
