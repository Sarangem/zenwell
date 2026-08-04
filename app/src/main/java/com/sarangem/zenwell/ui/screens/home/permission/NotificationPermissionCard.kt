/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.home.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.edit
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.sizing

@Composable
fun NotificationPermissionCard(
    modifier: Modifier = Modifier,
    onDeny: () -> Unit = {},
    recheckPermission: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { recheckPermission() }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        PermissionRequestCard(
            modifier = modifier.padding(MaterialTheme.sizing.small),
            name = R.string.notification_permission,
            cardColor = if (isSystemInDarkTheme()) Color(0xFF7C5900) else Color(0xFFF9DEBB),
            onGrantClick = { grantNotificationPermission(context, activity, permissionLauncher) }
        ) {
            Button(
                onClick = onDeny,
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error.copy(alpha = 0.05f)),
                modifier = Modifier
                    .padding(vertical = MaterialTheme.sizing.small)
                    .padding(start = MaterialTheme.sizing.small)
            ) {
                Text(
                    text = stringResource(R.string.dont_ask_again),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

private fun grantNotificationPermission(
    context: Context,
    activity: Activity?,
    launcher: ManagedActivityResultLauncher<String, Boolean>
) {
    val showRationale = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            (activity?.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) ?: false)
    val sharedPrefs = context.getSharedPreferences("permissions_prefs", Context.MODE_PRIVATE)
    val hasRequested = sharedPrefs.getBoolean("has_requested_notification", false)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && (showRationale || !hasRequested)) {
        sharedPrefs.edit { putBoolean("has_requested_notification", true) }
        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
        }
        context.startActivity(intent)
    }
}