package com.annywalker.ipet.core.domain.model

import java.time.LocalDate

data class DailyEntry(
    val date: LocalDate,
    val summary: String
)