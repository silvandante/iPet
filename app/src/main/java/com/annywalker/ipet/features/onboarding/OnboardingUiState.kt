package com.annywalker.ipet.features.onboarding

import com.annywalker.ipet.core.domain.model.PermissionInfo

sealed class OnboardingUiState {
    object Loading : OnboardingUiState()
    data class Permissions(val permissions: List<PermissionInfo>, val currentIndex: Int = 0) :
        OnboardingUiState()

    object Finished : OnboardingUiState()
}
