package com.annywalker.ipet.features.main

import androidx.credentials.GetCredentialRequest
import com.annywalker.ipet.features.IPetBaseViewModel
import com.annywalker.ipet.features.onboarding.ONBOARDING_KEY
import com.annywalker.ipet.managers.CacheManager
import com.annywalker.ipet.managers.FirebaseLoginManager
import com.annywalker.ipet.managers.PetSelectionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainSharedViewModel @Inject constructor(
    private val loginManager: FirebaseLoginManager,
    private val petSelectionManager: PetSelectionManager,
    private val cacheManager: CacheManager
) : IPetBaseViewModel<MainUiState>(MainUiState(isLoading = true)) {

    init {
        launchWithCatch {
            val isLoggedIn = loginManager.isUserLoggedIn()
            val isOnboardingCompleted = cacheManager.observeBoolean(
                ONBOARDING_KEY, false
            ).first()
            _uiState.update {
                it.copy(
                    isUserLoggedIn = isLoggedIn,
                    isOnboardingComplete = isOnboardingCompleted
                )
            }
            reloadUserState()
        }
    }

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword) }
    }

    fun login() {
        val email = _uiState.value.email
        val password = _uiState.value.password

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

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
            val result = loginManager.login(email, password)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.localizedMessage,
                    isUserLoggedIn = loginManager.isUserLoggedIn(),
                    userId = loginManager.getCurrentUser()?.email
                )
            }
            petSelectionManager.loadPets()
        }
    }

    fun createGoogleSignInRequest(): GetCredentialRequest {
        return loginManager.createGoogleSignInRequest()
    }

    fun loginWithGoogleCredential(credential: Any) {
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
            _uiState.update { it.copy(isLoading = true) }
            val success = loginManager.firebaseAuthWithCredential(credential)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isUserLoggedIn = success.isSuccess,
                    errorMessage = if (!success.isSuccess) "Google login failed" else null,
                    userId = loginManager.getCurrentUser()?.email
                )
            }
            petSelectionManager.loadPets()
        }
    }

    fun reloadUserState() {
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
            _uiState.update { it.copy(isLoading = true) }
            val isLoggedIn = loginManager.isUserLoggedIn()
            _uiState.update {
                it.copy(
                    isUserLoggedIn = isLoggedIn, isLoading = false,
                    userId = loginManager.getCurrentUser()?.email,
                    isOnboardingComplete = cacheManager.getBoolean(ONBOARDING_KEY, false)
                )
            }
            petSelectionManager.loadPets()
        }
    }

    fun onBoardingStarted() {
        launchWithCatch {
            _uiState.update {
                it.copy(
                    isOnboardingComplete = false,
                    isOnboardingStarted = true,
                    isLoading = false
                )
            }
        }
    }


    fun reloadOnboardingState() {
        launchWithCatch {
            cacheManager.setBoolean(ONBOARDING_KEY, true)
            _uiState.update {
                it.copy(
                    isOnboardingComplete = true,
                    isLoading = false
                )
            }
        }
    }

    fun logout() {
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
            _uiState.update { it.copy(isLoading = true) }
            loginManager.signOutAndClearCredentials()
            _uiState.update {
                it.copy(
                    isUserLoggedIn = false,
                    isLoading = false,
                    userId = null,
                    isOnboardingComplete = false,
                    isOnboardingStarted = false
                )
            }
        }
    }
}
