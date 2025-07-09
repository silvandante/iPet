package com.annywalker.ipet.features.onboarding

import androidx.datastore.preferences.core.booleanPreferencesKey
import com.annywalker.ipet.features.IPetBaseViewModel
import com.annywalker.ipet.managers.PermissionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

val ONBOARDING_KEY = booleanPreferencesKey("onboarding_completed")

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val permissionManager: PermissionManager
) : IPetBaseViewModel<OnboardingUiState>(
    OnboardingUiState.Loading
) {
    init {
        val ungrantedPermissions = permissionManager.getAllPermissions().filterNot {
            permissionManager.isPermissionGranted(it.permission)
        }
        if (ungrantedPermissions.isEmpty()) {
            _uiState.value = OnboardingUiState.Finished
        } else {
            _uiState.value = OnboardingUiState.Permissions(ungrantedPermissions)
        }
    }

    fun nextPermission(permission: String) {
        val currentState = _uiState.value
        if (currentState is OnboardingUiState.Permissions) {
            val remaining = currentState.permissions.filterNot { it.permission == permission }
            if (remaining.isEmpty()) {
                _uiState.value = OnboardingUiState.Finished
            } else {
                _uiState.value = OnboardingUiState.Permissions(remaining)
            }
        }
    }
}