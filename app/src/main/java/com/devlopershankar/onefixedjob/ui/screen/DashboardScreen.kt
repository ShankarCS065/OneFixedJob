// DashboardScreen.kt
package com.devlopershankar.onefixedjob.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import android.util.Log
import androidx.compose.foundation.shape.RoundedCornerShape
import com.devlopershankar.onefixedjob.ui.model.Opportunity

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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = error ?: "An unknown error occurred.", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { opportunityViewModel.fetchRecommendedOpportunities() }) {
                        Text(text = "Retry")
                    }
                }
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

                    // 2) Quick Actions Grid
                    item {
                        TwoByTwoGrid(
                            quickActions = quickActions,
                            navController = navController
                        )
                    }

                    // 3) Recommended Opportunities Sections
                    item {
                        RecommendedOpportunitiesSection(
                            title = "Recommended Jobs",
                            opportunities = recommendedJobs,
                            navController = navController,
                            isAdmin = isAdmin
                        )
                    }

                    item {
                        RecommendedOpportunitiesSection(
                            title = "Recommended Internships",
                            opportunities = recommendedInternships,
                            navController = navController,
                            isAdmin = isAdmin
                        )
                    }

                    item {
                        RecommendedOpportunitiesSection(
                            title = "Recommended Courses",
                            opportunities = recommendedCourses,
                            navController = navController,
                            isAdmin = isAdmin
                        )
                    }

                    item {
                        RecommendedOpportunitiesSection(
                            title = "Recommended Practices",
                            opportunities = recommendedPractices,
                            navController = navController,
                            isAdmin = isAdmin
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendedOpportunitiesSection(
    title: String,
    opportunities: List<Opportunity>,
    navController: NavController,
    isAdmin: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (opportunities.isNotEmpty()) {
            Text(
                text = "$title (${opportunities.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(opportunities) { opportunity ->
                    JobCardHorizontal(
                        opportunity = opportunity,
                        navController = navController,
                        onEdit = {
                            if (isAdmin) {
                                navController.navigate(Screens.editOpportunity(opportunity.id))
                            }
                        },
                        isAdmin = isAdmin
                    )
                }
            }

            if (isAdmin) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            // Extract the type from the title, e.g., "Recommended Jobs" -> "Job"
                            val type = title.removePrefix("Recommended ").removeSuffix("s")
                            navController.navigate("create_opportunity?type=$type")
                        }
                    ) {
                        Text(text = "Create ${title.removePrefix("Recommended ")}")
                    }
                }
            }
        } else {
            // Optionally, show a message if there are no recommended opportunities
            Text(
                text = "No recommended opportunities available.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
