package com.annywalker.ipet.core.designsystem.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.annywalker.ipet.core.designsystem.shapes.AppShapes
import com.annywalker.ipet.core.designsystem.typography.AppTypography

@Composable
fun IPetPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = AppShapes.medium,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = text,
            style = AppTypography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}