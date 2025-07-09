package com.annywalker.ipet.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.annywalker.ipet.core.designsystem.typography.AppTypography
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun IPetWeekWeekSelector(date: LocalDate, onPrevious: () -> Unit, onNext: () -> Unit) {

    val locale = LocalConfiguration.current.locales[0]
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
    val today = LocalDate.now()
    val isNextEnabled = date.isBefore(today)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = onPrevious) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Previous Day"
            )
        }

        Text(
            text = date.format(formatter),
            style = AppTypography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        IconButton(onClick = onNext, enabled = isNextEnabled) {
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Next Day"
            )
        }
    }
}