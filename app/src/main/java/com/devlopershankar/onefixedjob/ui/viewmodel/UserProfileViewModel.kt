// UserProfileViewModel.kt
package com.devlopershankar.onefixedjob.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devlopershankar.onefixedjob.data.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import android.util.Log

class UserProfileViewModel : ViewModel() {

    // Firebase Instances
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    // StateFlow for UserProfile (Non-Nullable)
    private val _userProfile = MutableStateFlow<UserProfile>(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile

    // Loading State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Event Flow for UI Events
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow

    // UI Events Sealed Class
    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
        object SaveSuccess : UiEvent()
        object LogoutSuccess : UiEvent()
        data class ShowError(val message: String) : UiEvent()
    }

    init {
        // Fetch user profile data upon ViewModel initialization
        fetchUserProfile()
    }

    /**
     * Method to fetch user profile data from Firestore.
     */
    private fun fetchUserProfile() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val userId = auth.currentUser?.uid
                    ?: throw Exception("User not authenticated")

                Log.d(TAG, "Fetching user profile for userId: $userId")

                // Fetch the user document from Firestore
                firestore.collection("users").document(userId).get().await().let { document ->
                    if (document.exists()) {
                        Log.d(TAG, "User document exists. Parsing data.")
                        val userProfileData = document.toObject(UserProfile::class.java)
                            ?: throw Exception("Failed to parse user profile data")
                        _userProfile.value = userProfileData
                        Log.d(TAG, "User profile fetched successfully: $userProfileData")
                    } else {
                        Log.d(TAG, "User document does not exist. Initializing profile.")
                        // Initialize user profile if document doesn't exist
                        initializeUserProfile(userId)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching user profile: ${e.message}", e)
                _eventFlow.emit(UiEvent.ShowError("Failed to fetch profile: ${e.message}"))
            } finally {
                _isLoading.value = false
                Log.d(TAG, "Loading state set to false.")
            }
        }
    }

    /**
     * Method to initialize user profile in Firestore.
     */
    private suspend fun initializeUserProfile(userId: String) {
        try {
            val initialData = UserProfile(
                fullName = "Your Name",
                email = auth.currentUser?.email ?: "your.email@example.com",
                phoneNumber = "1234567890"
                // Other fields remain as default empty strings
            )
            firestore.collection("users").document(userId)
                .set(initialData, SetOptions.merge()).await()
            _userProfile.value = initialData
            _eventFlow.emit(UiEvent.ShowToast("User profile initialized. Please complete your details."))
            Log.d(TAG, "User profile initialized with default data.")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing user profile: ${e.message}", e)
            _eventFlow.emit(UiEvent.ShowError("Failed to initialize profile: ${e.message}"))
        }
    }

    /**
     * Method to upload the profile image to Firebase Storage.
     *
     * @param imageUri The URI of the selected profile image.
     */
    fun uploadProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val userId = auth.currentUser?.uid
                    ?: throw Exception("User not authenticated")

                Log.d(TAG, "Uploading profile image for userId: $userId")

                // Generate a unique filename to prevent overwriting
                val fileName = "profile_images/${userId}_${UUID.randomUUID()}.jpg"

                val storageRef = storage.reference.child(fileName)
                storageRef.putFile(imageUri).await()

                // Retrieve the download URL of the uploaded image
                val downloadUrl = storageRef.downloadUrl.await()

                // Update Firestore with the new profile image URL using set() with merge
                firestore.collection("users").document(userId)
                    .set(mapOf("profileImageUrl" to downloadUrl.toString()), SetOptions.merge())
                    .await()

                // Update the UserProfile StateFlow
                val updatedProfile = _userProfile.value.copy(profileImageUrl = downloadUrl.toString())
                _userProfile.value = updatedProfile

                Log.d(TAG, "Profile image uploaded successfully: $downloadUrl")

                // Emit a success event to notify the UI
                _eventFlow.emit(UiEvent.ShowToast("Profile image uploaded successfully"))
            } catch (e: Exception) {
                Log.e(TAG, "Error uploading profile image: ${e.message}", e)
                // Emit an error event to notify the UI
                _eventFlow.emit(UiEvent.ShowError("Failed to upload profile image: ${e.message}"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Method to upload the resume PDF to Firebase Storage.
     *
     * @param resumeUriLocal The URI of the selected resume PDF.
     * @param filename The name of the resume file.
     */
    fun uploadResume(resumeUriLocal: Uri, filename: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val userId = auth.currentUser?.uid
                    ?: throw Exception("User not authenticated")

                Log.d(TAG, "Uploading resume for userId: $userId")

                // Generate a unique filename to prevent overwriting
                val fileName = "resumes/${userId}_${UUID.randomUUID()}_$filename"

                val storageRef = storage.reference.child(fileName)
                storageRef.putFile(resumeUriLocal).await()

                // Retrieve the download URL of the uploaded resume
                val downloadUrl = storageRef.downloadUrl.await()

                // Update Firestore with the new resume URL and filename using set() with merge
                firestore.collection("users").document(userId)
                    .set(
                        mapOf(
                            "resumeUrl" to downloadUrl.toString(),
                            "resumeFilename" to filename
                        ),
                        SetOptions.merge()
                    ).await()

                // Update the UserProfile StateFlow
                val updatedProfile = _userProfile.value.copy(
                    resumeUrl = downloadUrl.toString(),
                    resumeFilename = filename
                )
                _userProfile.value = updatedProfile

                Log.d(TAG, "Resume uploaded successfully: $downloadUrl")

                // Emit a success event to notify the UI
                _eventFlow.emit(UiEvent.ShowToast("Resume uploaded successfully"))
            } catch (e: Exception) {
                Log.e(TAG, "Error uploading resume: ${e.message}", e)
                // Emit an error event to notify the UI
                _eventFlow.emit(UiEvent.ShowError("Failed to upload resume: ${e.message}"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Method to update user personal details.
     *
     * @param fullName The user's full name.
     * @param email The user's email address.
     * @param phoneNumber The user's phone number.
     * @param dateOfBirth The user's date of birth.
     * @param gender The user's gender.
     * @param address The user's address.
     * @param state The user's state.
     * @param pincode The user's pincode.
     * @param district The user's district.
     */
    fun updateUserDetails(
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
        // Update the UserProfile StateFlow
        _userProfile.value = _userProfile.value.copy(
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

    /**
     * Method to update college/university details.
     *
     * @param collegeName The name of the college/university.
     * @param branch The user's branch.
     * @param course The user's course or degree.
     * @param passOutYear The user's pass-out year.
     */
    fun updateCollegeDetails(
        collegeName: String,
        branch: String,
        course: String,
        passOutYear: String
    ) {
        // Update the UserProfile StateFlow
        _userProfile.value = _userProfile.value.copy(
            collegeName = collegeName,
            branch = branch,
            course = course,
            passOutYear = passOutYear
        )
    }

    /**
     * Method to save all user data to Firestore.
     */
    fun saveUserData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val userId = auth.currentUser?.uid
                    ?: throw Exception("User not authenticated")

                val userProfile = _userProfile.value
                    ?: throw Exception("User profile data is null")

                Log.d(TAG, "Saving user data for userId: $userId")

                // Save the UserProfile object to Firestore
                firestore.collection("users").document(userId)
                    .set(userProfile, SetOptions.merge())
                    .await()

                // Emit a success event to notify the UI
                _eventFlow.emit(UiEvent.SaveSuccess)
                Log.d(TAG, "User data saved successfully.")

            } catch (e: Exception) {
                Log.e(TAG, "Error saving user data: ${e.message}", e)
                // Emit an error event to notify the UI
                _eventFlow.emit(UiEvent.ShowError("Failed to save profile: ${e.message}"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Method to logout the user.
     */
    fun logout() {
        viewModelScope.launch {
            try {
                auth.signOut()
                // Emit a logout success event to notify the UI
                _eventFlow.emit(UiEvent.LogoutSuccess)

                Log.d(TAG, "User logged out successfully.")
            } catch (e: Exception) {
                Log.e(TAG, "Error logging out: ${e.message}", e)
                // Emit an error event to notify the UI
                _eventFlow.emit(UiEvent.ShowError("Failed to logout: ${e.message}"))
            }
        }
    }

    companion object {
        private const val TAG = "UserProfileViewModel"
    }
}
