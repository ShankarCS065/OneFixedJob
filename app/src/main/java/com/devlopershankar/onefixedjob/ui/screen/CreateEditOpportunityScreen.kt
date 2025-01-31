// CreateEditOpportunityScreen.kt
package com.devlopershankar.onefixedjob.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import com.devlopershankar.onefixedjob.R
import com.devlopershankar.onefixedjob.ui.viewmodel.OpportunityViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import java.io.IOException
import com.devlopershankar.onefixedjob.ui.model.Opportunity

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

    // States for input fields
    var companyName by remember { mutableStateOf("") }
    var roleName by remember { mutableStateOf("") }
    var applyLink by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var isRecommended by remember { mutableStateOf(false) }

    // States for new fields
    var batch by remember { mutableStateOf("") }
    var jobType by remember { mutableStateOf("Full-time") }

    // State for opportunity type (only in creation mode)
    var selectedType by remember { mutableStateOf(opportunityType ?: "Job") }
    var expandedType by remember { mutableStateOf(false) }

    // State for job type dropdown
    var expandedJobType by remember { mutableStateOf(false) }
    val jobTypes = listOf("Full-time", "Part-time", "Hybrid", "Remote")

    // State for batch (only for Courses)
    var showBatch by remember { mutableStateOf(false) }

    // Existing opportunity data (only in edit mode)
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
        // Optionally, display a placeholder or nothing while redirecting
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
                isRecommended = it.isRecommended
                jobType = it.jobType
                batch = it.batch
                // If editing, set selectedType to existing type
                selectedType = it.type
                // Determine if batch should be shown
                showBatch = it.type == "Course"
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

    // Collect events from the ViewModel
    LaunchedEffect(key1 = viewModel) {
        viewModel.eventFlow.collectLatest { event ->
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
                    batch = ""
                    jobType = "Full-time"
                    selectedType = opportunityType ?: "Job"
                    navController.popBackStack()
                }

                is OpportunityViewModel.UiEvent.UpdateSuccess -> {
                    // Show success message in Snackbar and navigate back
                    snackbarHostState.showSnackbar("Opportunity updated successfully!")
                    navController.popBackStack()
                }
            }
        }
    }

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
            // Opportunity Type is fixed based on whether it's creation or editing
            if (!isEdit) {
                // Dropdown for Opportunity Type (only during creation)
                ExposedDropdownMenuBox(
                    expanded = expandedType,
                    onExpandedChange = { expandedType = it }
                ) {
                    OutlinedTextField(
                        value = selectedType,
                        onValueChange = { /* No-op */ },
                        readOnly = true,
                        label = { Text("Opportunity Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedType,
                        onDismissRequest = { expandedType = false }
                    ) {
                        listOf(
                            "Job",
                            "Internship",
                            "Course",
                            "Practice"
                        ).forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(text = selectionOption) },
                                onClick = {
                                    selectedType = selectionOption
                                    expandedType = false
                                    // Show or hide Batch field based on selected type
                                    showBatch = selectionOption == "Course"
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            } else {
                // If editing, show the type as read-only
                OutlinedTextField(
                    value = existingOpportunity?.type ?: "Job",
                    onValueChange = {},
                    label = { Text("Opportunity Type") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    enabled = false,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    )
                )
            }

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

            // Description
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

            // Batch (Only for Courses)
            if (showBatch) {
                OutlinedTextField(
                    value = batch,
                    onValueChange = { batch = it },
                    label = { Text("Batch") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    )
                )
            }

            // Job Type (Only for Jobs)
            if ((!isEdit && selectedType == "Job") || (isEdit && existingOpportunity?.type == "Job")) {
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
            }

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

            // Display selected image preview or existing image
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
            } else if (isEdit && existingOpportunity?.imageUrl?.isNotEmpty() == true && !showBatch && (selectedType != "Course")) {
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

            // Toggle for isRecommended
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Feature on Dashboard")
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = isRecommended,
                    onCheckedChange = { isRecommended = it }
                )
            }

            // Add/Edit Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        // Validate inputs
                        if (!isEdit && (opportunityType.isNullOrEmpty())) {
                            snackbarHostState.showSnackbar("Please select an opportunity type.")
                            return@launch
                        }
                        if (companyName.isBlank() || roleName.isBlank() || applyLink.isBlank()) {
                            snackbarHostState.showSnackbar("Please fill in all required fields.")
                            return@launch
                        }

                        // Validate the applyLink
                        if (!isValidUrl(applyLink)) {
                            snackbarHostState.showSnackbar("Please enter a valid Apply Link URL.")
                            return@launch
                        }

                        // Additional validation for batch and jobType if applicable
                        if (selectedType == "Course" && batch.isBlank()) {
                            snackbarHostState.showSnackbar("Please enter the Batch.")
                            return@launch
                        }
                        if (selectedType == "Job" && jobType.isBlank()) {
                            snackbarHostState.showSnackbar("Please select the Job Type.")
                            return@launch
                        }

                        // Create the Opportunity object
                        val opportunity = Opportunity(
                            id = if (isEdit) existingOpportunity?.id ?: "" else "",
                            type = if (isEdit) existingOpportunity?.type
                                ?: "Job" else (selectedType),
                            companyName = companyName,
                            roleName = roleName,
                            applyLink = applyLink,
                            description = description,
                            imageUrl = "", // Will be updated by ViewModel if image is uploaded
                            timestamp = if (isEdit) existingOpportunity?.timestamp
                                ?: Timestamp.now() else Timestamp.now(),
                            isRecommended = isRecommended, // Set isRecommended
                            batch = if (selectedType == "Course") batch else existingOpportunity?.batch
                                ?: "",
                            jobType = if (selectedType == "Job") jobType else existingOpportunity?.jobType
                                ?: "Full-time"
                        )
                        // Call ViewModel's addOrUpdateOpportunity
                        viewModel.addOrUpdateOpportunity(
                            opportunity = opportunity,
                            imageUri = imageUri, // Pass the selected image URI
                            isEditMode = isEdit // false for create, true for edit
                        )
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
    /**
     * Utility function to validate URLs.
     */
    fun isValidUrl(url: String): Boolean {
        return android.util.Patterns.WEB_URL.matcher(url).matches()
    }

