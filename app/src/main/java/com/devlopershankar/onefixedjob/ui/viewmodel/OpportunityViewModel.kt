//// OpportunityViewModel.kt
//package com.devlopershankar.onefixedjob.ui.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.devlopershankar.onefixedjob.ui.model.Opportunity
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.ListenerRegistration
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.tasks.await
//
//class OpportunityViewModel : ViewModel() {
//
//    // Firestore instance
//    private val firestore = FirebaseFirestore.getInstance()
//    private val opportunitiesCollection = firestore.collection("opportunities")
//
//    // StateFlow for recommended jobs
//    private val _recommendedJobs = MutableStateFlow<List<Opportunity>>(emptyList())
//    val recommendedJobs: StateFlow<List<Opportunity>> = _recommendedJobs.asStateFlow()
//
//    // StateFlow for recommended internships
//    private val _recommendedInternships = MutableStateFlow<List<Opportunity>>(emptyList())
//    val recommendedInternships: StateFlow<List<Opportunity>> = _recommendedInternships.asStateFlow()
//
//    // StateFlow for recommended courses
//    private val _recommendedCourses = MutableStateFlow<List<Opportunity>>(emptyList())
//    val recommendedCourses: StateFlow<List<Opportunity>> = _recommendedCourses.asStateFlow()
//
//    // StateFlow for recommended practices (optional)
//    private val _recommendedPractices = MutableStateFlow<List<Opportunity>>(emptyList())
//    val recommendedPractices: StateFlow<List<Opportunity>> = _recommendedPractices.asStateFlow()
//
//    // StateFlow for loading state
//    private val _isLoading = MutableStateFlow(false)
//    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
//
//    // StateFlow for error messages
//    private val _error = MutableStateFlow<String?>(null)
//    val error: StateFlow<String?> = _error.asStateFlow()
//
//    // SharedFlow for UI events
//    private val _eventFlow = MutableSharedFlow<UiEvent>()
//    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()
//
//    // Listener registrations to manage listeners
//    private var jobListener: ListenerRegistration? = null
//    private var internshipListener: ListenerRegistration? = null
//    private var courseListener: ListenerRegistration? = null
//    private var practiceListener: ListenerRegistration? = null
//
//    init {
//        // Initialize fetching recommended opportunities
//        fetchRecommendedOpportunities()
//    }
//
//    private fun fetchRecommendedOpportunities() {
//        _isLoading.value = true
//
//        // Fetch recommended jobs
//        jobListener = opportunitiesCollection
//            .whereEqualTo("type", "Job")
//            .whereEqualTo("isRecommended", true)
//            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
//            .limit(7)
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    viewModelScope.launch {
//                        _eventFlow.emit(UiEvent.ShowError("Failed to load recommended jobs: ${error.message}"))
//                    }
//                    return@addSnapshotListener
//                }
//
//                val jobsList = snapshot?.documents?.mapNotNull { doc ->
//                    doc.toObject(Opportunity::class.java)?.copy(id = doc.id)
//                } ?: emptyList()
//                _recommendedJobs.value = jobsList
//                _isLoading.value = false
//            }
//
//        // Fetch recommended internships
//        internshipListener = opportunitiesCollection
//            .whereEqualTo("type", "Internship")
//            .whereEqualTo("isRecommended", true)
//            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
//            .limit(7)
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    viewModelScope.launch {
//                        _eventFlow.emit(UiEvent.ShowError("Failed to load recommended internships: ${error.message}"))
//                    }
//                    return@addSnapshotListener
//                }
//
//                val internshipsList = snapshot?.documents?.mapNotNull { doc ->
//                    doc.toObject(Opportunity::class.java)?.copy(id = doc.id)
//                } ?: emptyList()
//                _recommendedInternships.value = internshipsList
//                _isLoading.value = false
//            }
//
//        // Fetch recommended courses
//        courseListener = opportunitiesCollection
//            .whereEqualTo("type", "Course")
//            .whereEqualTo("isRecommended", true)
//            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
//            .limit(7)
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    viewModelScope.launch {
//                        _eventFlow.emit(UiEvent.ShowError("Failed to load recommended courses: ${error.message}"))
//                    }
//                    return@addSnapshotListener
//                }
//
//                val coursesList = snapshot?.documents?.mapNotNull { doc ->
//                    doc.toObject(Opportunity::class.java)?.copy(id = doc.id)
//                } ?: emptyList()
//                _recommendedCourses.value = coursesList
//                _isLoading.value = false
//            }
//
//        // Optionally, fetch recommended practices
//        /*
//        practiceListener = opportunitiesCollection
//            .whereEqualTo("type", "Practice")
//            .whereEqualTo("isRecommended", true)
//            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
//            .limit(7)
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    viewModelScope.launch {
//                        _eventFlow.emit(UiEvent.ShowError("Failed to load recommended practices: ${error.message}"))
//                    }
//                    return@addSnapshotListener
//                }
//
//                val practicesList = snapshot?.documents?.mapNotNull { doc ->
//                    doc.toObject(Opportunity::class.java)?.copy(id = doc.id)
//                } ?: emptyList()
//                _recommendedPractices.value = practicesList
//                _isLoading.value = false
//            }
//        */
//    }
//
//    // Clean up listeners when ViewModel is cleared
//    override fun onCleared() {
//        super.onCleared()
//        jobListener?.remove()
//        internshipListener?.remove()
//        courseListener?.remove()
//        practiceListener?.remove()
//    }
//
//    // Function to add a new opportunity
//    fun addOpportunity(
//        opportunity: Opportunity,
//        onSuccess: () -> Unit,
//        onFailure: (String) -> Unit
//    ) {
//        viewModelScope.launch {
//            try {
//                opportunitiesCollection.add(opportunity).await()
//                _eventFlow.emit(UiEvent.AddSuccess)
//                onSuccess()
//            } catch (e: Exception) {
//                e.printStackTrace()
//                _eventFlow.emit(UiEvent.ShowError("Failed to add opportunity: ${e.message}"))
//                onFailure(e.message ?: "Unknown error")
//            }
//        }
//    }
//
//    // Function to update an existing opportunity
//    fun updateOpportunity(
//        opportunity: Opportunity,
//        onSuccess: () -> Unit,
//        onFailure: (String) -> Unit
//    ) {
//        viewModelScope.launch {
//            try {
//                opportunitiesCollection.document(opportunity.id).set(opportunity).await()
//                _eventFlow.emit(UiEvent.UpdateSuccess)
//                onSuccess()
//            } catch (e: Exception) {
//                e.printStackTrace()
//                _eventFlow.emit(UiEvent.ShowError("Failed to update opportunity: ${e.message}"))
//                onFailure(e.message ?: "Unknown error")
//            }
//        }
//    }
//
//    // Function to fetch a single opportunity by ID
//    suspend fun getOpportunityById(opportunityId: String): Opportunity? {
//        return try {
//            val doc = opportunitiesCollection.document(opportunityId).get().await()
//            doc.toObject(Opportunity::class.java)?.copy(id = doc.id)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }
//
//    // Sealed class for UI events
//    sealed class UiEvent {
//        data class ShowToast(val message: String) : UiEvent()
//        data class ShowError(val message: String) : UiEvent()
//        object AddSuccess : UiEvent()
//        object UpdateSuccess : UiEvent()
//        // Add more events as needed
//    }
//}


// OpportunityViewModel.kt
package com.devlopershankar.onefixedjob.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devlopershankar.onefixedjob.ui.model.Opportunity
import com.devlopershankar.onefixedjob.ui.repository.OpportunityRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OpportunityViewModel(
    private val repository: OpportunityRepository = OpportunityRepository()
) : ViewModel() {

    // StateFlows for all opportunities
    private val _allJobs = MutableStateFlow<List<Opportunity>>(emptyList())
    val allJobs: StateFlow<List<Opportunity>> = _allJobs.asStateFlow()

    private val _allInternships = MutableStateFlow<List<Opportunity>>(emptyList())
    val allInternships: StateFlow<List<Opportunity>> = _allInternships.asStateFlow()

    private val _allCourses = MutableStateFlow<List<Opportunity>>(emptyList())
    val allCourses: StateFlow<List<Opportunity>> = _allCourses.asStateFlow()

    private val _allPractices = MutableStateFlow<List<Opportunity>>(emptyList())
    val allPractices: StateFlow<List<Opportunity>> = _allPractices.asStateFlow()

    // StateFlows for recommended opportunities
    private val _recommendedJobs = MutableStateFlow<List<Opportunity>>(emptyList())
    val recommendedJobs: StateFlow<List<Opportunity>> = _recommendedJobs.asStateFlow()

    private val _recommendedInternships = MutableStateFlow<List<Opportunity>>(emptyList())
    val recommendedInternships: StateFlow<List<Opportunity>> = _recommendedInternships.asStateFlow()

    private val _recommendedCourses = MutableStateFlow<List<Opportunity>>(emptyList())
    val recommendedCourses: StateFlow<List<Opportunity>> = _recommendedCourses.asStateFlow()

    private val _recommendedPractices = MutableStateFlow<List<Opportunity>>(emptyList())
    val recommendedPractices: StateFlow<List<Opportunity>> = _recommendedPractices.asStateFlow()

    // StateFlow for loading and error states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // SharedFlow for UI events
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()

    // Listener registrations
    private var jobListener: ListenerRegistration? = null
    private var internshipListener: ListenerRegistration? = null
    private var courseListener: ListenerRegistration? = null
    private var practiceListener: ListenerRegistration? = null

    // Flags to track completion
    private var isJobsLoaded = false
    private var isInternshipsLoaded = false
    private var isCoursesLoaded = false
    private var isPracticesLoaded = false

    init {
        fetchAllOpportunities()
        fetchRecommendedOpportunities()
    }

    private fun fetchAllOpportunities() {
        // Fetch all Jobs
        jobListener = FirebaseFirestore.getInstance().collection("opportunities")
            .whereEqualTo("type", "Job")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    viewModelScope.launch {
                        _eventFlow.emit(UiEvent.ShowError("Failed to load jobs: ${error.message}"))
                    }
                    return@addSnapshotListener
                }

                val jobsList = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Opportunity::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                _allJobs.value = jobsList
            }

        // Fetch all Internships
        internshipListener = FirebaseFirestore.getInstance().collection("opportunities")
            .whereEqualTo("type", "Internship")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    viewModelScope.launch {
                        _eventFlow.emit(UiEvent.ShowError("Failed to load internships: ${error.message}"))
                    }
                    return@addSnapshotListener
                }

                val internshipsList = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Opportunity::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                _allInternships.value = internshipsList
            }

        // Fetch all Courses
        courseListener = FirebaseFirestore.getInstance().collection("opportunities")
            .whereEqualTo("type", "Course")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    viewModelScope.launch {
                        _eventFlow.emit(UiEvent.ShowError("Failed to load courses: ${error.message}"))
                    }
                    return@addSnapshotListener
                }

                val coursesList = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Opportunity::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                _allCourses.value = coursesList
            }

        // Fetch all Practices
        practiceListener = FirebaseFirestore.getInstance().collection("opportunities")
            .whereEqualTo("type", "Practice")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    viewModelScope.launch {
                        _eventFlow.emit(UiEvent.ShowError("Failed to load practices: ${error.message}"))
                    }
                    return@addSnapshotListener
                }

                val practicesList = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Opportunity::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                _allPractices.value = practicesList
            }
    }

    private fun fetchRecommendedOpportunities() {
        _isLoading.value = true

        // Fetch recommended jobs
        jobListener = FirebaseFirestore.getInstance().collection("opportunities")
            .whereEqualTo("type", "Job")
            .whereEqualTo("isRecommended", true)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(7)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    viewModelScope.launch {
                        _eventFlow.emit(UiEvent.ShowError("Failed to load recommended jobs: ${error.message}"))
                    }
                    return@addSnapshotListener
                }

                val jobsList = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Opportunity::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                _recommendedJobs.value = jobsList

                isJobsLoaded = true
                checkLoadingComplete()
            }

        // Fetch recommended internships
        internshipListener = FirebaseFirestore.getInstance().collection("opportunities")
            .whereEqualTo("type", "Internship")
            .whereEqualTo("isRecommended", true)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(7)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    viewModelScope.launch {
                        _eventFlow.emit(UiEvent.ShowError("Failed to load recommended internships: ${error.message}"))
                    }
                    return@addSnapshotListener
                }

                val internshipsList = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Opportunity::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                _recommendedInternships.value = internshipsList

                isInternshipsLoaded = true
                checkLoadingComplete()
            }

        // Fetch recommended courses
        courseListener = FirebaseFirestore.getInstance().collection("opportunities")
            .whereEqualTo("type", "Course")
            .whereEqualTo("isRecommended", true)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(7)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    viewModelScope.launch {
                        _eventFlow.emit(UiEvent.ShowError("Failed to load recommended courses: ${error.message}"))
                    }
                    return@addSnapshotListener
                }

                val coursesList = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Opportunity::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                _recommendedCourses.value = coursesList

                isCoursesLoaded = true
                checkLoadingComplete()
            }

        // Fetch recommended practices (if needed)
        practiceListener = FirebaseFirestore.getInstance().collection("opportunities")
            .whereEqualTo("type", "Practice")
            .whereEqualTo("isRecommended", true)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(7)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    viewModelScope.launch {
                        _eventFlow.emit(UiEvent.ShowError("Failed to load recommended practices: ${error.message}"))
                    }
                    return@addSnapshotListener
                }

                val practicesList = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Opportunity::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                _recommendedPractices.value = practicesList

                isPracticesLoaded = true
                checkLoadingComplete()
            }
    }

    private fun checkLoadingComplete() {
        // Set isLoading to false once all recommended data is loaded
        if (isJobsLoaded ||
            isInternshipsLoaded ||
            isCoursesLoaded ||
            isPracticesLoaded
        ) {
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
                repository.addOpportunity(opportunity)
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
                repository.updateOpportunity(opportunity)
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
        return repository.getOpportunityById(opportunityId)
    }

    // Sealed class for UI events
    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
        data class ShowError(val message: String) : UiEvent()
        object AddSuccess : UiEvent()
        object UpdateSuccess : UiEvent()
        // Add more events as needed
    }

    override fun onCleared() {
        super.onCleared()
        jobListener?.remove()
        internshipListener?.remove()
        courseListener?.remove()
        practiceListener?.remove()
    }
}
