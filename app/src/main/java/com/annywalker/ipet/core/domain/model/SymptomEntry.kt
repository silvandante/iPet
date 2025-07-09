package com.annywalker.ipet.core.domain.model

import android.os.Parcelable
import com.annywalker.ipet.models.gson.adapters.gsonWithAdapters
import kotlinx.parcelize.Parcelize

@Parcelize
data class SymptomEntry(
    val petId: String? = null,
    val date: String = "",
    val symptoms: Map<String?, String> = mapOf()
) : Parcelable {
    fun toJson(): String = gsonWithAdapters.toJson(this)

    companion object {
        fun fromJson(json: String): SymptomEntry = gsonWithAdapters.fromJson(json, SymptomEntry::class.java)
    }
}

