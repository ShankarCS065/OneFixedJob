// CreateEditOpportunityDialog.kt
package com.devlopershankar.onefixedjob.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.devlopershankar.onefixedjob.ui.model.Opportunity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditOpportunityDialog(
    isEdit: Boolean,
    existingOpportunity: Opportunity?,
    onDismiss: () -> Unit,
    onSave: (Opportunity) -> Unit
) {
    var companyName by remember { mutableStateOf(existingOpportunity?.companyName ?: "") }
    var roleName by remember { mutableStateOf(existingOpportunity?.roleName ?: "") }
    var applyLink by remember { mutableStateOf(existingOpportunity?.applyLink ?: "") }
    var description by remember { mutableStateOf(existingOpportunity?.description ?: "") }
    var imageUrl by remember { mutableStateOf(existingOpportunity?.imageUrl ?: "") }
    var isRecommended by remember { mutableStateOf(existingOpportunity?.isRecommended ?: false) }
    var batch by remember { mutableStateOf(existingOpportunity?.batch ?: "") } // New field
    var jobType by remember { mutableStateOf(existingOpportunity?.jobType ?: "Full-time") } // New field

    // Job Type Options
    val jobTypes = listOf("Full-time", "Part-time", "Hybrid", "Remote")
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = if (isEdit) "Edit Opportunity" else "Create Opportunity") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = companyName,
                    onValueChange = { companyName = it },
                    label = { Text("Company Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = roleName,
                    onValueChange = { roleName = it },
                    label = { Text("Role Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = applyLink,
                    onValueChange = { applyLink = it },
                    label = { Text("Apply Link (URL)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Image URL input
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Image URL") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Batch input
                OutlinedTextField(
                    value = batch,
                    onValueChange = { batch = it },
                    label = { Text("Batch") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Job Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = jobType,
                        onValueChange = { jobType = it },
                        readOnly = true,
                        label = { Text("Job Type") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expanded
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        jobTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(text = type) },
                                onClick = {
                                    jobType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Toggle for isRecommended
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Feature on Dashboard")
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = isRecommended,
                        onCheckedChange = { isRecommended = it }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                // Validate inputs if necessary
                val opportunity = Opportunity(
                    id = existingOpportunity?.id ?: "",
                    type = existingOpportunity?.type ?: "Job", // Adjust as needed
                    companyName = companyName,
                    roleName = roleName,
                    applyLink = applyLink,
                    description = description,
                    imageUrl = imageUrl,
                    isRecommended = isRecommended,
                    batch = batch, // Set batch
                    jobType = jobType, // Set jobType
                    timestamp = existingOpportunity?.timestamp ?: com.google.firebase.Timestamp.now()
                )
                onSave(opportunity)
            }) {
                Text(text = if (isEdit) "Update" else "Create")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = "Cancel")
            }
        }
    )
}
