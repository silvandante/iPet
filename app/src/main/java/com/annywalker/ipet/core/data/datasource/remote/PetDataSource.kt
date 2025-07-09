package com.annywalker.ipet.core.data.datasource.remote

import com.annywalker.ipet.core.domain.model.Pet
import com.annywalker.ipet.managers.FirebaseLoginManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetDataSource @Inject constructor(
    private val firebaseLoginManager: FirebaseLoginManager,
    private val firestore: FirebaseFirestore
) {
    private val petsCollection = firestore.collection("pets")

    suspend fun getPets(): List<Pet> = withContext(Dispatchers.IO) {
        val userId = firebaseLoginManager.getCurrentUser()?.uid

        val snapshot = firestore
            .collection("pets")
            .whereEqualTo("userId", userId)
            .get()
            .await()

        snapshot.documents.mapNotNull { it.toObject(Pet::class.java) }
    }

    suspend fun addPet(pet: Pet) {
        withContext(Dispatchers.IO) {
            petsCollection.document(pet.id).set(pet).await()
        }
    }

    suspend fun deletePet(pet: Pet) {
        withContext(Dispatchers.IO) {
            petsCollection.document(pet.id).delete().await()
        }
    }

    suspend fun addDiseaseToPet(newDisease: String, petId: String): Pet? {
        return withContext(Dispatchers.IO) {
            val petDoc = petsCollection.document(petId)

            petDoc.update("diseases", FieldValue.arrayUnion(newDisease)).await()

            val updatedSnapshot = petDoc.get().await()
            return@withContext updatedSnapshot.toObject(Pet::class.java)
        }
    }
}