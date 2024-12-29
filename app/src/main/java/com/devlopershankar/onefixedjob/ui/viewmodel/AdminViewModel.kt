// AdminViewModel.kt
package com.devlopershankar.onefixedjob.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devlopershankar.onefixedjob.ui.model.Opportunity
import com.devlopershankar.onefixedjob.ui.repository.OpportunityRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class AdminViewModel(
    private val repository: OpportunityRepository = OpportunityRepository()
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow

    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
        data class ShowError(val message: String) : UiEvent()
        object AddSuccess : UiEvent()
    }

    /**
     * Adds a new opportunity and emits corresponding events.
     */
    fun addOpportunity(opportunity: Opportunity) {
        viewModelScope.launch {
            try {
                repository.addOpportunity(opportunity)
                _eventFlow.emit(UiEvent.AddSuccess)
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowError("Failed to add opportunity: ${e.message}"))
            }
        }
    }
}
