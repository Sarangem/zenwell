/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.home

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.sizing

@Composable
fun AccessibilityPermissionCard(
    modifier: Modifier = Modifier,
    recheckPermission: () -> Unit = {}
){
    PermissionRequestCard(
        modifier = modifier.padding(MaterialTheme.sizing.small),
        permissionName = stringResource(R.string.accessibility_service_permission),
        permissionExplanation = stringResource(R.string.accessibility_service_permission_explanation),
        onGrantClick = recheckPermission
    )
}

@Composable
fun NotificationPermissionCard(
    modifier: Modifier = Modifier,
    onGrantClick: () -> Unit = {}
){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        PermissionRequestCard(
            modifier = modifier.padding(MaterialTheme.sizing.small),
            permissionName = stringResource(R.string.notification_permission),
            permissionExplanation = stringResource(R.string.notification_permission_explanation),
            cardColor = if (isSystemInDarkTheme()) Color(0xFF7C5900) else Color(0xFFF9DEBB),
            onGrantClick = onGrantClick
        )
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
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.sizing.tiny),
        colors = CardDefaults.cardColors(cardColor),
        modifier = modifier.fillMaxWidth()
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
            modifier = Modifier.padding(MaterialTheme.sizing.small)
        )
        Row {
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onGrantClick,
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error.copy(alpha = 0.1f)),
                modifier = Modifier.padding(MaterialTheme.sizing.small)
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