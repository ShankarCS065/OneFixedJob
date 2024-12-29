// ProfileCreationScreen.kt
package com.devlopershankar.onefixedjob.ui.screen

import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.devlopershankar.onefixedjob.ui.model.Opportunity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.devlopershankar.onefixedjob.R
import com.devlopershankar.onefixedjob.ui.viewmodel.UserProfileViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileCreationScreen(
    navController: NavController,
    viewModel: UserProfileViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Collect events from the ViewModel
    LaunchedEffect(key1 = viewModel) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UserProfileViewModel.UiEvent.ShowToast -> {
                    // Show toast or Snackbar
                    // Example using Toast:
                    android.widget.Toast.makeText(context, event.message, android.widget.Toast.LENGTH_SHORT).show()
                }
                is UserProfileViewModel.UiEvent.SaveSuccess -> {
                    // Show success message and navigate back or to another screen
                    android.widget.Toast.makeText(context, "Profile saved successfully!", android.widget.Toast.LENGTH_SHORT).show()
                    navController.navigateUp()
                }
                is UserProfileViewModel.UiEvent.ShowError -> {
                    // Show error message
                    android.widget.Toast.makeText(context, event.message, android.widget.Toast.LENGTH_SHORT).show()
                }
                is UserProfileViewModel.UiEvent.LogoutSuccess -> {
                    // Handle logout success if needed
                }
            }
        }
    }

    // States for input fields initialized with current ViewModel data
    val userProfile by viewModel.userProfile.collectAsState()

    // Initialize state variables and update them when userProfile changes
    var isInitialized by remember { mutableStateOf(false) }

    var fullName by remember { mutableStateOf(TextFieldValue(userProfile?.fullName ?: "")) }
    var email by remember { mutableStateOf(TextFieldValue(userProfile?.email ?: "")) }
    var phoneNumber by remember { mutableStateOf(TextFieldValue(userProfile?.phoneNumber ?: "")) }
    var dateOfBirth by remember { mutableStateOf(TextFieldValue(userProfile?.dateOfBirth ?: "")) }
    var gender by remember { mutableStateOf(TextFieldValue(userProfile?.gender ?: "")) }
    var address by remember { mutableStateOf(TextFieldValue(userProfile?.address ?: "")) }
    var state by remember { mutableStateOf(TextFieldValue(userProfile?.state ?: "")) }
    var pincode by remember { mutableStateOf(TextFieldValue(userProfile?.pincode ?: "")) }
    var district by remember { mutableStateOf(TextFieldValue(userProfile?.district ?: "")) }

    var collegeName by remember { mutableStateOf(TextFieldValue(userProfile?.collegeName ?: "")) }
    var branch by remember { mutableStateOf(TextFieldValue(userProfile?.branch ?: "")) }
    var course by remember { mutableStateOf(TextFieldValue(userProfile?.course ?: "")) }
    var passOutYear by remember { mutableStateOf(TextFieldValue(userProfile?.passOutYear ?: "")) }

    var resumeFilename by remember { mutableStateOf(TextFieldValue(userProfile?.resumeFilename ?: "")) }

    // Update state variables when userProfile changes (only once)
    LaunchedEffect(userProfile) {
        if (!isInitialized && userProfile != null) {
            fullName = TextFieldValue(userProfile!!.fullName)
            email = TextFieldValue(userProfile!!.email)
            phoneNumber = TextFieldValue(userProfile!!.phoneNumber)
            dateOfBirth = TextFieldValue(userProfile!!.dateOfBirth)
            gender = TextFieldValue(userProfile!!.gender)
            address = TextFieldValue(userProfile!!.address)
            state = TextFieldValue(userProfile!!.state)
            pincode = TextFieldValue(userProfile!!.pincode)
            district = TextFieldValue(userProfile!!.district)

            collegeName = TextFieldValue(userProfile!!.collegeName)
            branch = TextFieldValue(userProfile!!.branch)
            course = TextFieldValue(userProfile!!.course)
            passOutYear = TextFieldValue(userProfile!!.passOutYear)

            resumeFilename = TextFieldValue(userProfile!!.resumeFilename)

            isInitialized = true
        }
    }

    // Launcher for image selection
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Upload the selected image via ViewModel
            viewModel.uploadProfileImage(
                imageUri = it
            )
        }
    }

    // Launcher for PDF selection
    val pdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Extract filename
            val filename = getFileNameFromUri(context, it)
            resumeFilename = TextFieldValue(filename)
            // Upload the selected resume via ViewModel
            viewModel.uploadResume(
                resumeUriLocal = it,
                filename = filename
            )
        }
    }

    // Collect isLoading state as State
    val isLoading by viewModel.isLoading.collectAsState()

    // Collect resumeUri state as State
    val resumeUri = userProfile?.resumeUrl ?: ""

    // Collect profileImageUri state as State
    val profileImageUri = userProfile?.profileImageUrl ?: ""

    // Loading Indicator
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = "Create Profile", color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                ),
                modifier = Modifier.height(56.dp)
            )
        },
        containerColor = Color.White,
        contentColor = Color.Black
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image Selection
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .clickable {
                        // Launch the image picker
                        imageLauncher.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                if (profileImageUri.isNotEmpty()) {
                    AsyncImage(
                        model = profileImageUri,
                        contentDescription = "Selected Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.ic_user_placeholder),
                        error = painterResource(id = R.drawable.ic_user_placeholder)
                    )
                } else {
                    // Placeholder Icon
                    Icon(
                        painter = painterResource(id = R.drawable.ic_user_placeholder),
                        contentDescription = "Profile Placeholder",
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // User Details Inputs
            Text(
                text = "Personal Details",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = dateOfBirth,
                onValueChange = { dateOfBirth = it },
                label = { Text("Date of Birth") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "DD/MM/YYYY") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = gender,
                onValueChange = { gender = it },
                label = { Text("Gender") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state,
                onValueChange = { state = it },
                label = { Text("State") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = pincode,
                onValueChange = { pincode = it },
                label = { Text("Pincode") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = district,
                onValueChange = { district = it },
                label = { Text("District") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )
            Spacer(modifier = Modifier.height(24.dp))

            // College Details Inputs
            Text(
                text = "College/University Details",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = collegeName,
                onValueChange = { collegeName = it },
                label = { Text("College/University") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = branch,
                onValueChange = { branch = it },
                label = { Text("Branch") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = course,
                onValueChange = { course = it },
                label = { Text("Course/Degree") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = passOutYear,
                onValueChange = { passOutYear = it },
                label = { Text("Pass-out Year") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Resume Details Inputs
            Text(
                text = "Resume Details",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Update Resume Button
            Button(
                onClick = {
                    // Launch the PDF picker
                    pdfLauncher.launch("application/pdf")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
            ) {
                Icon(
                    imageVector = Icons.Filled.Upload,
                    contentDescription = "Upload Resume",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Update Resume",
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Resume Filename Display (Read-only)
            OutlinedTextField(
                value = resumeFilename,
                onValueChange = { /* Read-only */ },
                label = { Text("Resume Filename") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (resumeUri.isNotEmpty()) {
                            val uri = Uri.parse(resumeUri)
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                android.widget.Toast.makeText(
                                    context,
                                    "No PDF viewer found",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            android.widget.Toast.makeText(
                                context,
                                "No resume uploaded",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                readOnly = true,
                trailingIcon = {
                    if (resumeUri.isNotEmpty()) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_pdf_placeholder),
                            contentDescription = "View Resume",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    val uri = Uri.parse(resumeUri)
                                    val intent = Intent(Intent.ACTION_VIEW, uri)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    try {
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        android.widget.Toast.makeText(
                                            context,
                                            "No PDF viewer found",
                                            android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        // Update ViewModel with new data
                        viewModel.updateUserDetails(
                            fullName = fullName.text,
                            email = email.text,
                            phoneNumber = phoneNumber.text,
                            dateOfBirth = dateOfBirth.text,
                            gender = gender.text,
                            address = address.text,
                            state = state.text,
                            pincode = pincode.text,
                            district = district.text
                        )
                        viewModel.updateCollegeDetails(
                            collegeName = collegeName.text,
                            branch = branch.text,
                            course = course.text,
                            passOutYear = passOutYear.text
                        )
                        // Resume details are already updated via the launcher

                        // Save data to Firestore
                        viewModel.saveUserData()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text(text = "Save")
            }
        }
    }
}

/**
 * Helper function to get the filename from URI.
 */
private fun getFileNameFromUri(context: android.content.Context, uri: Uri): String {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1 && it.moveToFirst()) {
            return it.getString(nameIndex)
        }
    }
    return "Resume.pdf" // Default filename if retrieval fails
}
