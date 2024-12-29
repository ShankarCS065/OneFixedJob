// OpportunityViewModel.kt
package com.devlopershankar.onefixedjob.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devlopershankar.onefixedjob.ui.model.Opportunity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OpportunityViewModel : ViewModel() {

    // Firestore instance
    private val firestore = FirebaseFirestore.getInstance()
    private val opportunitiesCollection = firestore.collection("opportunities")

    // StateFlow for list of opportunities
    private val _opportunities = MutableStateFlow<List<Opportunity>>(emptyList())
    val opportunities: StateFlow<List<Opportunity>> = _opportunities.asStateFlow()

    // StateFlow for loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // StateFlow for error messages
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // SharedFlow for UI events
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()

    // Function to fetch opportunities by type in real-time
    fun getOpportunitiesByType(type: String) {
        _isLoading.value = true
        firestore.collection("opportunities")
            .whereEqualTo("type", type)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    viewModelScope.launch {
                        _eventFlow.emit(UiEvent.ShowError("Failed to load opportunities: ${error.message}"))
                    }
                    _isLoading.value = false
                    return@addSnapshotListener
                }

                val opportunitiesList = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Opportunity::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                _opportunities.value = opportunitiesList
                _isLoading.value = false
            }
    }

    // Function to add a new opportunity
    fun addOpportunity(
        opportunity: Opportunity,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                firestore.collection("opportunities").add(opportunity).await()
                _eventFlow.emit(UiEvent.AddSuccess)
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                _eventFlow.emit(UiEvent.ShowError("Failed to add opportunity: ${e.message}"))
                onFailure(e.message ?: "Unknown error")
            }
        }
    }

    // Function to update an existing opportunity
    fun updateOpportunity(
        opportunity: Opportunity,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                firestore.collection("opportunities").document(opportunity.id).set(opportunity).await()
                _eventFlow.emit(UiEvent.UpdateSuccess)
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                _eventFlow.emit(UiEvent.ShowError("Failed to update opportunity: ${e.message}"))
                onFailure(e.message ?: "Unknown error")
            }
        }
    }

    // Function to fetch a single opportunity by ID
    suspend fun getOpportunityById(opportunityId: String): Opportunity? {
        return try {
            val doc = opportunitiesCollection.document(opportunityId).get().await()
            doc.toObject(Opportunity::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Sealed class for UI events
    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
        data class ShowError(val message: String) : UiEvent()
        object AddSuccess : UiEvent()
        object UpdateSuccess : UiEvent()
        // Add more events as needed
    }
}
