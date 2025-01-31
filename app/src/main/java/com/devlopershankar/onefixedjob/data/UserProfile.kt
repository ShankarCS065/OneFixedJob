package com.devlopershankar.onefixedjob.data

/**
 * Data class mapping the user's profile.
 * Firestore will map these fields from/to the "users/{uid}" document.
 */
data class UserProfile(
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val address: String = "",
    val state: String = "",
    val pincode: String = "",
    val district: String = "",
    val collegeName: String = "",
    val branch: String = "",
    val course: String = "",
    val passOutYear: String = "",
    val profileImageUrl: String? = null,
    val resumeUrl: String? = null,
    val resumeFilename: String? = null,

    // The 'isAdmin' field:
    val isAdmin: Boolean = false
)
