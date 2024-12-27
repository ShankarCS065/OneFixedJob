// UserProfileScreen.kt
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.devlopershankar.onefixedjob.R
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
                // Handle other events if necessary
                else -> Unit
            }
        }
    }

    // Collecting isLoading state
    val isLoading by viewModel.isLoading.collectAsState()

    // Collecting profileImageUri and resumeUri
    val profileImageUri by viewModel.profileImageUri.collectAsState()
    val resumeUri by viewModel.resumeUri.collectAsState()

    // States for input fields initialized with current ViewModel data
    var fullName by remember { mutableStateOf(TextFieldValue(viewModel.fullName)) }
    var email by remember { mutableStateOf(TextFieldValue(viewModel.email)) }
    var phoneNumber by remember { mutableStateOf(TextFieldValue(viewModel.phoneNumber)) }
    var dateOfBirth by remember { mutableStateOf(TextFieldValue(viewModel.dateOfBirth)) }
    var gender by remember { mutableStateOf(TextFieldValue(viewModel.gender)) }
    var address by remember { mutableStateOf(TextFieldValue(viewModel.address)) }
    var state by remember { mutableStateOf(TextFieldValue(viewModel.state)) }
    var pincode by remember { mutableStateOf(TextFieldValue(viewModel.pincode)) }
    var district by remember { mutableStateOf(TextFieldValue(viewModel.district)) }

    var collegeName by remember { mutableStateOf(TextFieldValue(viewModel.collegeName)) }
    var branch by remember { mutableStateOf(TextFieldValue(viewModel.branch)) }
    var course by remember { mutableStateOf(TextFieldValue(viewModel.course)) }
    var passOutYear by remember { mutableStateOf(TextFieldValue(viewModel.passOutYear)) }

    var resumeFilename by remember { mutableStateOf(TextFieldValue(viewModel.resumeFilename)) }

    // Launcher for image selection
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Upload the selected image via ViewModel with Context
            viewModel.uploadProfileImage(
                imageUri = it,
                context = context
            )
        }
    }

    // Launcher for PDF selection
    val resumePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Extract filename
            val filename = getFileNameFromUri(context, it)
            resumeFilename = TextFieldValue(filename)
            // Upload the selected resume via ViewModel with Context
            viewModel.uploadResume(
                resumeUriLocal = it,
                filename = filename,
                context = context
            )
        }
    }

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
                            navController.navigate("profile_creation_screen")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.Black
                        ),
                        border = null,
                        shape = RoundedCornerShape(20.dp),
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
                    profileImageUri = profileImageUri,
                    onEditClick = {
                        // Trigger image picker
                        imageLauncher.launch("image/*")
                    }
                )
            }

            // User Details Card
            item {
                UserDetailsCard(
                    fullName = fullName,
                    email = email,
                    phoneNumber = phoneNumber,
                    dateOfBirth = dateOfBirth,
                    gender = gender,
                    address = address,
                    state = state,
                    pincode = pincode,
                    district = district
                )
            }

            // College/University Details Card
            item {
                CollegeDetailsCard(
                    collegeName = collegeName,
                    branch = branch,
                    course = course,
                    passOutYear = passOutYear
                )
            }

            // Resume Card
            item {
                ResumeCard(
                    resumeUri = resumeUri,
                    resumeFilename = resumeFilename.text,
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
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .size(120.dp)
        ) {
            if (profileImageUri != null && profileImageUri.isNotEmpty()) {
                AsyncImage(
                    model = profileImageUri,
                    contentDescription = "User Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.ic_user_placeholder),
                    error = painterResource(id = R.drawable.ic_user_placeholder)
                )
            } else {
                // Placeholder Image
                Image(
                    painter = painterResource(id = R.drawable.ic_user_placeholder),
                    contentDescription = "User Placeholder",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
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
                .background(Color.White, shape = CircleShape)
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
    fullName: TextFieldValue,
    email: TextFieldValue,
    phoneNumber: TextFieldValue,
    dateOfBirth: TextFieldValue,
    gender: TextFieldValue,
    address: TextFieldValue,
    state: TextFieldValue,
    pincode: TextFieldValue,
    district: TextFieldValue
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.LightGray),
        shape = RoundedCornerShape(12.dp),
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
            ProfileDetailItem(label = "Full Name", value = fullName.text)
            Spacer(modifier = Modifier.height(8.dp))
            ProfileDetailItem(label = "Email", value = email.text)
            Spacer(modifier = Modifier.height(8.dp))
            ProfileDetailItem(label = "Phone Number", value = phoneNumber.text)
            Spacer(modifier = Modifier.height(8.dp))
            ProfileDetailItem(label = "Date of Birth", value = dateOfBirth.text)
            Spacer(modifier = Modifier.height(8.dp))
            ProfileDetailItem(label = "Gender", value = gender.text)
            Spacer(modifier = Modifier.height(8.dp))
            ProfileDetailItem(label = "Address", value = address.text)
            Spacer(modifier = Modifier.height(8.dp))
            ProfileDetailItem(label = "State", value = state.text)
            Spacer(modifier = Modifier.height(8.dp))
            ProfileDetailItem(label = "Pincode", value = pincode.text)
            Spacer(modifier = Modifier.height(8.dp))
            ProfileDetailItem(label = "District", value = district.text)
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
    collegeName: TextFieldValue,
    branch: TextFieldValue,
    course: TextFieldValue,
    passOutYear: TextFieldValue
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.LightGray),
        shape = RoundedCornerShape(12.dp),
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
            ProfileDetailItem(label = "College/University", value = collegeName.text)
            Spacer(modifier = Modifier.height(8.dp))
            ProfileDetailItem(label = "Branch", value = branch.text)
            Spacer(modifier = Modifier.height(8.dp))
            ProfileDetailItem(label = "Course/Degree", value = course.text)
            Spacer(modifier = Modifier.height(8.dp))
            ProfileDetailItem(label = "Pass-out Year", value = passOutYear.text)
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
        shape = RoundedCornerShape(12.dp),
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
                                Toast.makeText(context, "No PDF viewer found", Toast.LENGTH_SHORT)
                                    .show()
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

                            Toast.makeText(context, "Resume downloading...", Toast.LENGTH_SHORT)
                                .show()
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
