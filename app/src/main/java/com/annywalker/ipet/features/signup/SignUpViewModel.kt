package com.annywalker.ipet.features.signup

import com.annywalker.ipet.features.IPetBaseViewModel
import com.annywalker.ipet.managers.FirebaseLoginManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val firebaseLoginManager: FirebaseLoginManager
) : IPetBaseViewModel<SignUpUiState>(SignUpUiState()) {

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun signUp() {
        launchWithCatch(
            onError = { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error"
                    )
                }
            }
        ) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = firebaseLoginManager.signUp(
                _uiState.value.email,
                _uiState.value.password
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isUserCreated = result.isSuccess,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }
}
