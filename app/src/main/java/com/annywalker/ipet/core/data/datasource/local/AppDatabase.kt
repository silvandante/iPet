package com.annywalker.ipet.core.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.annywalker.ipet.core.data.datasource.local.dao.MedAlarmDao
import com.annywalker.ipet.core.data.datasource.local.entity.MedAlarmEntity

@Database(entities = [MedAlarmEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medAlarmDao(): MedAlarmDao
}