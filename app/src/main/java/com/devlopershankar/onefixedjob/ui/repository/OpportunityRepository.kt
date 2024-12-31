//// OpportunityRepository.kt
//package com.devlopershankar.onefixedjob.ui.repository
//
//import android.net.Uri
//import com.devlopershankar.onefixedjob.ui.model.Opportunity
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.storage.FirebaseStorage
//import kotlinx.coroutines.tasks.await
//import java.util.UUID
//
//class OpportunityRepository(
//    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
//    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
//) {
//
//    private val TAG = "OpportunityRepository"
//
//    /**
//     * Fetches opportunities by type.
//     */
//    suspend fun getOpportunitiesByType(type: String): List<Opportunity> {
//        return try {
//            val querySnapshot = firestore.collection("opportunities")
//                .whereEqualTo("type", type)
//                .get()
//                .await()
//
//            val opportunities = querySnapshot.documents.mapNotNull { doc ->
//                val opportunity = doc.toObject(Opportunity::class.java)
//                opportunity?.copy(id = doc.id) // Set id to document ID
//            }
//            opportunities
//        } catch (e: Exception) {
//            throw e
//        }
//    }
//
//    /**
//     * Adds a new opportunity and sets its ID to the Firestore document ID.
//     */
//    suspend fun addOpportunity(opportunity: Opportunity): String {
//        return try {
//            // Add the opportunity without the ID
//            val docRef = firestore.collection("opportunities")
//                .add(opportunity.copy(id = "")) // Ensure id is empty when adding
//                .await()
//            // Update the document with the generated id
//            firestore.collection("opportunities")
//                .document(docRef.id)
//                .update("id", docRef.id)
//                .await()
//            docRef.id
//        } catch (e: Exception) {
//            throw e
//        }
//    }
//
//    /**
//     * Updates an existing opportunity.
//     */
//    suspend fun updateOpportunity(opportunity: Opportunity) {
//        try {
//            firestore.collection("opportunities")
//                .document(opportunity.id)
//                .set(opportunity)
//                .await()
//        } catch (e: Exception) {
//            throw e
//        }
//    }
//
//    /**
//     * Uploads an image and returns its URL.
//     */
//    suspend fun uploadImage(uri: Uri): String? {
//        return try {
//            val filename = UUID.randomUUID().toString()
//            val ref = storage.reference.child("opportunity_images/$filename")
//            ref.putFile(uri).await()
//            ref.downloadUrl.await().toString()
//        } catch (e: Exception) {
//            throw e
//        }
//    }
//
//    /**
//     * Fetches a single opportunity by ID.
//     */
//    suspend fun getOpportunityById(jobId: String): Opportunity? {
//        return try {
//            val snapshot = firestore.collection("opportunities")
//                .document(jobId)
//                .get()
//                .await()
//
//            snapshot.toObject(Opportunity::class.java)?.copy(id = snapshot.id) // Set id to document ID
//        } catch (e: Exception) {
//            throw e
//        }
//    }
//}

// OpportunityRepository.kt
package com.devlopershankar.onefixedjob.ui.repository

import android.net.Uri
import com.devlopershankar.onefixedjob.ui.model.Opportunity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class OpportunityRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {

    private val TAG = "OpportunityRepository"

    /**
     * Fetches opportunities by type.
     */
    suspend fun getOpportunitiesByType(type: String): List<Opportunity> {
        return try {
            val querySnapshot = firestore.collection("opportunities")
                .whereEqualTo("type", type)
                .get()
                .await()

            val opportunities = querySnapshot.documents.mapNotNull { doc ->
                val opportunity = doc.toObject(Opportunity::class.java)
                opportunity?.copy(id = doc.id) // Set id to document ID
            }
            opportunities
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Adds a new opportunity and sets its ID to the Firestore document ID.
     */
    suspend fun addOpportunity(opportunity: Opportunity): String {
        return try {
            // Add the opportunity without the ID
            val docRef = firestore.collection("opportunities")
                .add(opportunity.copy(id = "")) // Ensure id is empty when adding
                .await()
            // Update the document with the generated id
            firestore.collection("opportunities")
                .document(docRef.id)
                .update("id", docRef.id)
                .await()
            docRef.id
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Updates an existing opportunity.
     */
    suspend fun updateOpportunity(opportunity: Opportunity) {
        try {
            firestore.collection("opportunities")
                .document(opportunity.id)
                .set(opportunity)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Uploads an image and returns its URL.
     */
    suspend fun uploadImage(uri: Uri): String? {
        return try {
            val filename = UUID.randomUUID().toString()
            val ref = storage.reference.child("opportunity_images/$filename")
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Fetches a single opportunity by ID.
     */
    suspend fun getOpportunityById(jobId: String): Opportunity? {
        return try {
            val snapshot = firestore.collection("opportunities")
                .document(jobId)
                .get()
                .await()

            snapshot.toObject(Opportunity::class.java)?.copy(id = snapshot.id) // Set id to document ID
        } catch (e: Exception) {
            throw e
        }
    }
}
