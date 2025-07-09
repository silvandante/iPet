package com.annywalker.ipet.features.reports

import com.annywalker.ipet.core.domain.model.SymptomEntry

sealed class PetReportUiState {
    data object Loading : PetReportUiState()
    data class Success(
        val entries: List<SymptomEntry?>,
        val intervalDays: Int,
    ) : PetReportUiState()

    data class Error(val message: String) : PetReportUiState()
}