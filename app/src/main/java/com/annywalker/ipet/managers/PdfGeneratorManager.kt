package com.annywalker.ipet.managers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.annywalker.ipet.core.domain.model.SymptomEntry
import com.annywalker.ipet.core.util.PdfConstants.KEY_ENTRIES
import com.annywalker.ipet.core.util.PdfConstants.KEY_PET_AGE
import com.annywalker.ipet.core.util.PdfConstants.KEY_PET_BIRTHDAY
import com.annywalker.ipet.core.util.PdfConstants.KEY_PET_DISEASES
import com.annywalker.ipet.core.util.PdfConstants.KEY_PET_NAME
import com.annywalker.ipet.core.util.toJson
import com.annywalker.ipet.worker.PdfGenerationWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfGeneratorManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val channelId = "report_channel"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelId,
            "Report Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Channel for report generation notifications"
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun generateInBackground(
        entries: List<SymptomEntry?>,
        petName: String,
        petAge: String,
        petBirthday: String?,
        petDiseases: String
    ) {
        val inputData = workDataOf(
            KEY_ENTRIES to entries.toJson(),
            KEY_PET_NAME to petName,
            KEY_PET_AGE to petAge,
            KEY_PET_BIRTHDAY to petBirthday,
            KEY_PET_DISEASES to petDiseases
        )

        val workRequest = OneTimeWorkRequestBuilder<PdfGenerationWorker>()
            .setInputData(inputData)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}