package com.annywalker.ipet.core.util

import com.annywalker.ipet.core.domain.model.SymptomEntry
import com.annywalker.ipet.models.gson.adapters.gsonWithAdapters
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Extension functions for List<SymptomEntry>
fun List<SymptomEntry?>.toJson(): String = gsonWithAdapters.toJson(this)

fun String.fromJsonSymptomEntryList(): List<SymptomEntry?> =
    gsonWithAdapters.fromJson(this, Array<SymptomEntry>::class.java).toList()

fun Long.formatTime(): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(this))
}
