package com.devlopershankar.onefixedjob.ui.screen

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
fun RegisterScreen(navController: NavController) {
    // Get a reference to Firebase Auth
    val auth = Firebase.auth

    // State variables for user inputs
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Function to register the user
    val registerUser: () -> Unit = {
        if (password == confirmPassword) {
            auth.createUserWithEmailAndPassword(email.trim(), password.trim())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // If registration succeeds, navigate to the DashboardScreen
                        navController.navigate(Screens.DashboardScreen.route) {
                            // Remove the current screen from the back stack
                            popUpTo(Screens.RegisterScreen.route) { inclusive = true }
                        }
                    } else {
                        // Handle any registration errors (e.g., user already exists, invalid email, etc.)
                    }
                }
        } else {
            // Handle the case where the two passwords don't match
        }
    }

    // UI layout
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Register", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

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

        // Confirm Password field
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Register button
        Button(onClick = registerUser, modifier = Modifier.fillMaxWidth()) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Link to go back to the Login screen
        TextButton(onClick = {
            navController.popBackStack()
        }) {
            Text("Already have an account? Login")
        }
    }
}
