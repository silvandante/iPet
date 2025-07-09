package com.annywalker.ipet.features.diary

import com.annywalker.ipet.core.data.repository.SymptomRepository
import com.annywalker.ipet.core.domain.model.Pet
import com.annywalker.ipet.core.domain.model.SymptomDefinition
import com.annywalker.ipet.core.domain.model.SymptomEntry
import com.annywalker.ipet.features.IPetBaseViewModel
import com.annywalker.ipet.managers.PetSelectionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class PetMainViewModel @Inject constructor(
    private val repository: SymptomRepository,
    private val petSelectionManager: PetSelectionManager
) : IPetBaseViewModel<PetMainState>(PetMainState.Loading) {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    private val _selectedOptions = MutableStateFlow<Map<String?, String>>(emptyMap())

    private val _symptomDefinitions = MutableStateFlow<List<SymptomDefinition>>(emptyList())
    private val _symptomEntry = MutableStateFlow<SymptomEntry?>(null)

    private val _availablePets = MutableStateFlow<List<Pet>>(emptyList())
    val availablePets: StateFlow<List<Pet>> = _availablePets.asStateFlow()

    private val _selectedPet = MutableStateFlow<Pet?>(null)
    val selectedPet: StateFlow<Pet?> = _selectedPet.asStateFlow()

    init {
        observePetsAndSelection()
    }

    private fun observePetsAndSelection() {
        launchWithCatch(
            onError = {
                _uiState.value = PetMainState.Error(it.message ?: "Error")
            }
        ) {
            combine(
                petSelectionManager.pets,
                petSelectionManager.selectedPet
            ) { pets, selectedPet -> pets to selectedPet }
                .collect { (pets, selectedPet) ->
                    _availablePets.value = pets
                    _selectedPet.value = selectedPet

                    if (pets.isEmpty()) {
                        _uiState.value = PetMainState.Error("Adicione um novo pet na aba de Pets")
                        return@collect
                    }

                    if (selectedPet == null) {
                        pets.firstOrNull()?.id?.let { firstPetId ->
                            petSelectionManager.selectPetById(firstPetId)
                        }
                        return@collect
                    }

                    loadEntry()
                }
        }
    }

    fun selectPetId(petId: String) {
        petSelectionManager.selectPetById(petId)
    }

    private fun loadEntry() {
        launchWithCatch(
            onError = {
                _uiState.value = PetMainState.Error("Erro ao carregar dados do pet.")
            }
        ) {
            _uiState.value = PetMainState.Loading

            val defs = repository.getSymptomDefinitions()
            _symptomDefinitions.value = defs

            val petId = _selectedPet.value?.id?.takeIf { it.isNotBlank() } ?: run {
                _uiState.value = PetMainState.Error("Pet selecionado inválido.")
                return@launchWithCatch
            }

            val entry = repository.getSymptomEntryForPetAndDate(petId, _selectedDate.value)
            _symptomEntry.value = entry

            _selectedOptions.value = entry?.symptoms ?: emptyMap()
            
            updateUiState()
        }
    }

    private fun updateUiState() {
        val defs = _symptomDefinitions.value
        val selectedOpts = _selectedOptions.value

        if (defs.isEmpty()) {
            _uiState.value = PetMainState.Loading
        } else {
            _uiState.value = PetMainState.Success(
                petId = _selectedPet.value?.id,
                selectedDate = _selectedDate.value,
                symptomDefinitions = defs,
                selectedOptions = selectedOpts
            )
        }
    }

    fun changeDate(byDays: Long) {
        _selectedDate.value = _selectedDate.value.plusDays(byDays)
        loadEntry()
    }

    fun changeSpecificDate(newDate: LocalDate) {
        _selectedDate.value = newDate
        loadEntry()
    }

    fun onOptionSelected(symptomId: String, optionId: String) {
        val updatedSelections = _selectedOptions.value.toMutableMap()
        updatedSelections[symptomId] = optionId

        _selectedOptions.value = updatedSelections.toMap()
        updateUiState()
    }

    fun save() {
        launchWithCatch(
            onError = {
                _uiState.value = PetMainState.Error(it.message ?: "Error")
            }
        ) {
            val entry = SymptomEntry(
                petId = _selectedPet.value?.id,
                date = _selectedDate.value.toString(),
                symptoms = _selectedOptions.value.ifEmpty {
                    _symptomEntry.value?.symptoms ?: emptyMap()
                }
            )
            repository.saveSymptomEntry(entry)
            _symptomEntry.value = entry
            _selectedOptions.value = emptyMap()
            updateUiState()
            loadEntry()
        }
    }
}