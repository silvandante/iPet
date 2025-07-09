package com.annywalker.ipet.worker

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.annywalker.ipet.core.domain.model.SymptomEntry
import com.annywalker.ipet.core.util.PdfConstants
import com.annywalker.ipet.core.util.fromJsonSymptomEntryList
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@HiltWorker
class PdfGenerationWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val entries = inputData.getString(PdfConstants.KEY_ENTRIES)?.fromJsonSymptomEntryList()
                ?: return Result.failure()

            val petName = inputData.getString(PdfConstants.KEY_PET_NAME) ?: return Result.failure()
            val petAge = inputData.getString(PdfConstants.KEY_PET_AGE) ?: return Result.failure()
            val petBirthday = inputData.getString(PdfConstants.KEY_PET_BIRTHDAY)
            val petDiseases =
                inputData.getString(PdfConstants.KEY_PET_DISEASES) ?: return Result.failure()

            generate(entries, petName, petAge, petBirthday, petDiseases)
            Result.success()
        } catch (e: Exception) {
            e.message?.let { Log.e("Error Pdf generator", it) }
            Result.failure()
        }
    }

    private fun generate(
        entries: List<SymptomEntry?>,
        petName: String,
        petAge: String,
        petBirthday: String?,
        petDiseases: String
    ) {
        val pdfDocument = PdfDocument()
        var y = PdfConstants.MARGIN_TOP

        fun startNewPage(pageNumber: Int): PdfDocument.Page {
            val pageInfo = PdfDocument.PageInfo.Builder(
                PdfConstants.PAGE_WIDTH,
                PdfConstants.PAGE_HEIGHT,
                pageNumber
            ).create()
            val page = pdfDocument.startPage(pageInfo)
            y = PdfConstants.MARGIN_TOP
            return page
        }

        val paintTitle = PdfConstants.createTitlePaint()
        val paintHeader = PdfConstants.createHeaderPaint()
        val paintBody = PdfConstants.createBodyPaint()

        var pageNumber = 1
        var page = startNewPage(pageNumber)
        var canvas: Canvas = page.canvas

        canvas.drawText("Relatório Completo do Pet", PdfConstants.MARGIN_LEFT, y, paintTitle)
        y += PdfConstants.LINE_HEIGHT_TITLE
        canvas.drawText("Nome: $petName", PdfConstants.MARGIN_LEFT, y, paintHeader)
        y += PdfConstants.LINE_HEIGHT_HEADER
        canvas.drawText("Idade: $petAge anos", PdfConstants.MARGIN_LEFT, y, paintHeader)
        y += PdfConstants.LINE_HEIGHT_HEADER

        petBirthday?.let {
            canvas.drawText("Aniversário: $it", PdfConstants.MARGIN_LEFT, y, paintHeader)
            y += PdfConstants.LINE_HEIGHT_HEADER
        }

        canvas.drawText("Doenças: $petDiseases", PdfConstants.MARGIN_LEFT, y, paintHeader)
        y += PdfConstants.LINE_HEIGHT_TITLE

        val sortedEntries = entries.sortedBy { LocalDate.parse(it?.date) }
        val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

        for (entry in sortedEntries) {
            if (y + PdfConstants.BOTTOM_MARGIN > PdfConstants.PAGE_HEIGHT) {
                pdfDocument.finishPage(page)
                pageNumber++
                page = startNewPage(pageNumber)
                canvas = page.canvas
            }

            canvas.drawText(
                "Data: ${entry?.date?.format(dateFormatter)}",
                PdfConstants.MARGIN_LEFT,
                y,
                paintHeader
            )
            y += PdfConstants.LINE_HEIGHT_HEADER

            entry?.symptoms?.forEach { (symptom, option) ->
                canvas.drawText(
                    "- $symptom: $option",
                    PdfConstants.MARGIN_LEFT + PdfConstants.BODY_INDENT,
                    y,
                    paintBody
                )
                y += PdfConstants.LINE_HEIGHT_BODY
            }

            y += PdfConstants.LINE_HEIGHT_BODY
        }

        pdfDocument.finishPage(page)

        val fileName = "RelatorioPet_${petName}_${System.currentTimeMillis()}.pdf"
        val resolver = context.contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, PdfConstants.MIME_TYPE_PDF)
            put(MediaStore.MediaColumns.RELATIVE_PATH, PdfConstants.RELATIVE_PATH)
        }

        val uri = try {
            resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        } catch (e: Exception) {
            null
        } ?: throw IOException("Failed to create MediaStore record.")

        resolver.openOutputStream(uri)?.use {
            pdfDocument.writeTo(it)
        } ?: throw IOException("Failed to write PDF content")

        pdfDocument.close()

        val file = File("/storage/emulated/0/${PdfConstants.RELATIVE_PATH}/$fileName")
        showNotification(fileName, file.absolutePath)
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(fileName: String, filePath: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            val file = File(filePath)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            setDataAndType(uri, PdfConstants.MIME_TYPE_PDF)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, PdfConstants.CHANNEL_ID)
            .setContentTitle("Report Generated")
            .setContentText(fileName)
            .setSmallIcon(PdfConstants.NOTIFICATION_ICON)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context)
            .notify(System.currentTimeMillis().toInt(), notification)
    }
}
