package com.annywalker.ipet.core.domain.model

data class MedAlarm(
    val name: String,
    val time: Long,
    val pet: Pet,
    val id: String
)

