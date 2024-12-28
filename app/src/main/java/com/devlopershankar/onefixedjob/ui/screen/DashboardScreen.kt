// DashboardScreen.kt
package com.devlopershankar.onefixedjob.ui.screen

import androidx.compose.foundation.clickable
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
import com.devlopershankar.onefixedjob.R
import com.devlopershankar.onefixedjob.navigation.Screens
import com.devlopershankar.onefixedjob.ui.viewmodel.UserProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    onOpenDrawer: () -> Unit, // Accept the onOpenDrawer lambda
    userProfileViewModel: UserProfileViewModel = viewModel()
) {
    val userProfile by userProfileViewModel.userProfile.collectAsState()
    val userName = userProfile?.fullName?.ifBlank { "User" } ?: "User"

    // Coroutine scope for handling UI events if needed
    val scope = rememberCoroutineScope()
    // State to track the selected item in bottom navigation
    val selectedIndex = remember { mutableStateOf(0) }

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

    // 7 jobs for the horizontal row
    val horizontalJobs = List(7) { index ->
        JobInfo(
            companyName = "Company $index",
            roleName = "Role $index",
            applyLink = if (index == 0) {
                "https://cdn.photographylife.com/wp-content/uploads/2014/09/Nikon-D750-Image-Samples-2.jpg"
            } else {
                "https://example.com/apply/$index"
            }
        )
    }


    // 14 jobs for the vertical list
    val verticalJobs = List(14) { index ->
        JobInfo(
            companyName = "BigCompany $index",
            roleName = "Position $index",
            applyLink = "https://bigcompany.com/apply/$index"
        )
    }

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
                        navController.navigate(Screens.NotificationScreen.route)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_notifications),
                            contentDescription = "Notifications",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    // Chat icon -> ChatCreationScreen
                    IconButton(onClick = {
                        navController.navigate(Screens.ChatCreationScreen.route)
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
                bottomNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = item.iconRes),
                                contentDescription = item.label,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = { Text(item.label) },
                        selected = (selectedIndex.value == index),
                        onClick = {
                            selectedIndex.value = index
                            // Implement navigation based on selected item
                            when (item.label) {
                                "Home" -> {
                                    navController.navigate(Screens.DashboardScreen.route) {
                                        popUpTo(Screens.DashboardScreen.route) {
                                            inclusive = true
                                        }
                                    }
                                }

                                "Internship" -> {
                                    navController.navigate(Screens.InternshipScreen.route)
                                }

                                "Job" -> {
                                    navController.navigate(Screens.JobScreen.route)
                                }

                                "More" -> {
                                    navController.navigate(Screens.MoreScreen.route)
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // Main Content of the Dashboard Screen
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
                    navController = navController
                )
            }

            // 3) Horizontal row of 7 recommended jobs
            item {
                Text(
                    text = "Recommended Jobs (7)",
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
                    items(horizontalJobs) { job ->
                        JobCardHorizontal(job = job, navController = navController)
                    }
                }
            }

            // 4) Vertical list of 14 more jobs
            item {
                Text(
                    text = "More Jobs (14)",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            items(verticalJobs) { job ->
                JobCardVertical(job = job, navController = navController)
            }
        }
    }
}
