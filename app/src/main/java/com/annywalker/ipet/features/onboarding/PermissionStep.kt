package com.annywalker.ipet.features.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.annywalker.ipet.core.designsystem.dimensions.AppDimens
import com.annywalker.ipet.core.designsystem.shapes.AppShapes
import com.annywalker.ipet.core.designsystem.typography.AppTypography
import com.annywalker.ipet.core.domain.model.PermissionInfo


@Composable
fun PermissionStep(
    permissionInfo: PermissionInfo,
    onRequest: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(AppDimens.spacingLarge)
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = AppShapes.small,
        elevation = CardDefaults.cardElevation(defaultElevation = AppDimens.spacingMedium)
    ) {
        Column(
            modifier = Modifier
                .padding(AppDimens.spacingLarge)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = permissionInfo.icon,
                contentDescription = null,
                modifier = Modifier
                    .size(AppDimens.spacingXLarge)
                    .padding(bottom = AppDimens.spacingSmall),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = permissionInfo.title,
                style = AppTypography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = permissionInfo.description,
                style = AppTypography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onRequest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = AppShapes.large
            ) {
                Text("Allow Access")
            }
        }
    }
}

