// Opportunity.kt
package com.devlopershankar.onefixedjob.ui.model

import com.google.firebase.Timestamp

data class Opportunity(
    val id: String = "",
    val type: String = "Job", // "Job", "Internship", "Course", "Practice"
    val companyName: String = "",
    val roleName: String = "",
    val applyLink: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val isRecommended: Boolean = false,
    val batch: String = "", // New field for Batch
    val jobType: String = "Full-time" // "Full-time", "Part-time", "Hybrid", "Remote"
)
