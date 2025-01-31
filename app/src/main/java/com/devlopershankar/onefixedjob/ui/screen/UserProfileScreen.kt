package com.devlopershankar.onefixedjob.ui.screen

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.devlopershankar.onefixedjob.R
import com.devlopershankar.onefixedjob.data.UserProfile
import com.devlopershankar.onefixedjob.navigation.Screens
import com.devlopershankar.onefixedjob.ui.viewmodel.UserProfileViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    navController: NavController,
    viewModel: UserProfileViewModel
) {
    val context = LocalContext.current

    // Coroutines scope for background operations
    val coroutineScope = rememberCoroutineScope()

    // Collect events from the ViewModel
    LaunchedEffect(key1 = viewModel) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UserProfileViewModel.UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is UserProfileViewModel.UiEvent.SaveSuccess -> {
                    Toast.makeText(context, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
                    navController.navigateUp()
                }
                is UserProfileViewModel.UiEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is UserProfileViewModel.UiEvent.LogoutSuccess -> {
                    // Handle logout success if needed
                }
            }
        }
    }

    // Collecting isLoading state
    val isLoading by viewModel.isLoading.collectAsState()

    // Collecting UserProfile StateFlow (can be null)
    val userProfile by viewModel.userProfile.collectAsState()

    // If userProfile is null, use a default/empty UserProfile to show placeholders
    val currentProfile = userProfile ?: UserProfile()

    // Launcher for image selection
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Upload the selected image via ViewModel
            viewModel.uploadProfileImage(imageUri = it)
        }
    }

    // Launcher for PDF selection
    val resumePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Extract filename
            val filename = getFileNameFromUri(context, it)
            // Upload the selected resume via ViewModel
            viewModel.uploadResume(
                resumeUriLocal = it,
                filename = filename
            )
        }
    }

    // Loading Indicator overlay
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
                title = { Text(text = "User Profile", color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = {

                            navController.navigate(Screens.ProfileCreationScreen + "/true")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.Black
                        ),
                        border = null,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Edit",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
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

        // Main content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Image Card
            item {
                UserImageCard(
                    profileImageUri = currentProfile.profileImageUrl,
                    onEditClick = {
                        // Trigger image picker
                        imageLauncher.launch("image/*")
                    }
                )
            }

            // User Details Card
            item {
                UserDetailsCard(
                    fullName = currentProfile.fullName,
                    email = currentProfile.email,
                    phoneNumber = currentProfile.phoneNumber,
                    dateOfBirth = currentProfile.dateOfBirth,
                    gender = currentProfile.gender,
                    address = currentProfile.address,
                    state = currentProfile.state,
                    pincode = currentProfile.pincode,
                    district = currentProfile.district
                )
            }

            // College/University Details Card
            item {
                CollegeDetailsCard(
                    collegeName = currentProfile.collegeName,
                    branch = currentProfile.branch,
                    course = currentProfile.course,
                    passOutYear = currentProfile.passOutYear
                )
            }

            // Resume Card
            item {
                ResumeCard(
                    resumeUri = currentProfile.resumeUrl ?: "",
                    resumeFilename = currentProfile.resumeFilename ?: "",
                    onUploadClick = {
                        // Trigger resume picker
                        resumePickerLauncher.launch("application/pdf")
                    }
                )
            }
        }
    }
}

/**
 * Composable for displaying the user's profile image with an edit button.
 */
@Composable
fun UserImageCard(profileImageUri: String?, onEditClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.LightGray),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.size(120.dp)
        ) {
            if (!profileImageUri.isNullOrEmpty()) {
                AsyncImage(
                    model = profileImageUri,
                    contentDescription = "User Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.ic_user_placeholder),
                    error = painterResource(id = R.drawable.ic_user_placeholder)
                )
            } else {
                // Show placeholder image if no profile image
                Image(
                    painter = painterResource(id = R.drawable.ic_user_placeholder),
                    contentDescription = "User Placeholder",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Edit Button Overlay
        IconButton(
            onClick = onEditClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-10).dp, y = 10.dp)
                .background(Color.White, shape = MaterialTheme.shapes.small)
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Edit Profile Image",
                tint = Color.Black
            )
        }
    }
}

/**
 * Composable for displaying user details.
 */
@Composable
fun UserDetailsCard(
    fullName: String,
    email: String,
    phoneNumber: String,
    dateOfBirth: String,
    gender: String,
    address: String,
    state: String,
    pincode: String,
    district: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.LightGray),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "User Details",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            ProfileDetailItem(label = "Full Name", value = fullName)
            Spacer(modifier = Modifier.height(8.dp))
            ProfileDetailItem(label = "Email", value = email)
            Spacer(modifier = Modifier.height(8.dp))
            ProfileDetailItem(label = "Phone Number", value = phoneNumber)
            Spacer(modifier = Modifier.height(8.dp))
            ProfileDetailItem(label = "Date of Birth", value = dateOfBirth)
            Spacer(modifier = Modifier.height(8.dp))
            ProfileDetailItem(label = "Gender", value = gender)
            Spacer(modifier = Modifier.height(8.dp))
            ProfileDetailItem(label = "Address", value = address)
            Spacer(modifier = Modifier.height(8.dp))
            ProfileDetailItem(label = "State", value = state)
            Spacer(modifier = Modifier.height(8.dp))
            ProfileDetailItem(label = "Pincode", value = pincode)
            Spacer(modifier = Modifier.height(8.dp))
            ProfileDetailItem(label = "District", value = district)
        }
    }
}

/**
 * Composable for displaying a single profile detail item.
 */
@Composable
fun ProfileDetailItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.Black
        )
        Text(
            text = if (value.isNotEmpty()) value else "-",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )
    }
}

/**
 * Composable for displaying college/university details.
 */
@Composable
fun CollegeDetailsCard(
    collegeName: String,
    branch: String,
    course: String,
    passOutYear: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.LightGray),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "College/University Details",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            ProfileDetailItem(label = "College/University", value = collegeName)
            Spacer(modifier = Modifier.height(8.dp))
            ProfileDetailItem(label = "Branch", value = branch)
            Spacer(modifier = Modifier.height(8.dp))
            ProfileDetailItem(label = "Course/Degree", value = course)
            Spacer(modifier = Modifier.height(8.dp))
            ProfileDetailItem(label = "Pass-out Year", value = passOutYear)
        }
    }
}

/**
 * Composable for displaying resume information with options to view, download, and upload.
 */
@Composable
fun ResumeCard(
    resumeUri: String,
    resumeFilename: String,
    onUploadClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.LightGray),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Resume",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_pdf_placeholder),
                    contentDescription = "Resume PDF",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 8.dp)
                )
                Text(
                    text = if (resumeFilename.isNotEmpty()) resumeFilename else "No Resume Uploaded",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // View Button
                OutlinedButton(
                    onClick = {
                        if (resumeUri.isNotEmpty()) {
                            val uri = Uri.parse(resumeUri)
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.setDataAndType(uri, "application/pdf")
                            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY or
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "No PDF viewer found", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "No resume uploaded", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Black,
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(1.dp, Color.Black)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = "View Resume",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "View")
                }

                // Download Button
                Button(
                    onClick = {
                        if (resumeUri.isNotEmpty()) {
                            val uri = Uri.parse(resumeUri)
                            val request = DownloadManager.Request(uri)
                                .setTitle(resumeFilename)
                                .setDescription("Downloading Resume")
                                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                .setDestinationInExternalPublicDir(
                                    Environment.DIRECTORY_DOWNLOADS,
                                    resumeFilename
                                )

                            val downloadManager =
                                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                            downloadManager.enqueue(request)

                            Toast.makeText(context, "Resume downloading...", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "No resume uploaded", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Download,
                        contentDescription = "Download Resume",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Download")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Upload Resume Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onUploadClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Upload,
                        contentDescription = "Upload Resume",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Upload")
                }
            }
        }
    }
}

/**
 * Helper function to get the filename from URI.
 */
private fun getFileNameFromUri(context: Context, uri: Uri): String {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1 && it.moveToFirst()) {
            return it.getString(nameIndex)
        }
    }
    return "Resume.pdf" // Default filename if retrieval fails
}
