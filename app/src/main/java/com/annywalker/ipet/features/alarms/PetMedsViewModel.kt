package com.annywalker.ipet.features.alarms

import com.annywalker.ipet.core.domain.model.MedAlarm
import com.annywalker.ipet.core.domain.model.Pet
import com.annywalker.ipet.features.IPetBaseViewModel
import com.annywalker.ipet.managers.PetAlarmManager
import com.annywalker.ipet.managers.PetSelectionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class PetMedsViewModel @Inject constructor(
    private val petSelectionManager: PetSelectionManager,
    private val petAlarmManager: PetAlarmManager
) : IPetBaseViewModel<PetAlarmUiState>(PetAlarmUiState.Loading) {

    private val _availablePets = MutableStateFlow<List<Pet>>(emptyList())
    val availablePets: StateFlow<List<Pet>> = _availablePets.asStateFlow()

    private val _selectedPet = MutableStateFlow<Pet?>(null)
    val selectedPet: StateFlow<Pet?> = _selectedPet.asStateFlow()

    init {
        loadPets()
        listenSelectedPet()
    }

    fun selectPetById(petId: String) {
        petSelectionManager.selectPetById(petId)
    }

    private fun loadPets() {
        launchWithCatch(
            onError = {
                _uiState.value = PetAlarmUiState.Error(it.message ?: "Error")
            }
        ) {
            petSelectionManager.pets.collect { pets ->
                _availablePets.value = pets
            }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    private fun listenSelectedPet() {
        launchWithCatch(
            onError = {
                _uiState.value = PetAlarmUiState.Error(it.message ?: "Error")
            }
        ) {
            val petFlow = petSelectionManager.selectedPet

            val alarmsFlow = petFlow.flatMapLatest { pet ->
                if (pet == null) flowOf(emptyList())
                else petAlarmManager.getAlarmsFlowForPet(pet.id)
            }

            combine(petFlow, alarmsFlow) { pet, alarms ->
                if (pet == null) {
                    PetAlarmUiState.Error("Adicione um novo pet na aba de Pets")
                } else {
                    _selectedPet.value = pet
                    PetAlarmUiState.Success(pet = pet, alarms = alarms)
                }
            }
                .catch { e -> emit(PetAlarmUiState.Error("Failed to load pet details")) }
                .collect { state -> _uiState.value = state }
        }
    }

    fun addAlarm(alarm: MedAlarm) {
        launchWithCatch(
            onError = {
                _uiState.value = PetAlarmUiState.Error(it.message ?: "Error")
            }
        ) {
            petAlarmManager.addAlarm(alarm)
        }
    }

    fun deleteAlarm(alarm: MedAlarm) {
        launchWithCatch(
            onError = {
                _uiState.value = PetAlarmUiState.Error(it.message ?: "Error")
            }
        ) {
            petAlarmManager.removeAlarm(alarm)
        }
    }
}