package com.sarangem.zenwell.ui.screens.home

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.sarangem.zenwell.R

@Composable
fun AskPermissions(
    modifier: Modifier = Modifier,
    hasAccessibilityServicePermission: Boolean = false,
    hasNotificationsPermission: Boolean = false,
    startPermissionActivity: (Intent) -> Unit = {},
    requestNotification: () -> Unit = {},
    isSystemInDarkTheme: Boolean = isSystemInDarkTheme()
) {
    Column {

        if (!hasAccessibilityServicePermission) {
            PermissionRequestCard(
                modifier = modifier.padding(dimensionResource(R.dimen.padding_small)),
                permissionName = stringResource(R.string.accessibility_service_permission),
                permissionExplanation = stringResource(R.string.accessibility_service_permission_explanation),
                onGrantClick = {
                    startPermissionActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }
            )
        }

        if (!hasNotificationsPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionRequestCard(
                modifier = modifier.padding(dimensionResource(R.dimen.padding_small)),
                permissionName = stringResource(R.string.notification_permission),
                permissionExplanation = stringResource(R.string.notification_permission_explanation),
                cardColor = if (isSystemInDarkTheme) Color(0xFF7C5900) else Color(0xFFF9DEBB),
                onGrantClick = {
                    requestNotification()
                }
            )
        }

    }
}

@Composable
fun PermissionRequestCard(
    modifier: Modifier = Modifier,
    onGrantClick: () -> Unit = {},
    permissionName: String = "",
    permissionExplanation: String = "",
    cardColor: Color = MaterialTheme.colorScheme.errorContainer,
    textColor: Color = MaterialTheme.colorScheme.onErrorContainer
) {
    val buttonColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)

    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(R.dimen.card_elevation)
        ),
        colors = CardDefaults.cardColors(cardColor),
        modifier = modifier
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
                colors = ButtonDefaults.buttonColors(buttonColor),
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
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