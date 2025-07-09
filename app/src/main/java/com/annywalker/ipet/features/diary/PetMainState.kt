package com.annywalker.ipet.features.diary

import com.annywalker.ipet.core.domain.model.SymptomDefinition
import java.time.LocalDate

sealed class PetMainState {
    data object Loading : PetMainState()
    data class Success(
        val petId: String?,
        val selectedDate: LocalDate,
        val symptomDefinitions: List<SymptomDefinition>,
        val selectedOptions: Map<String?, String>
    ) : PetMainState()
    data class Error(val message: String) : PetMainState()
}