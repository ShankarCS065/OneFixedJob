package com.devlopershankar.onefixedjob.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun TwoByTwoGrid(quickActions: List<String>, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // First Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickActions.take(2).forEach { action ->
                QuickActionCard(
                    action = action,
                    navController = navController,
                    modifier = Modifier.weight(1f) // Apply weight here
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Second Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickActions.drop(2).take(2).forEach { action ->
                QuickActionCard(
                    action = action,
                    navController = navController,
                    modifier = Modifier.weight(1f) // Apply weight here
                )
            }
        }
    }
}
