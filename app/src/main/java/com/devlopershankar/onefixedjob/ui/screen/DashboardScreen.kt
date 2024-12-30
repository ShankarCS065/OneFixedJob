// DashboardScreen.kt
package com.devlopershankar.onefixedjob.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.devlopershankar.onefixedjob.navigation.Screens
import com.devlopershankar.onefixedjob.ui.components.JobCardHorizontal
import com.devlopershankar.onefixedjob.R
import com.devlopershankar.onefixedjob.ui.viewmodel.OpportunityViewModel
import com.devlopershankar.onefixedjob.ui.viewmodel.UserProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    onOpenDrawer: () -> Unit,
    userProfileViewModel: UserProfileViewModel = viewModel(),
    opportunityViewModel: OpportunityViewModel = viewModel()
) {

    // Collecting data from ViewModels
    val userProfile by userProfileViewModel.userProfile.collectAsState()
    val isAdmin by userProfileViewModel.isAdmin.collectAsState()

    // Collect recommended opportunities
    val recommendedJobs by opportunityViewModel.recommendedJobs.collectAsState()
    val recommendedInternships by opportunityViewModel.recommendedInternships.collectAsState()
    val recommendedCourses by opportunityViewModel.recommendedCourses.collectAsState()
    val recommendedPractices by opportunityViewModel.recommendedPractices.collectAsState()

    val isLoading by opportunityViewModel.isLoading.collectAsState()
    val error by opportunityViewModel.error.collectAsState()

    val userName = userProfile?.fullName?.ifBlank { "User" } ?: "User"

    // Coroutine scope for handling UI events if needed
    val scope = rememberCoroutineScope()

    // Define bottom navigation items with labels and icons
    data class BottomNavItem(val label: String, val iconRes: Int)

    val bottomNavItems = listOf(
        BottomNavItem("Home", R.drawable.ic_home),
        BottomNavItem("Internship", R.drawable.ic_internship),
        BottomNavItem("Job", R.drawable.ic_job),
        BottomNavItem("More", R.drawable.ic_more)
    )

    // Quick actions: 4 cards in a 2 × 2 grid
    val quickActions = listOf("Job", "Internship", "Course", "Practice")

    // Scaffold provides the basic layout structure with TopAppBar and BottomNavigation
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("OneFixedJob") },
                navigationIcon = {
                    // Hamburger Icon to open the drawer using the provided lambda
                    IconButton(onClick = { onOpenDrawer() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_menu),
                            contentDescription = "Menu",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                actions = {
                    // Notifications icon -> NotificationScreen
                    IconButton(onClick = {
                        navController.navigate(Screens.NotificationScreen)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_notifications),
                            contentDescription = "Notifications",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    // Chat icon -> ChatCreationScreen
                    IconButton(onClick = {
                        navController.navigate(Screens.ChatCreationScreen)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_chat),
                            contentDescription = "Chat",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        },
        bottomBar = {
            // Bottom Navigation Bar
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = item.iconRes),
                                contentDescription = item.label,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = { Text(item.label) },
                        selected = false, // Update selection logic as needed
                        onClick = {
                            // Implement navigation based on selected item
                            when (item.label) {
                                "Home" -> {
                                    navController.navigate(Screens.DashboardScreen) {
                                        popUpTo(Screens.DashboardScreen) {
                                            inclusive = true
                                        }
                                    }
                                }

                                "Internship" -> {
                                    navController.navigate(Screens.InternshipScreen)
                                }

                                "Job" -> {
                                    navController.navigate(Screens.JobScreen)
                                }

                                "More" -> {
                                    navController.navigate(Screens.MoreScreen)
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // Main Content of the Dashboard Screen
        Box(modifier = Modifier.fillMaxSize()) {

            if (isLoading) {

                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (error != null) {
                Text(
                    text = error ?: "An unknown error occurred.",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1) Subheader: "Hi, Username" + search bar
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Hi, $userName",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            // Search bar with leading icon
                            var searchText by remember { mutableStateOf("") }
                            OutlinedTextField(
                                value = searchText,
                                onValueChange = { searchText = it },
                                label = { Text("Search Opportunities") },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_search),
                                        contentDescription = "Search Icon",
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 56.dp) // Increased height for better UX
                            )
                        }
                    }

                    // 2) A 2×2 grid of quick actions
                    item {
                        TwoByTwoGrid(
                            quickActions = quickActions,
                            navController = navController,
                            isAdmin = isAdmin,
                            viewModel = opportunityViewModel
                        )
                    }

                    // 3) Recommended Jobs
                    if (recommendedJobs.isNotEmpty()) {
                        item {
                            Text(
                                text = "Recommended Jobs(7)",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                        item {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {
                                items(recommendedJobs) { job ->
                                    JobCardHorizontal(
                                        opportunity = job,
                                        navController = navController,
                                        onEdit = {
                                            if (isAdmin) {
                                                navController.navigate(Screens.editOpportunity(job.id))
                                            }
                                        },
                                        isAdmin = isAdmin
                                    )
                                }
                            }
                        }
                    }

                    // 4) Recommended Internships
                    if (recommendedInternships.isNotEmpty()) {
                        item {
                            Text(
                                text = "Recommended Internships(7)",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                        item {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {
                                items(recommendedInternships) { internship ->
                                    JobCardHorizontal(
                                        opportunity = internship,
                                        navController = navController,
                                        onEdit = {
                                            if (isAdmin) {
                                                navController.navigate(Screens.editOpportunity(internship.id))
                                            }
                                        },
                                        isAdmin = isAdmin
                                    )
                                }
                            }
                        }
                    }

                    // 5) Recommended Courses
                    if (recommendedCourses.isNotEmpty()) {
                        item {
                            Text(
                                text = "Recommended Courses(7)",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                        item {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {
                                items(recommendedCourses) { course ->
                                    JobCardHorizontal(
                                        opportunity = course,
                                        navController = navController,
                                        onEdit = {
                                            if (isAdmin) {
                                                navController.navigate(Screens.editOpportunity(course.id))
                                            }
                                        },
                                        isAdmin = isAdmin
                                    )
                                }
                            }
                        }
                    }

                    // 6) Recommended Practices (Optional)
                    if (recommendedPractices.isNotEmpty()) {
                        item {
                            Text(
                                text = "Recommended Practices(7)",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                        item {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {
                                items(recommendedPractices) { practice ->
                                    JobCardHorizontal(
                                        opportunity = practice,
                                        navController = navController,
                                        onEdit = {
                                            if (isAdmin) {
                                                navController.navigate(Screens.editOpportunity(practice.id))
                                            }
                                        },
                                        isAdmin = isAdmin
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
