// MainActivity.kt
package com.devlopershankar.onefixedjob

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.devlopershankar.onefixedjob.navigation.Screens
import com.devlopershankar.onefixedjob.ui.screen.*
import com.devlopershankar.onefixedjob.ui.theme.OneFixedJobTheme
import com.devlopershankar.onefixedjob.ui.viewmodel.OpportunityViewModel
import com.devlopershankar.onefixedjob.ui.viewmodel.UserProfileViewModel
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            OneFixedJobTheme { // Apply your custom theme
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    // Obtain the ViewModels using viewModel() which provides the same instance across composables
                    val userProfileViewModel: UserProfileViewModel = viewModel()
                    val opportunityViewModel: OpportunityViewModel = viewModel()

                    // Manage the Drawer state
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val scope = rememberCoroutineScope()

                    // Handle logout navigation based on ViewModel events
                    LaunchedEffect(key1 = userProfileViewModel) {
                        userProfileViewModel.eventFlow.collectLatest { event ->
                            when (event) {
                                is UserProfileViewModel.UiEvent.ShowToast -> {
                                    // Optionally handle global toasts here
                                }
                                is UserProfileViewModel.UiEvent.SaveSuccess -> {
                                    // Handle save success if needed globally
                                }
                                is UserProfileViewModel.UiEvent.ShowError -> {
                                    // Handle global error messages
                                }
                                is UserProfileViewModel.UiEvent.LogoutSuccess -> {
                                    // Navigate to LoginScreen upon logout
                                    navController.navigate(Screens.LoginScreen) {
                                        popUpTo(Screens.LoginScreen) {
                                            inclusive = true
                                        }
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
                            startDestination = Screens.SplashScreen
                        ) {
                            // Authentication Screens
                            composable(Screens.LoginScreen) {
                                LoginScreen(navController)
                            }
                            composable(Screens.ForgotPasswordScreen) {
                                ForgotPasswordScreen(navController)
                            }
                            composable(Screens.RegisterScreen) {
                                RegisterScreen(navController)
                            }

                            // Onboarding and Main Screens
                            composable(Screens.SplashScreen) {
                                SplashScreen(navController)
                            }
                            composable(Screens.DashboardScreen) {
                                DashboardScreen(
                                    navController = navController,
                                    onOpenDrawer = {
                                        scope.launch {
                                            drawerState.open()
                                        }
                                    },
                                    userProfileViewModel = userProfileViewModel
                                )
                            }

                            // Profile Screens with shared ViewModel
                            composable(Screens.UserProfileScreen) {
                                UserProfileScreen(navController, userProfileViewModel)
                            }
                            composable(Screens.ProfileCreationScreen) {
                                ProfileCreationScreen(navController, userProfileViewModel)
                            }

                            // Admin Screen
                            composable(Screens.AdminScreen) {
                                val isAdmin by userProfileViewModel.isAdmin.collectAsState()
                                if (isAdmin) {
                                    AdminScreen(navController)
                                } else {
                                    // Show access denied message or navigate away
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Access Denied",
                                            style = MaterialTheme.typography.headlineMedium
                                        )
                                    }
                                }
                            }

                            // Other Feature Screens
                            composable(Screens.SettingsScreen) {
                                SettingsScreen(navController)
                            }
                            composable(Screens.InternshipScreen) {
                                val isAdmin by userProfileViewModel.isAdmin.collectAsState()
                                InternshipScreen(
                                    navController,
                                    isAdmin = isAdmin,
                                    viewModel = opportunityViewModel
                                )
                            }
                            composable(Screens.JobScreen) {
                                val isAdmin by userProfileViewModel.isAdmin.collectAsState()
                                JobScreen(
                                    navController,
                                    isAdmin = isAdmin,
                                    viewModel = opportunityViewModel
                                )
                            }
                            composable(Screens.MoreScreen) {
                                MoreScreen(navController)
                            }
                            composable(Screens.NotificationScreen) {
                                NotificationScreen(navController)
                            }
                            composable(Screens.HelpScreen) {
                                HelpScreen(navController)
                            }

                            // Newly Added CourseScreen and PracticeScreen
                            composable(Screens.CourseScreen) {
                                CourseScreen(navController)
                            }
                            composable(Screens.PracticeScreen) {
                                PracticeScreen(navController)
                            }

                            // Detail screens
                            composable(
                                route = Screens.jobDetail("{jobId}"),
                                arguments = listOf(navArgument("jobId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val jobId = backStackEntry.arguments?.getString("jobId")
                                JobDetailScreen(navController, jobId)
                            }
                            composable(
                                route = Screens.internshipDetail("{internshipId}"),
                                arguments = listOf(navArgument("internshipId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val internshipId = backStackEntry.arguments?.getString("internshipId")
                                InternshipDetailScreen(navController, internshipId)
                            }
                            composable(
                                route = Screens.courseDetail("{courseId}"),
                                arguments = listOf(navArgument("courseId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val courseId = backStackEntry.arguments?.getString("courseId")
                                CourseDetailScreen(navController, courseId)
                            }
                            composable(
                                route = Screens.practiceDetail("{practiceId}"),
                                arguments = listOf(navArgument("practiceId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val practiceId = backStackEntry.arguments?.getString("practiceId")
                                PracticeDetailScreen(navController, practiceId)
                            }

                            // Create/Edit Opportunity Screens
                            composable(
                                route = Screens.createOpportunity("{type}"),
                                arguments = listOf(navArgument("type") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val type = backStackEntry.arguments?.getString("type") ?: "Job"
                                CreateOpportunityScreen(
                                    navController = navController,
                                    type = type,
                                    userProfileViewModel = userProfileViewModel,
                                    opportunityViewModel = opportunityViewModel
                                )
                            }
                            composable(
                                route = Screens.editOpportunity("{opportunityId}"),
                                arguments = listOf(navArgument("opportunityId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val opportunityId = backStackEntry.arguments?.getString("opportunityId")
                                EditOpportunityScreen(
                                    navController = navController,
                                    opportunityId = opportunityId,
                                    userProfileViewModel = userProfileViewModel,
                                    opportunityViewModel = opportunityViewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
