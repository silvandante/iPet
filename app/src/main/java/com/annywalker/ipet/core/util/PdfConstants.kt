package com.annywalker.ipet.core.util

import android.graphics.Paint

object PdfConstants {
    // PDF Layout
    const val PAGE_WIDTH = 595
    const val PAGE_HEIGHT = 842
    const val MARGIN_LEFT = 40f
    const val MARGIN_TOP = 40f
    const val LINE_HEIGHT_TITLE = 40f
    const val LINE_HEIGHT_HEADER = 25f
    const val LINE_HEIGHT_BODY = 18f
    const val BODY_INDENT = 20f
    const val BOTTOM_MARGIN = 40f

    // MediaStore / File
    const val MIME_TYPE_PDF = "application/pdf"
    const val RELATIVE_PATH = "Download"

    // Notification
    const val CHANNEL_ID = "report_channel"
    const val NOTIFICATION_ICON = android.R.drawable.ic_menu_save

    // InputData keys
    const val KEY_ENTRIES = "entries"
    const val KEY_PET_NAME = "petName"
    const val KEY_PET_AGE = "petAge"
    const val KEY_PET_BIRTHDAY = "petBirthday"
    const val KEY_PET_DISEASES = "petDiseases"

    // Paint factory
    fun createTitlePaint() = Paint().apply {
        textSize = 24f
        isFakeBoldText = true
    }

    fun createHeaderPaint() = Paint().apply {
        textSize = 16f
        isFakeBoldText = true
    }

    fun createBodyPaint() = Paint().apply {
        textSize = 14f
    }
}
