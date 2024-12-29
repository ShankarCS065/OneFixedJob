// Opportunity.kt
package com.devlopershankar.onefixedjob.ui.model

import com.google.firebase.Timestamp

data class Opportunity(
    val id: String = "",
    val type: String = "Job", // Default type
    val companyName: String = "",
    val roleName: String = "",
    val applyLink: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val timestamp: Timestamp = Timestamp.now()
)
