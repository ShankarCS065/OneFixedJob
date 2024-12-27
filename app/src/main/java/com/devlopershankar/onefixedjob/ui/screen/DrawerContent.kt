// DrawerContent.kt
package com.devlopershankar.onefixedjob.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.devlopershankar.onefixedjob.R
import com.devlopershankar.onefixedjob.navigation.Screens
import com.devlopershankar.onefixedjob.ui.viewmodel.UserProfileViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Data class representing each menu item in the drawer.
 */
data class DrawerMenuItemData(val iconRes: Int, val label: String, val screen: Screens)

/**
 * Composable function representing the content of the navigation drawer.
 *
 * @param navController The NavController to handle navigation actions.
 * @param viewModel The UserProfileViewModel to handle logout and other actions.
 * @param onCloseDrawer A lambda function to close the drawer.
 */
@Composable
fun DrawerContent(
    navController: NavController,
    viewModel: UserProfileViewModel,
    onCloseDrawer: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Collect events from the ViewModel
    LaunchedEffect(key1 = viewModel) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UserProfileViewModel.UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is UserProfileViewModel.UiEvent.LogoutSuccess -> {
                    // Navigation is already handled in MainActivity's LaunchedEffect
                    Toast.makeText(context, "Logged out successfully!", Toast.LENGTH_SHORT).show()
                }
                is UserProfileViewModel.UiEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                // Handle other events if necessary
                else -> Unit
            }
        }
    }

    val menuItems = listOf(
        DrawerMenuItemData(
            iconRes = R.drawable.ic_user_profile, // Ensure these icons exist in your drawable resources
            label = "Profile",
            screen = Screens.UserProfileScreen
        ),
        DrawerMenuItemData(
            iconRes = R.drawable.ic_home,
            label = "Home",
            screen = Screens.DashboardScreen
        ),
        DrawerMenuItemData(
            iconRes = R.drawable.ic_settings,
            label = "Settings",
            screen = Screens.SettingsScreen
        ),
        DrawerMenuItemData(
            iconRes = R.drawable.ic_help,
            label = "Help",
            screen = Screens.HelpScreen
        )
    )

    // Drawer content layout
    Row(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.75f) // Set width to 75% of the screen width for better usability
            .background(Color.White)
            .verticalScroll(rememberScrollState()), // Enable vertical scrolling if content overflows.
    ) {
        // Column for the menu content aligned to the left
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // Padding to avoid content touching the edges
            horizontalAlignment = Alignment.Start
        ) {
            // App Icon and Name at the top
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "App Icon",
                    modifier = Modifier
                        .size(80.dp)
                        .padding(8.dp)
                )
                Text(
                    text = "OneFixedJob",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black // Change text color to black for contrast
                )
            }

            Spacer(modifier = Modifier.height(16.dp)) // Add space between header and menu items

            // Menu items
            menuItems.forEach { item ->
                DrawerMenuItem(
                    iconRes = item.iconRes,
                    label = item.label,
                    onClick = {
                        navController.navigate(item.screen.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                            launchSingleTop = true
                        }
                        onCloseDrawer()
                    }
                )
            }

            // Add logout option at the bottom
            Spacer(modifier = Modifier.weight(1f)) // Push logout to the bottom
            Divider(color = Color.Gray, thickness = 1.dp)
            DrawerMenuItem(
                iconRes = R.drawable.ic_logout,  // Ensure you have a logout icon in your drawable resources
                label = "Logout",
                onClick = {
                    coroutineScope.launch {
                        viewModel.logout() // Call logout function from ViewModel
                        onCloseDrawer()
                    }
                }
            )
        }
    }
}

/**
 * Composable function representing a single menu item in the drawer.
 *
 * @param iconRes The drawable resource ID for the menu item's icon.
 * @param label The text label for the menu item.
 * @param onClick A lambda function to execute when the menu item is clicked.
 */
@Composable
fun DrawerMenuItem(iconRes: Int, label: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = "$label Icon",
            modifier = Modifier.size(24.dp),
            tint = Color.Black // Set icon color to black for contrast
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black, // Set text color to black for contrast
            fontWeight = FontWeight.Medium
        )
    }
}
