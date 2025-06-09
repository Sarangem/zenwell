package com.sarangem.zenwell.ui.homescreen

import android.annotation.SuppressLint
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.sarangem.zenwell.R
import com.sarangem.zenwell.checkPackageUsageStatsPermission
import com.sarangem.zenwell.checkSystemAlertWindowPermission
import com.sarangem.zenwell.isIgnoringBatteryOptimisations
import com.sarangem.zenwell.ui.theme.ZenwellTheme

@SuppressLint("InlinedApi")
@Composable
fun AskPermissions(
    startPermissionActivity: (Intent) -> Unit = {},
) {
    val context = LocalContext.current
    var hasSystemAlertWindowPermission by remember {
        mutableStateOf(
            checkSystemAlertWindowPermission(context)
        )
    }
    var hasPackageUsageStatsPermission by remember {
        mutableStateOf(
            checkPackageUsageStatsPermission(context)
        )
    }
    var isIgnoringBatteryOptimisations by remember {
        mutableStateOf(
            isIgnoringBatteryOptimisations(context)
        )
    }

    AskPermissionsBody(
        hasSystemAlertWindowPermission = hasSystemAlertWindowPermission,
        hasPackageUsageStatsPermission = hasPackageUsageStatsPermission,
        isIgnoringBatteryOptimisations = isIgnoringBatteryOptimisations,
        startPermissionActivity = { intent ->
            startPermissionActivity(intent)
            hasSystemAlertWindowPermission = checkSystemAlertWindowPermission(context)
            hasPackageUsageStatsPermission = checkPackageUsageStatsPermission(context)
            isIgnoringBatteryOptimisations = isIgnoringBatteryOptimisations(context)
        }
    )
}

@SuppressLint("InlinedApi")
@Composable
fun AskPermissionsBody(
    hasSystemAlertWindowPermission: Boolean,
    hasPackageUsageStatsPermission: Boolean,
    isIgnoringBatteryOptimisations: Boolean,
    startPermissionActivity: (Intent) -> Unit = {},
    isSystemInDarkTheme: Boolean = isSystemInDarkTheme()
) {
    Column {

        if (!hasSystemAlertWindowPermission) {
            PermissionRequestCard(
                permissionName = stringResource(R.string.display_over_other_apps),
                permissionExplanation = stringResource(R.string.display_over_other_apps_explanation),
                onGrantClick = {
                    startPermissionActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
                }
            )
        }

        if (!hasPackageUsageStatsPermission) {
            PermissionRequestCard(
                permissionName = stringResource(R.string.app_usage_access),
                permissionExplanation = stringResource(R.string.app_usage_access_explanation),
                onGrantClick = {
                    startPermissionActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                }
            )
        }

        if (!isIgnoringBatteryOptimisations) {
            PermissionRequestCard(
                permissionExplanation = stringResource(R.string.battery_optimization_explanation),
                cardColor = if (isSystemInDarkTheme) Color(0xFF7C5900) else Color(0xFFF9DEBB),
                onGrantClick = {
                    startPermissionActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
                }
            )
        }

    }
}

@Composable
fun PermissionRequestCard(
    onGrantClick: () -> Unit = {},
    permissionName: String = "",
    permissionExplanation: String = "",
    cardColor: Color = MaterialTheme.colorScheme.errorContainer,
    textColor: Color = MaterialTheme.colorScheme.onErrorContainer
) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(R.dimen.card_elevation)
        ),
        colors = CardDefaults.cardColors(cardColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_small))
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(permissionName)
                }
                append(permissionExplanation)
            },
            style = MaterialTheme.typography.titleMedium,
            color = textColor,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
        )
        Row {
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onGrantClick,
                colors = ButtonDefaults.buttonColors(Color.Transparent),
            ) {
                Text(
                    text = stringResource(R.string.grant_permission),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AskPermissionsLightModePreview() {
    ZenwellTheme(darkTheme = false) {
        AskPermissionsBody(false, false, false)
    }
}

@Preview
@Composable
fun AskPermissionsDarkModePreview() {
    ZenwellTheme(darkTheme = true) {
        AskPermissionsBody(false, false, false, {}, true)
    }
}