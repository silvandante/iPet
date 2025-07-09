package com.annywalker.ipet.managers

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessAlarm
import androidx.compose.material.icons.filled.Notifications
import androidx.core.content.ContextCompat
import com.annywalker.ipet.core.domain.model.PermissionInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionManager @Inject constructor(
    private val context: Application
) {
    private val _permissions: List<PermissionInfo> = listOf(
        PermissionInfo(
            title = "Notifications",
            description = "We need to send you important alerts and updates about your pet.",
            permission = Manifest.permission.POST_NOTIFICATIONS,
            icon = Icons.Default.Notifications
        ),
        PermissionInfo(
            title = "Alarm Scheduling",
            description = "We use alarms to remind you about important pet activities like feeding or medication.",
            permission = Manifest.permission.SCHEDULE_EXACT_ALARM,
            icon = Icons.Default.AccessAlarm
        )
    )

    fun getAllPermissions(): List<PermissionInfo> = _permissions

    fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun areAllPermissionsGranted(): Boolean {
        return _permissions.all { isPermissionGranted(it.permission) }
    }

    fun getUngrantedPermissions(): List<PermissionInfo> {
        return _permissions.filterNot { isPermissionGranted(it.permission) }
    }
}
