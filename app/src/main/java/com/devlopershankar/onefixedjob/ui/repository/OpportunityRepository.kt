// OpportunityRepository.kt
package com.devlopershankar.onefixedjob.ui.repository

import com.devlopershankar.onefixedjob.ui.model.Opportunity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class OpportunityRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val opportunitiesCollection = firestore.collection("opportunities")

    /**
     * Fetches opportunities of a specific type (Job, Internship, etc.) in real-time.
     */
    fun getOpportunitiesByType(type: String): Flow<List<Opportunity>> = callbackFlow {
        val registration: ListenerRegistration = opportunitiesCollection
            .whereEqualTo("type", type)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val opportunities = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Opportunity::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(opportunities)
            }

        awaitClose { registration.remove() }
    }

    /**
     * Adds a new opportunity to Firestore.
     */
    suspend fun addOpportunity(opportunity: Opportunity) {
        opportunitiesCollection.add(opportunity).await()
    }

    /**
     * Updates an existing opportunity in Firestore.
     */
    suspend fun updateOpportunity(opportunity: Opportunity) {
        opportunitiesCollection.document(opportunity.id).set(opportunity).await()
    }

    /**
     * Fetches a single opportunity by its ID.
     */
    suspend fun getOpportunityById(opportunityId: String): Opportunity? {
        return try {
            val doc = opportunitiesCollection.document(opportunityId).get().await()
            doc.toObject(Opportunity::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
