package com.devlopershankar.onefixedjob.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.devlopershankar.onefixedjob.navigation.Screens
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(2000) // 2-second splash

        // Check if user is logged in
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            // Navigate to Dashboard
            navController.navigate(Screens.DashboardScreen) {
                popUpTo(Screens.SplashScreen) { inclusive = true }
            }
        } else {
            // Go back to Login
            navController.navigate(Screens.LoginScreen) {
                popUpTo(Screens.SplashScreen) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Job App", style = MaterialTheme.typography.headlineLarge)
    }
}
