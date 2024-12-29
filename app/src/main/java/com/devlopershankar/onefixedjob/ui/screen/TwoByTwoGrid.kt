// TwoByTwoGrid.kt
package com.devlopershankar.onefixedjob.ui.screen

import com.devlopershankar.onefixedjob.ui.model.Opportunity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.devlopershankar.onefixedjob.ui.components.OpportunityCard
import com.devlopershankar.onefixedjob.ui.components.QuickActionCard
import com.devlopershankar.onefixedjob.ui.viewmodel.OpportunityViewModel
import kotlinx.coroutines.launch

@Composable
fun TwoByTwoGrid(
    quickActions: List<String>,
    navController: NavController,
    isAdmin: Boolean,
    viewModel: OpportunityViewModel
) {
    // Collect the entire list of opportunities once
    val allOpportunities by viewModel.opportunities.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // First Row of Quick Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickActions.take(2).forEach { action ->
                QuickActionCard(
                    action = action,
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Second Row of Quick Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickActions.drop(2).take(2).forEach { action ->
                QuickActionCard(
                    action = action,
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display Opportunities for Each Quick Action
        quickActions.forEach { action ->
            Text(
                text = "$action Opportunities",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp)
            )

            // Filter opportunities based on action
            val filteredOpportunities = allOpportunities.filter { it.type == action }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = 400.dp), // Allow dynamic height
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (filteredOpportunities.isEmpty()) {
                    item {
                        Text(
                            text = "No $action opportunities available.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    items(filteredOpportunities) { opportunity ->
                        OpportunityCard(
                            opportunity = opportunity,
                            onClick = {
                                // Navigate to the corresponding detail screen based on opportunity type
                                when (opportunity.type) {
                                    "Job" -> navController.navigate("job_detail/${opportunity.id}")
                                    "Internship" -> navController.navigate("internship_detail/${opportunity.id}")
                                    "Course" -> navController.navigate("course_detail/${opportunity.id}")
                                    "Practice" -> navController.navigate("practice_detail/${opportunity.id}")
                                    else -> {
                                        // Handle unknown types if necessary
                                    }
                                }
                            },
                            onEdit = if (isAdmin) {
                                { navController.navigate("edit_opportunity/${opportunity.id}") }
                            } else {
                                {} // No-op lambda when not admin
                            }
                        )
                    }
                }
            }

            if (isAdmin) {
                // Use Row with Arrangement.End instead of Box and Modifier.align
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            // Navigate to Create Opportunity Screen with the current action type
                            navController.navigate("create_opportunity?type=$action")
                        }
                    ) {
                        Text("Create $action")
                    }
                }
            }
        }
    }
}
