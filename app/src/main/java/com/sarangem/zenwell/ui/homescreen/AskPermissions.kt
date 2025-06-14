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
import com.sarangem.zenwell.checkAccessibilityServicePermission
import com.sarangem.zenwell.ui.theme.ZenwellTheme

@SuppressLint("InlinedApi")
@Composable
fun AskPermissions(
    startPermissionActivity: (Intent) -> Unit = {},
) {
    var hasAccessibilityServicePermission by remember {
        mutableStateOf(
            checkAccessibilityServicePermission()
        )
    }

    AskPermissionsBody(
        hasAccessibilityServicePermission = hasAccessibilityServicePermission,
        startPermissionActivity = { intent ->
            startPermissionActivity(intent)
            hasAccessibilityServicePermission = checkAccessibilityServicePermission()
        }
    )
}

@SuppressLint("InlinedApi")
@Composable
fun AskPermissionsBody(
    hasAccessibilityServicePermission: Boolean,
    startPermissionActivity: (Intent) -> Unit = {},
    isSystemInDarkTheme: Boolean = isSystemInDarkTheme()
) {
    Column {

        if (!hasAccessibilityServicePermission) {
            PermissionRequestCard(
                permissionName = stringResource(R.string.accessibility_service_permission),
                permissionExplanation = stringResource(R.string.accessibility_service_permission_explanation),
                onGrantClick = {
                    startPermissionActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
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
        AskPermissionsBody(false)
    }
}

@Preview
@Composable
fun AskPermissionsDarkModePreview() {
    ZenwellTheme(darkTheme = true) {
        AskPermissionsBody(false, {}, true)
    }
}