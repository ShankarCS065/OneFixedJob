package com.devlopershankar.onefixedjob.ui.viewmodel

import android.net.Uri
import android.util.Log
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

class UserProfileViewModel : ViewModel() {

    // Firebase Instances
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    // StateFlow for UserProfile
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    // Loading State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Admin State (if you want to differentiate admin vs. normal user)
    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin

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

    private var authStateListener: FirebaseAuth.AuthStateListener? = null

    companion object {
        private const val TAG = "UserProfileViewModel"
    }

    init {
        // Observe Firebase Auth state changes
        observeAuthState()
        // Fetch user profile if someone is signed in
        fetchUserProfile()
    }

    /**
     * Observes Auth state changes.
     * If user == null (no user logged in), we DO NOT emit LogoutSuccess automatically.
     */
    private fun observeAuthState() {
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                Log.d(TAG, "User signed in: ${user.uid}")
                fetchUserAdminStatus()
                fetchUserProfile()
            } else {
                // No user is logged in (e.g., first app open or user manually logged out)
                _userProfile.value = null
                _isAdmin.value = false
                Log.d(TAG, "No user is logged in.")
                // IMPORTANT: we do NOT emit UiEvent.LogoutSuccess here anymore
            }
        }
        auth.addAuthStateListener(authStateListener!!)
    }

    /**
     * Fetches whether the current user is an admin, if doc is found.
     * If the doc doesn't exist (new user), we skip the "User profile not found" error.
     */
    private fun fetchUserAdminStatus() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            viewModelScope.launch {
                try {
                    val userDoc = firestore.collection("users")
                        .document(currentUser.uid)
                        .get()
                        .await()
                    if (userDoc.exists()) {
                        val adminStatus = userDoc.getBoolean("isAdmin") ?: false
                        _isAdmin.value = adminStatus
                    } else {
                        // The document doesn't exist yet (new user).
                        _isAdmin.value = false
                        // We do NOT emit "User profile not found."
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _isAdmin.value = false
                    _eventFlow.emit(UiEvent.ShowError("Failed to fetch admin status: ${e.message}"))
                }
            }
        } else {
            _isAdmin.value = false
        }
    }

    /**
     * Fetches the current user's profile from Firestore (if it exists).
     * If no doc, we do not treat it as an error — we just keep profile = null.
     */
    private fun fetchUserProfile() {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                try {
                    val documentSnapshot = firestore.collection("users")
                        .document(currentUser.uid)
                        .get()
                        .await()

                    if (documentSnapshot.exists()) {
                        val profile = documentSnapshot.toObject(UserProfile::class.java)
                        _userProfile.value = profile
                    } else {
                        // No doc yet => new user, or user hasn't saved profile
                        _userProfile.value = null
                        _isAdmin.value = false
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching user profile: ${e.localizedMessage}", e)
                    _userProfile.value = null
                    _isAdmin.value = false
                } finally {
                    _isLoading.value = false
                }
            } else {
                _userProfile.value = null
                _isAdmin.value = false
                _isLoading.value = false
            }
        }
    }

    /**
     * Uploads the user's profile image to Firebase Storage,
     * then saves the resulting downloadURL to Firestore, and updates local stateFlow.
     */
    fun uploadProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val userId = auth.currentUser?.uid
                    ?: throw Exception("User not authenticated")

                // 1) Create a unique path in Storage
                val fileName = "profile_images/${userId}_${UUID.randomUUID()}.jpg"
                val storageRef = storage.reference.child(fileName)

                // 2) Upload file
                storageRef.putFile(imageUri).await()

                // 3) Get download URL
                val downloadUrl = storageRef.downloadUrl.await()

                // 4) Save the new image URL to Firestore
                firestore.collection("users").document(userId)
                    .set(
                        mapOf("profileImageUrl" to downloadUrl.toString()),
                        SetOptions.merge()
                    )
                    .await()

                // 5) Update local stateFlow
                val updatedProfile = _userProfile.value?.copy(profileImageUrl = downloadUrl.toString())
                _userProfile.value = updatedProfile

                // Show success toast
                _eventFlow.emit(UiEvent.ShowToast("Profile image uploaded successfully"))
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowError("Failed to upload profile image: ${e.message}"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Uploads the user's resume to Firebase Storage,
     * then saves the downloadURL to Firestore, and updates local stateFlow.
     */
    fun uploadResume(resumeUriLocal: Uri, filename: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val userId = auth.currentUser?.uid
                    ?: throw Exception("User not authenticated")

                // 1) Create a unique path
                val fileName = "resumes/${userId}_${UUID.randomUUID()}_$filename"
                val storageRef = storage.reference.child(fileName)

                // 2) Upload file
                storageRef.putFile(resumeUriLocal).await()

                // 3) Get download URL
                val downloadUrl = storageRef.downloadUrl.await()

                // 4) Save to Firestore
                firestore.collection("users").document(userId)
                    .set(
                        mapOf(
                            "resumeUrl" to downloadUrl.toString(),
                            "resumeFilename" to filename
                        ),
                        SetOptions.merge()
                    ).await()

                // 5) Update local stateFlow
                val updatedProfile = _userProfile.value?.copy(
                    resumeUrl = downloadUrl.toString(),
                    resumeFilename = filename
                )
                _userProfile.value = updatedProfile

                // Show success
                _eventFlow.emit(UiEvent.ShowToast("Resume uploaded successfully"))
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowError("Failed to upload resume: ${e.message}"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Updates the user’s personal details in local stateFlow.
     * Actual Firestore save happens when saveUserData() is called.
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
        val updatedProfile = _userProfile.value?.copy(
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
        _userProfile.value = updatedProfile
    }

    /**
     * Updates the user’s college details in local stateFlow.
     * Actual Firestore save happens when saveUserData() is called.
     */
    fun updateCollegeDetails(
        collegeName: String,
        branch: String,
        course: String,
        passOutYear: String
    ) {
        val updatedProfile = _userProfile.value?.copy(
            collegeName = collegeName,
            branch = branch,
            course = course,
            passOutYear = passOutYear
        )
        _userProfile.value = updatedProfile
    }

    /**
     * Saves the local userProfile to Firestore using SetOptions.merge().
     */
    fun saveUserData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val userId = auth.currentUser?.uid
                    ?: throw Exception("User not authenticated")

                val userProfile = _userProfile.value
                    ?: throw Exception("User profile data is null")

                firestore.collection("users").document(userId)
                    .set(userProfile, SetOptions.merge())
                    .await()

                // Notify UI that we have saved the data
                _eventFlow.emit(UiEvent.SaveSuccess)
            } catch (e: Exception) {
                Log.e(TAG, "Error saving user data: ${e.message}", e)
                _eventFlow.emit(UiEvent.ShowError("Failed to save profile: ${e.message}"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Logs out the currently signed-in user explicitly.
     * Emitting UiEvent.LogoutSuccess only here ensures we don't show
     * "Logout successfully" at app startup when there's no user.
     */
    fun logout() {
        viewModelScope.launch {
            try {
                auth.signOut()
                _eventFlow.emit(UiEvent.LogoutSuccess)
            } catch (e: Exception) {
                Log.e(TAG, "Error logging out: ${e.message}", e)
                _eventFlow.emit(UiEvent.ShowError("Failed to logout: ${e.message}"))
            }
        }
    }
}
