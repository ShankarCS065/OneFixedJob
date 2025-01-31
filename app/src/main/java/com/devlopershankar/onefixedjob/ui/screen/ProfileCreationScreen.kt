package com.devlopershankar.onefixedjob.ui.screen

import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.*
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
import com.devlopershankar.onefixedjob.navigation.Screens
import com.devlopershankar.onefixedjob.ui.viewmodel.UserProfileViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileCreationScreen(
    navController: NavController,
    viewModel: UserProfileViewModel,
    // Decide if it's a new user or an existing user
    isNewUser: Boolean = false
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Collect events from the ViewModel
    LaunchedEffect(viewModel) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UserProfileViewModel.UiEvent.ShowToast -> {
                    android.widget.Toast
                        .makeText(context, event.message, android.widget.Toast.LENGTH_SHORT)
                        .show()
                }
                is UserProfileViewModel.UiEvent.ShowError -> {
                    android.widget.Toast
                        .makeText(context, event.message, android.widget.Toast.LENGTH_SHORT)
                        .show()
                }
                is UserProfileViewModel.UiEvent.SaveSuccess -> {
                    // We'll handle navigation ourselves after "Save" is pressed
                }
                is UserProfileViewModel.UiEvent.LogoutSuccess -> {
                    // ...
                }
            }
        }
    }

    // Get the userProfile from state
    val userProfile by viewModel.userProfile.collectAsState()

    // We keep local text fields
    var isInitialized by remember { mutableStateOf(false) }

    var fullName by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var phoneNumber by remember { mutableStateOf(TextFieldValue("")) }
    var dateOfBirth by remember { mutableStateOf(TextFieldValue("")) }
    var gender by remember { mutableStateOf(TextFieldValue("")) }
    var address by remember { mutableStateOf(TextFieldValue("")) }
    var state by remember { mutableStateOf(TextFieldValue("")) }
    var pincode by remember { mutableStateOf(TextFieldValue("")) }
    var district by remember { mutableStateOf(TextFieldValue("")) }

    var collegeName by remember { mutableStateOf(TextFieldValue("")) }
    var branch by remember { mutableStateOf(TextFieldValue("")) }
    var course by remember { mutableStateOf(TextFieldValue("")) }
    var passOutYear by remember { mutableStateOf(TextFieldValue("")) }

    var resumeFilename by remember { mutableStateOf(TextFieldValue("")) }

    // Initialize the local fields once from userProfile
    LaunchedEffect(userProfile) {
        if (!isInitialized && userProfile != null) {
            userProfile?.let { profile ->
                fullName = TextFieldValue(profile.fullName)
                email = TextFieldValue(profile.email)
                phoneNumber = TextFieldValue(profile.phoneNumber)
                dateOfBirth = TextFieldValue(profile.dateOfBirth)
                gender = TextFieldValue(profile.gender)
                address = TextFieldValue(profile.address)
                state = TextFieldValue(profile.state)
                pincode = TextFieldValue(profile.pincode)
                district = TextFieldValue(profile.district)

                collegeName = TextFieldValue(profile.collegeName)
                branch = TextFieldValue(profile.branch)
                course = TextFieldValue(profile.course)
                passOutYear = TextFieldValue(profile.passOutYear)

                resumeFilename = TextFieldValue(profile.resumeFilename ?: "")
            }
            isInitialized = true
        }
    }

    // We'll get the image/resume from the user
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.uploadProfileImage(it)
        }
    }
    val pdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val filename = getFileNameFromUri(context, it)
            resumeFilename = TextFieldValue(filename)
            viewModel.uploadResume(it, filename)
        }
    }

    // Observe isLoading
    val isLoading by viewModel.isLoading.collectAsState()

    // Display the image/resume
    val profileImageUri = userProfile?.profileImageUrl ?: ""
    val resumeUri = userProfile?.resumeUrl ?: ""

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
                title = { Text("Create Profile", color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .clickable {
                        // Launch the image picker
                        imagePicker.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                if (profileImageUri.isNotEmpty()) {
                    AsyncImage(
                        model = profileImageUri,
                        contentDescription = "Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.ic_user_placeholder),
                        error = painterResource(id = R.drawable.ic_user_placeholder)
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_user_placeholder),
                        contentDescription = "Placeholder",
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Personal Details
            Text(
                text = "Personal Details",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = dateOfBirth,
                onValueChange = { dateOfBirth = it },
                label = { Text("Date of Birth") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("DD/MM/YYYY") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = gender,
                onValueChange = { gender = it },
                label = { Text("Gender") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state,
                onValueChange = { state = it },
                label = { Text("State") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = pincode,
                onValueChange = { pincode = it },
                label = { Text("Pincode") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = district,
                onValueChange = { district = it },
                label = { Text("District") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            // College Details
            Text(
                text = "College/University Details",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = collegeName,
                onValueChange = { collegeName = it },
                label = { Text("College/University") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = branch,
                onValueChange = { branch = it },
                label = { Text("Branch") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = course,
                onValueChange = { course = it },
                label = { Text("Course/Degree") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = passOutYear,
                onValueChange = { passOutYear = it },
                label = { Text("Pass-out Year") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Resume
            Text(
                text = "Resume Details",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Upload Resume Button
            Button(
                onClick = { pdfPicker.launch("application/pdf") },
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

            // Read-only text for resume filename
            OutlinedTextField(
                value = resumeFilename,
                onValueChange = { /* do nothing, read-only */ },
                label = { Text("Resume Filename") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (resumeUri.isNotEmpty()) {
                            val uri = Uri.parse(resumeUri)
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
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
                readOnly = true
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        // 1) Update local data in ViewModel
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

                        // 2) Save to Firestore
                        viewModel.saveUserData()

                        // 3) If it's a new user, go to Login,
                        //    otherwise go back to UserProfile
                        if (isNewUser) {
                            navController.navigate(Screens.LoginScreen) {
                                popUpTo(Screens.ProfileCreationScreen) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screens.UserProfileScreen) {
                                popUpTo(Screens.ProfileCreationScreen) { inclusive = true }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text("Save")
            }
        }
    }
}

/**
 * Helper function to get the filename from URI
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
