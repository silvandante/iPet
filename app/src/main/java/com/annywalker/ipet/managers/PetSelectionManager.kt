package com.annywalker.ipet.managers

import com.annywalker.ipet.core.data.repository.PetAlarmRepository
import com.annywalker.ipet.core.data.repository.PetRepository
import com.annywalker.ipet.core.domain.model.Pet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetSelectionManager @Inject constructor(
    private val repository: PetRepository,
    private val petAlarmRepository: PetAlarmRepository
) {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _pets = MutableStateFlow<List<Pet>>(emptyList())
    val pets: StateFlow<List<Pet>> = _pets.asStateFlow()

    private val _selectedPet = MutableStateFlow<Pet?>(null)
    val selectedPet: StateFlow<Pet?> = _selectedPet.asStateFlow()

    init {
        coroutineScope.launch {
            loadPets()
        }
    }

    suspend fun loadPets() {
        val loadedPets = repository.getPets()
        _pets.value = loadedPets
        _selectedPet.value = loadedPets.firstOrNull()
    }

    fun selectPetById(petId: String) {
        val found = _pets.value.find { it.id == petId }
        _selectedPet.value = found
    }

    suspend fun addPet(pet: Pet) {
        repository.addPet(pet)
        loadPets()
    }

    suspend fun deletePet(pet: Pet) {
        petAlarmRepository.removeAllAlarmsForPet(pet.id)
        repository.deletePet(pet)
        loadPets()
    }

    suspend fun addDiseaseToPet(diseasis: String, pet: Pet): Pet? {
        return repository.addDiseaseToPet(diseasis, pet.id)
    }
}