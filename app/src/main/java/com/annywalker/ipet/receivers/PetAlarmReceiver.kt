package com.annywalker.ipet.receivers

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.annywalker.ipet.R

class PetAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmName = intent.getStringExtra(EXTRA_ALARM_NAME) ?: "Alarm"
        val petName = intent.getStringExtra(EXTRA_PET_NAME) ?: "Pet"

        showNotification(context, alarmName, petName)
        scheduleNextDayAlarm(context, alarmName, petName)
    }

    private fun showNotification(context: Context, alarmName: String, petName: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "med_alarm_channel"

        val channel = NotificationChannel(
            channelId,
            "Pet Alarms",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Pet Alarm")
            .setContentText("$petName: $alarmName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(alarmName.hashCode(), notification)
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNextDayAlarm(context: Context, alarmName: String, petName: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, PetAlarmReceiver::class.java).apply {
            putExtra(EXTRA_ALARM_NAME, alarmName)
            putExtra(EXTRA_PET_NAME, petName)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = System.currentTimeMillis() + 24 * 60 * 60 * 1000  // 24 hours later

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }


    companion object {
        const val EXTRA_ALARM_NAME = "extra_alarm_name"
        const val EXTRA_PET_NAME = "extra_pet_name"
    }
}
