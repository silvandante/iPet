package com.annywalker.ipet.core.data.repository

import com.annywalker.ipet.core.data.datasource.remote.PetDataSource
import com.annywalker.ipet.core.domain.model.Pet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetRepository @Inject constructor(
    private val petDataSource: PetDataSource
) {
    suspend fun getPets(): List<Pet> = petDataSource.getPets()

    suspend fun addPet(pet: Pet) = petDataSource.addPet(pet)

    suspend fun deletePet(pet: Pet) = petDataSource.deletePet(pet)

    suspend fun addDiseaseToPet(newDisease: String, petId: String): Pet? =
        petDataSource.addDiseaseToPet(newDisease, petId)
}