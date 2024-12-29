package com.devlopershankar.onefixedjob.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.devlopershankar.onefixedjob.navigation.Screens
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun LoginScreen(navController: NavController) {
    // Firebase auth
    val auth = Firebase.auth

    // UI states
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Email/Password login
    val loginWithEmailPassword: () -> Unit = {
        auth.signInWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Navigate to Splash

                    navController.navigate(Screens.DashboardScreen) {
                        popUpTo(Screens.LoginScreen) { inclusive = true }
                    }
                } else {
                    // Handle error
                    task.exception?.let {
                        Log.e("FirebaseAuth", "Login failed: ${it.localizedMessage}")
                        errorMessage = "Login failed: ${it.localizedMessage}"
                    }
                }
            }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to Job App", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Login button
        Button(
            onClick = loginWithEmailPassword,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display error (if any)
        errorMessage?.let {
            Snackbar(
                action = {
                    TextButton(onClick = { errorMessage = null }) {
                        Text("Dismiss")
                    }
                }
            ) { Text(it) }
        }

        // Forgot password
        TextButton(onClick = {
            if (email.isNotBlank()) {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("FirebaseAuth", "Password reset email sent.")
                        } else {
                            Log.e(
                                "FirebaseAuth",
                                "Error sending password reset email: ${task.exception?.localizedMessage}"
                            )
                        }
                    }
            } else {
                // If email is empty, you might show a warning
                Log.e("FirebaseAuth", "Cannot send reset email, email field is empty.")
            }
        }){
            TextButton(onClick = {
                navController.navigate(Screens.ForgotPasswordScreen)
            }) {
                Text("Forgot Password?")
            }

        }

        // Registration link
        TextButton(onClick = {
            navController.navigate(Screens.RegisterScreen)
        }) {
            Text("New Register?")
        }
    }
}
