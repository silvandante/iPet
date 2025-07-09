package com.annywalker.ipet.core.domain.model

data class SymptomDefinition(
    val id: String = "",
    val label: String = "",
    val options: List<SymptomOptionDefinition> = emptyList()
)

data class SymptomOptionDefinition(
    val id: String = "",
    val label: String = ""
)