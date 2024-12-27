// UserProfile.kt
package com.devlopershankar.onefixedjob.data

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
    val resumeUrl: String = "",
    val resumeFilename: String = ""
)
