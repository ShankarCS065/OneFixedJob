// DashboardViewModel.kt
package com.devlopershankar.onefixedjob.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devlopershankar.onefixedjob.ui.model.Opportunity
import com.devlopershankar.onefixedjob.ui.repository.OpportunityRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(
    private val repository: OpportunityRepository = OpportunityRepository()
) : ViewModel() {

//    val jobs: StateFlow<List<Opportunity>> = repository.getOpportunitiesByType("Job")
//        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
//
//    val internships: StateFlow<List<Opportunity>> = repository.getOpportunitiesByType("Internship")
//        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
//
//    val courses: StateFlow<List<Opportunity>> = repository.getOpportunitiesByType("Course")
//        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
//
//    val practices: StateFlow<List<Opportunity>> = repository.getOpportunitiesByType("Practice")
//        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
