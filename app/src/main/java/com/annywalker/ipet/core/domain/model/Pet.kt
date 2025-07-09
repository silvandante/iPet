package com.annywalker.ipet.core.domain.model

data class Pet(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val birthday: String? = null,
    val diseases: List<String> = emptyList()
)