// Screens.kt
package com.devlopershankar.onefixedjob.navigation

object Screens {
    // Base Screens
    const val SplashScreen = "splash_screen"
    const val LoginScreen = "login_screen"
    const val ForgotPasswordScreen = "forgot_password_screen"
    const val RegisterScreen = "register_screen"
    const val DashboardScreen = "dashboard_screen"
    const val InternshipScreen = "internship_screen"
    const val JobScreen = "job_screen"
    const val CourseScreen = "course_screen"
    const val PracticeScreen = "practice_screen"
    const val MoreScreen = "more_screen"
    const val SettingsScreen = "settings_screen"
    const val UserProfileScreen = "user_profile_screen"
    const val ProfileCreationScreen = "profile_creation_screen"
    const val AdminScreen = "admin_screen"
    const val NotificationScreen = "notification_screen"
    const val ChatCreationScreen = "chat_creation_screen"
    const val HelpScreen = "help_screen"
    // Detail Screens Base Routes
    private const val JobDetailBase = "job_detail"
    private const val InternshipDetailBase = "internship_detail"
    private const val CourseDetailBase = "course_detail"
    private const val PracticeDetailBase = "practice_detail"

    // Create/Edit Opportunity Screens Base Routes
    private const val CreateOpportunityBase = "create_opportunity"
    private const val EditOpportunityBase = "edit_opportunity"

    // Helper Functions to Generate Complete Routes with Parameters
    fun jobDetail(jobId: String) = "$JobDetailBase/$jobId"
    fun internshipDetail(internshipId: String) = "$InternshipDetailBase/$internshipId"
    fun courseDetail(courseId: String) = "$CourseDetailBase/$courseId"
    fun practiceDetail(practiceId: String) = "$PracticeDetailBase/$practiceId"

    fun createOpportunity(type: String) = "$CreateOpportunityBase/$type"
    fun editOpportunity(opportunityId: String) = "$EditOpportunityBase/$opportunityId"
}
