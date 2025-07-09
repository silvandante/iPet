package com.annywalker.ipet.features.reports

import com.annywalker.ipet.core.data.repository.SymptomRepository
import com.annywalker.ipet.core.domain.model.Pet
import com.annywalker.ipet.core.domain.model.SymptomEntry
import com.annywalker.ipet.features.IPetBaseViewModel
import com.annywalker.ipet.managers.FirebaseLoginManager
import com.annywalker.ipet.managers.PdfGeneratorManager
import com.annywalker.ipet.managers.PetSelectionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class PetReportViewModel @Inject constructor(
    private val repository: SymptomRepository,
    private val petSelectionManager: PetSelectionManager,
    private val pdfGeneratorManager: PdfGeneratorManager,
    private val firebaseLoginManager: FirebaseLoginManager
) : IPetBaseViewModel<PetReportUiState>(PetReportUiState.Loading) {

    private val _isEditingPet = MutableStateFlow(false)
    val isEditingPet: StateFlow<Boolean> = _isEditingPet.asStateFlow()

    private val _availablePets = MutableStateFlow<List<Pet>>(emptyList())
    val availablePets: StateFlow<List<Pet>> = _availablePets.asStateFlow()

    private val _selectedPet = MutableStateFlow<Pet?>(null)
    var selectedPet: StateFlow<Pet?> = _selectedPet.asStateFlow()

    private var allEntries: List<SymptomEntry?> = emptyList()
    private var intervalDays = 60

    init {
        loadPets()
        listenSelectedPet()
    }

    private fun loadPets() {
        launchWithCatch(
            onError = {
                _uiState.value = PetReportUiState.Error(it.message ?: "Error")
            }
        ) {
            petSelectionManager.pets.collect { pets ->
                _availablePets.value = pets
            }
        }
    }

    private fun listenSelectedPet() {
        launchWithCatch(
            onError = {
                _uiState.value = PetReportUiState.Error(it.message ?: "Error")
            }
        ) {
            petSelectionManager.selectedPet.collect { pet ->
                _selectedPet.value = pet
                loadEntries()
            }

        }
    }

    private fun loadEntries() {
        launchWithCatch(
            onError = {
                _uiState.value = PetReportUiState.Error(it.message ?: "Error")
            }
        ) {
            _uiState.value = PetReportUiState.Loading
            try {
                updateState()
            } catch (e: Exception) {
                _uiState.value = PetReportUiState.Error("Failed to load entries")
            }

        }
    }

    private fun updateState() {
        val filtered = allEntries.filter { entry ->
            entry?.let {
                it.petId == selectedPet.value?.id && LocalDate.parse(it.date) >= LocalDate.now()
                    .minusDays(intervalDays.toLong())
            } ?: false
        }
        _uiState.value = PetReportUiState.Success(
            entries = filtered,
            intervalDays = intervalDays
        )
    }

    private fun calculateAge(birthday: LocalDate?): Int? {
        birthday ?: return null
        val now = LocalDate.now()
        var age = now.year - birthday.year
        if (now.dayOfYear < birthday.dayOfYear) age--
        return age
    }

    fun generatePdfReport() {
        launchWithCatch(
            onError = {
                _uiState.value = PetReportUiState.Error(it.message ?: "Error")
            }
        ) {
            val pet = _selectedPet.value ?: return@launchWithCatch
            val entries = repository.getSymptomsEntryForPetAllTime(pet.id)
                .sortedBy { LocalDate.parse(it.date) }

            pdfGeneratorManager.generateInBackground(
                entries = entries,
                petName = pet.name,
                petAge = calculateAge(LocalDate.parse(pet.birthday)).toString(),
                petBirthday = pet.birthday ?: "N/A",
                petDiseases = pet.diseases.toString()
            )
        }
    }

    fun addPet(pet: Pet) {
        _isEditingPet.value = true
        launchWithCatch(
            onError = {
                _uiState.value = PetReportUiState.Error(it.message ?: "Error")
                _isEditingPet.value = false
            }
        ) {
            firebaseLoginManager.getCurrentUser()?.uid?.let {
                val addPet = pet.copy(userId = it)
                petSelectionManager.addPet(addPet)
            }
            loadPets()
            _isEditingPet.value = false
            _selectedPet.value = pet
        }
    }

    fun deletePet(pet: Pet) {
        _isEditingPet.value = true
        launchWithCatch(
            onError = {
                _uiState.value = PetReportUiState.Error(it.message ?: "Error")
                _isEditingPet.value = false
            }
        ) {
            petSelectionManager.deletePet(pet)
            loadPets()
            _isEditingPet.value = false
        }
    }

    fun selectPetId(petId: String) {
        petSelectionManager.selectPetById(petId)
    }

    fun addDiseaseToPet(disease: String) {
        launchWithCatch(
            onError = {
                _uiState.value = PetReportUiState.Error(it.message ?: "Error")
            }
        ) {
            val pet = _selectedPet.value ?: return@launchWithCatch
            _selectedPet.value = petSelectionManager.addDiseaseToPet(disease, pet)
        }
    }
}
