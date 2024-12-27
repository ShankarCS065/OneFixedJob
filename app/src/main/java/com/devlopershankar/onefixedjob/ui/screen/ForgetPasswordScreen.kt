package com.devlopershankar.onefixedjob.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    // Material 3 approach: Use SnackbarHostState instead of ScaffoldState
    val snackbarHostState = remember { SnackbarHostState() }

    var email by remember { mutableStateOf(TextFieldValue("")) }
    var message by remember { mutableStateOf("") }
    var showMessage by remember { mutableStateOf(false) }

    val sendPasswordResetEmail: () -> Unit = {
        val trimmedEmail = email.text.trim()
        if (trimmedEmail.isNotEmpty()) {
            Firebase.auth.sendPasswordResetEmail(trimmedEmail)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        message = "Password reset link sent to $trimmedEmail."
                        showMessage = true
                        Log.d("ForgotPassword", "Email sent.")
                    } else {
                        message = task.exception?.localizedMessage ?: "Error sending reset email."
                        showMessage = true
                        Log.e("ForgotPassword", "Error: $message")
                    }
                }
        } else {
            message = "Please enter a valid email."
            showMessage = true
        }
    }

    // Whenever showMessage becomes true and there's a message, we trigger a snackbar
    if (showMessage && message.isNotEmpty()) {
        // Show the snackbar once, then reset the flag
        LaunchedEffect(key1 = showMessage) {
            snackbarHostState.showSnackbar(message)
            showMessage = false
        }
    }

    Scaffold(
        // Provide a SnackbarHost to display snackbars
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Forgot Password",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = sendPasswordResetEmail,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send Reset Link")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.popBackStack() }) {
                Text("Back to Login")
            }
        }
    }
}
