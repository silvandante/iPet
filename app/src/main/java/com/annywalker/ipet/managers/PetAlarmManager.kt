package com.annywalker.ipet.managers

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.annywalker.ipet.core.data.repository.PetAlarmRepository
import com.annywalker.ipet.core.domain.model.MedAlarm
import com.annywalker.ipet.receivers.PetAlarmReceiver
import com.annywalker.ipet.receivers.PetAlarmReceiver.Companion.EXTRA_ALARM_NAME
import com.annywalker.ipet.receivers.PetAlarmReceiver.Companion.EXTRA_PET_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetAlarmManager @Inject constructor(
    private val repository: PetAlarmRepository,
    @ApplicationContext private val context: Context,
) {
    private val alarmsMap = mutableMapOf<String, MutableStateFlow<List<MedAlarm>>>()
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val scope = CoroutineScope(Dispatchers.IO)

    fun getAlarmsFlowForPet(petId: String): StateFlow<List<MedAlarm>> {
        return alarmsMap.getOrPut(petId) {
            val stateFlow = MutableStateFlow<List<MedAlarm>>(emptyList())
            scope.launch {
                repository.getAlarmsFlowForPet(petId).collect { list ->
                    stateFlow.value = list
                }
            }
            stateFlow
        }
    }

    private fun Context.openExactAlarmPermissionSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:$packageName")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }
    }

    suspend fun addAlarm(alarm: MedAlarm) {
        repository.addAlarm(alarm)
        scheduleAlarm(alarm)
    }

    suspend fun removeAlarm(alarm: MedAlarm) {
        repository.removeAlarm(alarm)
        cancelScheduledAlarm(alarm)
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleAlarm(alarm: MedAlarm) {
        val am = context.getSystemService(AlarmManager::class.java)
        if (!am.canScheduleExactAlarms()) {
            Log.w(
                "AlarmRepo",
                "Exact alarm permission not granted. Prompting user to enable it."
            )
            context.openExactAlarmPermissionSettings()
            return
        }

        val intent = Intent(context, PetAlarmReceiver::class.java).apply {
            putExtra(EXTRA_ALARM_NAME, alarm.name)
            putExtra(EXTRA_PET_NAME, alarm.pet.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarm.time,
            pendingIntent,
        )
    }

    private fun cancelScheduledAlarm(alarm: MedAlarm) {
        val intent = Intent(context, PetAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
