package com.annywalker.ipet.models.gson.adapters

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.LocalDate

val gsonWithAdapters: Gson = GsonBuilder()
    .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
    .create()