package com.annywalker.ipet.core.designsystem.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.annywalker.ipet.R
import com.annywalker.ipet.core.designsystem.dimensions.AppDimens
import com.annywalker.ipet.core.designsystem.typography.AppTypography

@Composable
fun IPetEmptyErrorBox(
    @StringRes message: Int? = null,
    messageText: String? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppDimens.spacingLarge)
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(AppDimens.elevationLow)
        ) {
            Text(
                text = messageText ?: message?.let { stringResource(message) }
                ?: stringResource(R.string.app_error),
                style = AppTypography.bodyLarge,
                modifier = Modifier.padding(AppDimens.spacingLarge)
            )
        }
    }
}