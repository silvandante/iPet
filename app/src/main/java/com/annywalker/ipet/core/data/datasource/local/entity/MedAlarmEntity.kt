package com.annywalker.ipet.core.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "med_alarms")
data class MedAlarmEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val name: String,
    val timeMillis: Long,
    val petId: String
)