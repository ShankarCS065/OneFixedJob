// OpportunityViewModel.kt
package com.devlopershankar.onefixedjob.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devlopershankar.onefixedjob.ui.model.Opportunity
import com.devlopershankar.onefixedjob.ui.repository.OpportunityRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class OpportunityViewModel(
    private val repository: OpportunityRepository = OpportunityRepository()
) : ViewModel() {

    private val TAG = "OpportunityViewModel"

    // StateFlows for different types of opportunities
    private val _recommendedJobs = MutableStateFlow<List<Opportunity>>(emptyList())
    val recommendedJobs: StateFlow<List<Opportunity>> = _recommendedJobs

    private val _recommendedInternships = MutableStateFlow<List<Opportunity>>(emptyList())
    val recommendedInternships: StateFlow<List<Opportunity>> = _recommendedInternships

    private val _recommendedCourses = MutableStateFlow<List<Opportunity>>(emptyList())
    val recommendedCourses: StateFlow<List<Opportunity>> = _recommendedCourses

    private val _recommendedPractices = MutableStateFlow<List<Opportunity>>(emptyList())
    val recommendedPractices: StateFlow<List<Opportunity>> = _recommendedPractices

    // General loading and error states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Event Flow for UI Events like Snackbars
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow

    init {
        Log.d(TAG, "ViewModel initialized. Fetching opportunities.")
        fetchRecommendedOpportunities()
    }

    /**
     * Fetches recommended opportunities for all types.
     */
    fun fetchRecommendedOpportunities() {
        viewModelScope.launch {
            Log.d(TAG, "Fetching recommended opportunities.")
            _isLoading.value = true
            _error.value = null
            try {
                val jobs = repository.getOpportunitiesByType("Job")
                Log.d(TAG, "Fetched ${jobs.size} jobs.")
                _recommendedJobs.value = jobs

                val internships = repository.getOpportunitiesByType("Internship")
                Log.d(TAG, "Fetched ${internships.size} internships.")
                _recommendedInternships.value = internships

                val courses = repository.getOpportunitiesByType("Course")
                Log.d(TAG, "Fetched ${courses.size} courses.")
                _recommendedCourses.value = courses

                val practices = repository.getOpportunitiesByType("Practice")
                Log.d(TAG, "Fetched ${practices.size} practices.")
                _recommendedPractices.value = practices
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching opportunities: ${e.localizedMessage}", e)
                _error.value = e.localizedMessage ?: "An unexpected error occurred."
                _eventFlow.emit(UiEvent.ShowError(e.localizedMessage ?: "An unexpected error occurred."))
            } finally {
                _isLoading.value = false
                Log.d(TAG, "Loading state set to false.")
            }
        }
    }

    /**
     * Adds or updates an opportunity, handling validation and image uploading.
     */
    fun addOrUpdateOpportunity(opportunity: Opportunity, imageUri: Uri?, isEditMode: Boolean) {
        viewModelScope.launch {
            Log.d(TAG, "addOrUpdateOpportunity called. isEditMode: $isEditMode")
            _isLoading.value = true
            _error.value = null
            try {
                // Validate inputs
                if (opportunity.companyName.isBlank() || opportunity.roleName.isBlank() || opportunity.applyLink.isBlank()) {
                    Log.d(TAG, "Validation failed: Required fields are empty.")
                    _eventFlow.emit(UiEvent.ShowError("Please fill in all required fields."))
                    return@launch
                }

                if (!isValidUrl(opportunity.applyLink)) {
                    Log.d(TAG, "Validation failed: Invalid Apply Link URL.")
                    _eventFlow.emit(UiEvent.ShowError("Please enter a valid Apply Link URL."))
                    return@launch
                }

                var finalOpportunity = opportunity

                if (imageUri != null) {
                    Log.d(TAG, "Uploading image.")
                    try {
                        val uploadedImageUrl = repository.uploadImage(imageUri)
                        if (uploadedImageUrl != null) {
                            Log.d(TAG, "Image uploaded successfully. URL: $uploadedImageUrl")
                            finalOpportunity = opportunity.copy(imageUrl = uploadedImageUrl)
                        } else {
                            Log.d(TAG, "Image upload failed: Uploaded URL is null.")
                            _eventFlow.emit(UiEvent.ShowError("Image upload failed."))
                            return@launch
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Image upload failed: ${e.localizedMessage}", e)
                        _eventFlow.emit(UiEvent.ShowError("Image upload failed: ${e.localizedMessage}"))
                        return@launch
                    }
                }

                if (isEditMode) {
                    Log.d(TAG, "Updating opportunity with ID: ${finalOpportunity.id}")
                    repository.updateOpportunity(finalOpportunity)
                    _eventFlow.emit(UiEvent.UpdateSuccess)
                    Log.d(TAG, "Opportunity updated successfully.")
                } else {
                    Log.d(TAG, "Adding new opportunity.")
                    val generatedId = repository.addOpportunity(finalOpportunity)
                    finalOpportunity = finalOpportunity.copy(id = generatedId)
                    // Optionally, refresh the opportunities
                    fetchRecommendedOpportunities()
                    _eventFlow.emit(UiEvent.AddSuccess)
                    Log.d(TAG, "Opportunity added successfully with ID: $generatedId")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Operation failed: ${e.localizedMessage}", e)
                _eventFlow.emit(UiEvent.ShowError("Operation failed: ${e.localizedMessage}"))
            } finally {
                _isLoading.value = false
                Log.d(TAG, "Loading state set to false.")
            }
        }
    }

    /**
     * Fetches a single opportunity by ID.
     */
    suspend fun getOpportunityById(jobId: String): Opportunity? {
        return try {
            repository.getOpportunityById(jobId)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching opportunity by ID: $jobId", e)
            null
        }
    }

    /**
     * UI Events for one-time actions like showing Snackbars.
     */
    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
        data class ShowError(val message: String) : UiEvent()
        object AddSuccess : UiEvent()
        object UpdateSuccess : UiEvent()
    }

    /**
     * Utility function to validate URLs.
     */
    fun isValidUrl(url: String): Boolean {
        return android.util.Patterns.WEB_URL.matcher(url).matches()
    }

    /**
     * Function to upload image, returns the URL
     */
    suspend fun uploadImage(uri: Uri): String? {
        return repository.uploadImage(uri)
    }
}
