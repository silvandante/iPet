package com.annywalker.ipet.core.data.datasource.local

import com.annywalker.ipet.core.data.datasource.local.dao.MedAlarmDao
import com.annywalker.ipet.core.data.datasource.local.entity.MedAlarmEntity
import com.annywalker.ipet.core.domain.model.MedAlarm
import com.annywalker.ipet.core.domain.model.Pet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetAlarmDataSource @Inject constructor(
    private val medAlarmDao: MedAlarmDao
) {
    fun getAlarmsFlowForPet(petId: String): Flow<List<MedAlarm>> {
        return medAlarmDao.getAlarmsForPet(petId)
            .map { list ->
                list.map { entity ->
                    MedAlarm(
                        name = entity.name,
                        time = entity.timeMillis,
                        pet = Pet(
                            id = entity.petId,
                            name = ""
                        ),
                        id = entity.id
                    )
                }
            }
    }

    suspend fun addAlarm(alarm: MedAlarm) {
        medAlarmDao.insertAlarm(
            MedAlarmEntity(
                name = alarm.name,
                timeMillis = alarm.time,
                petId = alarm.pet.id,
                id = alarm.id
            )
        )
    }

    suspend fun removeAlarm(alarm: MedAlarm) {
        medAlarmDao.deleteAlarmByFields(
            name = alarm.name,
            timeMillis = alarm.time,
            petId = alarm.pet.id
        )
    }

    suspend fun removeAllAlarmsForPet(petId: String) {
        medAlarmDao.deleteAllAlarmsForPet(petId)
    }
}