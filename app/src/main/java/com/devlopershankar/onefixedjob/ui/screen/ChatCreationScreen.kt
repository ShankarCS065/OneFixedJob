package com.devlopershankar.onefixedjob.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatCreationScreen(navController: NavController) {
    // State to hold the chat name input
    var chatName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            ChatCreationTopAppBar(navController = navController)
        },
        containerColor = Color.White // Set the background color to black
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Chat Name Input Field
            OutlinedTextField(
                value = chatName,
                onValueChange = { chatName = it },
                label = {
                    Text(
                        text = "Chat Name",
                        color = Color.Black
                    )
                },
                placeholder = {
                    Text(
                        text = "Enter chat name",
                        color = Color.Black.copy(alpha = 0.7f)
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = Color.Black, // Text when focused
                    unfocusedTextColor = Color.Black.copy(alpha = 0.7f), // Text when not focused
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black.copy(alpha = 0.5f),
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black.copy(alpha = 0.7f),
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (chatName.isNotBlank()) {
                            navController.navigateUp()
                        }
                    }
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Create Chat Button
            Button(
                onClick = {
                    if (chatName.isNotBlank()) {
                        navController.navigateUp()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Create Chat",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatCreationTopAppBar(navController: NavController) {
    SmallTopAppBar(
        title = { Text(text = "Create Chat", color = Color.Black) },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black
        )
    )
}

@Composable
@Preview(showBackground = true)
fun ChatCreationScreenPreview() {
    val navController = rememberNavController()
    ChatCreationScreen(navController = navController)
}
