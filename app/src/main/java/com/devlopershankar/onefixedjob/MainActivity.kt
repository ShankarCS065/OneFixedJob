// MainActivity.kt
package com.devlopershankar.onefixedjob

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.devlopershankar.onefixedjob.navigation.Screens
import com.devlopershankar.onefixedjob.ui.screen.*
import com.devlopershankar.onefixedjob.ui.theme.OneFixedJobTheme
import com.devlopershankar.onefixedjob.ui.viewmodel.UserProfileViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OneFixedJobTheme { // Apply your custom theme
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    // Obtain the ViewModel using viewModel() which provides the same instance across composables
                    val userProfileViewModel: UserProfileViewModel = viewModel()

                    // Manage the Drawer state
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val scope = rememberCoroutineScope()

                    // Handle logout navigation based on ViewModel events
                    LaunchedEffect(key1 = userProfileViewModel) {
                        userProfileViewModel.eventFlow.collectLatest { event ->
                            when (event) {
                                is UserProfileViewModel.UiEvent.ShowToast -> {
                                    // Optionally handle global toasts here
                                    // For example, show a Snackbar or Toast
                                    // Example using Toast:
                                    // Toast.makeText(this@MainActivity, event.message, Toast.LENGTH_SHORT).show()
                                }
                                is UserProfileViewModel.UiEvent.SaveSuccess -> {
                                    // Handle save success if needed globally
                                    // For instance, show a global Snackbar
                                }
                                is UserProfileViewModel.UiEvent.ShowError -> {
                                    // Handle global error messages
                                    // Example using Toast:
                                    // Toast.makeText(this@MainActivity, event.message, Toast.LENGTH_SHORT).show()
                                }
                                is UserProfileViewModel.UiEvent.LogoutSuccess -> {
                                    // Navigate to LoginScreen upon logout
                                    navController.navigate(Screens.LoginScreen.route) {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    }
                                }
                                // Handle other events if necessary
                                else -> Unit
                            }
                        }
                    }

                    // ModalNavigationDrawer wraps the NavHost and provides drawer functionality
                    ModalNavigationDrawer(
                        drawerContent = {
                            DrawerContent(
                                navController = navController,
                                viewModel = userProfileViewModel,
                                onCloseDrawer = {
                                    scope.launch {
                                        drawerState.close()
                                    }
                                }
                            )
                        },
                        drawerState = drawerState,
                        gesturesEnabled = drawerState.isOpen
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = Screens.SplashScreen.route
                        ) {
                            // Authentication Screens
                            composable(Screens.LoginScreen.route) {
                                LoginScreen(navController)
                            }
                            composable(Screens.ForgotPasswordScreen.route) {
                                ForgotPasswordScreen(navController)
                            }
                            composable(Screens.RegisterScreen.route) {
                                RegisterScreen(navController)
                            }

                            // Onboarding and Main Screens
                            composable(Screens.SplashScreen.route) {
                                SplashScreen(navController)
                            }
                            composable(Screens.DashboardScreen.route) {
                                DashboardScreen(
                                    navController = navController,
                                    onOpenDrawer = {
                                        scope.launch {
                                            drawerState.open()
                                        }
                                    },
                                    userProfileViewModel = userProfileViewModel // Pass the ViewModel
                                )
                            }

                            // Profile Screens with shared ViewModel
                            composable(Screens.UserProfileScreen.route) {
                                UserProfileScreen(navController, userProfileViewModel)
                            }
                            composable(Screens.ProfileCreationScreen.route) {
                                ProfileCreationScreen(navController, userProfileViewModel)
                            }

                            // Other Feature Screens
                            composable(Screens.SettingsScreen.route) {
                                SettingsScreen(navController)
                            }
                            composable(Screens.InternshipScreen.route) {
                                InternshipScreen(navController)
                            }
                            composable(Screens.JobScreen.route) {
                                JobScreen(navController)
                            }
                            composable(Screens.MoreScreen.route) {
                                MoreScreen(navController)
                            }
                            composable(Screens.NotificationScreen.route) {
                                NotificationScreen(navController)
                            }
                            composable(Screens.HelpScreen.route) {
                                HelpScreen(navController)
                            }
                            composable(
                                route = Screens.JobDetailScreen.route,
                                arguments = listOf(navArgument("applyLink") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val applyLink = backStackEntry.arguments?.getString("applyLink")
                                JobDetailScreen(navController, applyLink)
                            }
                            composable(Screens.CourseScreen.route) {
                                CourseScreen(navController)
                            }
                            composable(Screens.PracticeScreen.route) {
                                PracticeScreen(navController)
                            }
                            composable(Screens.ChatCreationScreen.route) {
                                ChatCreationScreen(navController)
                            }
                            // Add other screens here
                        }
                    }
                }
            }
        }
    }
}
