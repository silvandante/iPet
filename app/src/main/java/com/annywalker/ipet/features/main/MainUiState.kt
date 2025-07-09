package com.annywalker.ipet.features.main

data class MainUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isUserLoggedIn: Boolean = false,
    val isOnboardingComplete: Boolean = false,
    val isOnboardingStarted: Boolean = false,
    val email: String = "",
    val password: String = "",
    val userId: String? = ""
)