package com.annywalker.ipet.core.data.repository

import com.annywalker.ipet.core.data.datasource.local.PetAlarmDataSource
import com.annywalker.ipet.core.domain.model.MedAlarm
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetAlarmRepository @Inject constructor(
    private val petAlarmDataSource: PetAlarmDataSource
) {
    fun getAlarmsFlowForPet(petId: String): Flow<List<MedAlarm>> {
        return petAlarmDataSource.getAlarmsFlowForPet(petId)
    }

    suspend fun addAlarm(alarm: MedAlarm) {
        petAlarmDataSource.addAlarm(alarm)
    }

    suspend fun removeAlarm(alarm: MedAlarm) {
        petAlarmDataSource.removeAlarm(alarm)
    }

    suspend fun removeAllAlarmsForPet(petId: String) {
        petAlarmDataSource.removeAllAlarmsForPet(petId)
    }
}