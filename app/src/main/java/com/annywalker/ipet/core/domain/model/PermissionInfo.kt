package com.annywalker.ipet.core.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.ui.graphics.vector.ImageVector

data class PermissionInfo(
    val title: String,
    val description: String,
    val permission: String,
    val icon: ImageVector = Icons.Default.Security
)