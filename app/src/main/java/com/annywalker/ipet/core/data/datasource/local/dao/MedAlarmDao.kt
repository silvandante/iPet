package com.annywalker.ipet.core.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.annywalker.ipet.core.data.datasource.local.entity.MedAlarmEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedAlarmDao {

    @Query("SELECT * FROM med_alarms WHERE petId = :petId ORDER BY timeMillis")
    fun getAlarmsForPet(petId: String): Flow<List<MedAlarmEntity>>

    @Insert
    suspend fun insertAlarm(alarm: MedAlarmEntity)

    @Delete
    suspend fun deleteAlarm(alarm: MedAlarmEntity)

    @Query("DELETE FROM med_alarms WHERE name = :name AND timeMillis = :timeMillis AND petId = :petId")
    suspend fun deleteAlarmByFields(name: String, timeMillis: Long, petId: String)

    @Query("DELETE FROM med_alarms WHERE petId = :petId")
    suspend fun deleteAllAlarmsForPet(petId: String)

}