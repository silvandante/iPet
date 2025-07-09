package com.annywalker.ipet.features.alarms

import com.annywalker.ipet.core.domain.model.MedAlarm
import com.annywalker.ipet.core.domain.model.Pet


sealed interface PetAlarmUiState {
    data object Loading : PetAlarmUiState
    data class Success(val pet: Pet, val alarms: List<MedAlarm>) : PetAlarmUiState
    data class Error(val message: String) : PetAlarmUiState
}
