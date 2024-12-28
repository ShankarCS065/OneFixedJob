// Screens.kt
package com.devlopershankar.onefixedjob.navigation

sealed class Screens(val route: String) {
    // Authentication Screens
    object SplashScreen : Screens("splash_screen")
    object LoginScreen : Screens("login_screen")
    object ForgotPasswordScreen : Screens("forgot_password_screen")
    object RegisterScreen : Screens("register_screen")

    // Main App Screens
    object DashboardScreen : Screens("dashboard_screen")
    object UserProfileScreen : Screens("user_profile_screen")
    object ProfileCreationScreen : Screens("profile_creation_screen")
    object SettingsScreen : Screens("settings_screen")
    object InternshipScreen : Screens("internship_screen")
    object JobScreen : Screens("job_screen")
    object MoreScreen : Screens("more_screen")
    object NotificationScreen : Screens("notification_screen")
    object HelpScreen : Screens("help_screen")

    // JobDetailScreen with applyLink argument
    object JobDetailScreen : Screens("job_detail_screen/{applyLink}") {
        fun createRoute(applyLink: String) = "job_detail_screen/$applyLink"
    }

    object CourseScreen : Screens("course_screen")
    object PracticeScreen : Screens("practice_screen")
    object ChatCreationScreen : Screens("chat_creation_screen")

    // Add more screens as needed
}
