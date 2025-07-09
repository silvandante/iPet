package com.annywalker.ipet.core.data.repository

import android.util.Log
import com.annywalker.ipet.core.domain.model.SymptomDefinition
import com.annywalker.ipet.core.domain.model.SymptomEntry
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SymptomRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val definitionsCollection = firestore.collection("symptom_definitions")
    private val entriesCollection = firestore.collection("symptom_entries")

    suspend fun getSymptomDefinitions(): List<SymptomDefinition> = withContext(Dispatchers.IO) {
        try {
            val snapshot = definitionsCollection.get().await()
            snapshot.documents.mapNotNull { it.toObject(SymptomDefinition::class.java) }
        } catch (e: Exception) {
            Log.e("error", "fetch failed: ${e.message}")
            emptyList()
        }
    }

    suspend fun getSymptomEntryForPetAndDate(petId: String?, date: LocalDate): SymptomEntry? {
        try {
            val id = "${petId}_${date}"
            val doc = entriesCollection.document(id).get().await()
            return doc.toObject(SymptomEntry::class.java)
        } catch (e: Exception) {
            Log.e("error", "fetch failed: ${e.message}")
            return null
        }
    }

    suspend fun getSymptomsEntryForPetAllTime(petId: String?): List<SymptomEntry> =
        withContext(Dispatchers.IO) {
            val snapshot = entriesCollection
                .whereEqualTo("petId", petId)
                .get()
                .await()
            snapshot.documents.mapNotNull { it.toObject(SymptomEntry::class.java) }
        }

    suspend fun saveSymptomEntry(entry: SymptomEntry) = withContext(Dispatchers.IO) {
        val id = "${entry.petId}_${entry.date}"
        entriesCollection.document(id).set(entry).await()
    }
}