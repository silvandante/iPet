package com.annywalker.ipet.features.signup

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isUserCreated: Boolean = false,
    val errorMessage: String? = null
)