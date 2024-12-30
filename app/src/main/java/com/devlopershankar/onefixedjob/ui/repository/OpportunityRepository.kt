// OpportunityRepository.kt
package com.devlopershankar.onefixedjob.ui.repository

import com.devlopershankar.onefixedjob.ui.model.Opportunity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.tasks.await

class OpportunityRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val opportunitiesCollection = firestore.collection("opportunities")

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

    /**
     * Fetches opportunities by type as a Flow.
     */
    fun getOpportunitiesByType(type: String): Flow<List<Opportunity>> {
        return opportunitiesCollection
            .whereEqualTo("type", type)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot: QuerySnapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Opportunity::class.java)?.copy(id = doc.id)
                }
            }
    }
}
