// UserProfileViewModel.kt
package com.devlopershankar.onefixedjob.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devlopershankar.onefixedjob.data.UserProfile
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class UserProfileViewModel : ViewModel() {

    // Firebase Instances
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    // User Data Fields
    var fullName: String = ""
    var email: String = ""
    var phoneNumber: String = ""
    var dateOfBirth: String = ""
    var gender: String = ""
    var address: String = ""
    var state: String = ""
    var pincode: String = ""
    var district: String = ""

    var collegeName: String = ""
    var branch: String = ""
    var course: String = ""
    var passOutYear: String = ""

    var resumeFilename: String = ""

    // StateFlows for Profile Image and Resume URI
    private val _profileImageUri = MutableStateFlow<String?>(null)
    val profileImageUri: StateFlow<String?> = _profileImageUri

    private val _resumeUri = MutableStateFlow<String>("")
    val resumeUri: StateFlow<String> = _resumeUri

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

    /**
     * Method to upload the profile image to Firebase Storage.
     *
     * @param imageUri The URI of the selected profile image.
     * @param context The Context to display Toast messages.
     */
    fun uploadProfileImage(imageUri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")

                // Generate a unique filename to prevent overwriting
                val fileName = "profile_images/${userId}_${UUID.randomUUID()}.jpg"

                val storageRef = storage.reference.child(fileName)
                val uploadTask = storageRef.putFile(imageUri).await()

                // Retrieve the download URL of the uploaded image
                val downloadUrl = storageRef.downloadUrl.await()

                // Update the profileImageUri StateFlow
                _profileImageUri.value = downloadUrl.toString()

                // Update Firestore with the new profile image URL using set() with merge
                firestore.collection("users").document(userId)
                    .set(mapOf("profileImageUrl" to downloadUrl.toString()), SetOptions.merge())

                // Emit a success event to notify the UI
                _eventFlow.emit(UiEvent.ShowToast("Profile image uploaded successfully"))
            } catch (e: Exception) {
                e.printStackTrace()
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
     * @param context The Context to display Toast messages.
     */
    fun uploadResume(resumeUriLocal: Uri, filename: String, context: Context) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")

                // Generate a unique filename to prevent overwriting
                val fileName = "resumes/${userId}_${UUID.randomUUID()}_$filename"

                val storageRef = storage.reference.child(fileName)
                val uploadTask = storageRef.putFile(resumeUriLocal).await()

                // Retrieve the download URL of the uploaded resume
                val downloadUrl = storageRef.downloadUrl.await()

                // Update the resumeUri StateFlow
                _resumeUri.value = downloadUrl.toString()
                resumeFilename = filename

                // Update Firestore with the new resume URL and filename using set() with merge
                firestore.collection("users").document(userId)
                    .set(
                        mapOf(
                            "resumeUrl" to downloadUrl.toString(),
                            "resumeFilename" to filename
                        ),
                        SetOptions.merge()
                    )

                // Emit a success event to notify the UI
                _eventFlow.emit(UiEvent.ShowToast("Resume uploaded successfully"))
            } catch (e: Exception) {
                e.printStackTrace()
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
        this.fullName = fullName
        this.email = email
        this.phoneNumber = phoneNumber
        this.dateOfBirth = dateOfBirth
        this.gender = gender
        this.address = address
        this.state = state
        this.pincode = pincode
        this.district = district
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
        this.collegeName = collegeName
        this.branch = branch
        this.course = course
        this.passOutYear = passOutYear
    }

    /**
     * Method to save all user data to Firestore.
     */
    fun saveUserData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")

                val userProfile = UserProfile(
                    fullName = fullName,
                    email = email,
                    phoneNumber = phoneNumber,
                    dateOfBirth = dateOfBirth,
                    gender = gender,
                    address = address,
                    state = state,
                    pincode = pincode,
                    district = district,
                    collegeName = collegeName,
                    branch = branch,
                    course = course,
                    passOutYear = passOutYear,
                    profileImageUrl = _profileImageUri.value,
                    resumeUrl = _resumeUri.value,
                    resumeFilename = resumeFilename
                )

                // Save the UserProfile object to Firestore
                firestore.collection("users").document(userId)
                    .set(userProfile, SetOptions.merge())
                    .await()

                // Emit a success event to notify the UI
                _eventFlow.emit(UiEvent.SaveSuccess)
            } catch (e: Exception) {
                e.printStackTrace()
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
            } catch (e: Exception) {
                e.printStackTrace()
                // Emit an error event to notify the UI
                _eventFlow.emit(UiEvent.ShowError("Failed to logout: ${e.message}"))
            }
        }
    }

    /**
     * Extension function to handle Task.await() similar to Kotlin coroutines.
     *
     * @return The result of the Task.
     * @throws Exception If the Task fails or is canceled.
     */
    suspend fun <T> Task<T>.await(): T {
        return suspendCancellableCoroutine { cont ->
            addOnSuccessListener { result ->
                cont.resume(result)
            }
            addOnFailureListener { exception ->
                cont.resumeWithException(exception)
            }
            addOnCanceledListener {
                cont.cancel()
            }
        }
    }
}
