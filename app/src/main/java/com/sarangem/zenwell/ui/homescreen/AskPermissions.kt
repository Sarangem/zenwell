package com.sarangem.zenwell.ui.homescreen

import android.annotation.SuppressLint
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import com.sarangem.zenwell.ui.theme.Purple80

@SuppressLint("InlinedApi")
@Composable
fun AskPermissions(
    startPermissionActivity: (Intent) -> Unit = {}
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

    AskPermissionsBody(
        hasSystemAlertWindowPermission = hasSystemAlertWindowPermission,
        hasPackageUsageStatsPermission = hasPackageUsageStatsPermission,
        startPermissionActivity = { intent ->
            startPermissionActivity(intent)
            hasSystemAlertWindowPermission = checkSystemAlertWindowPermission(context)
            hasPackageUsageStatsPermission = checkPackageUsageStatsPermission(context)
        }
    )
}

@SuppressLint("InlinedApi")
@Composable
fun AskPermissionsBody(
    hasSystemAlertWindowPermission: Boolean,
    hasPackageUsageStatsPermission: Boolean,
    startPermissionActivity: (Intent) -> Unit = {}
){
    Column {

        if (!hasSystemAlertWindowPermission) {
            PermissionRequestCard(
                message = {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(stringResource(R.string.display_over_other_apps))
                            }
                            append(stringResource(R.string.display_over_other_apps_explanation))
                        },
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                    )
                },
                onGrantClick = {
                    startPermissionActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
                }
            )
        }

        if (!hasPackageUsageStatsPermission) {
            PermissionRequestCard(
                message = {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(stringResource(R.string.app_usage_access))
                            }
                            append(stringResource(R.string.app_usage_access_explanation))
                        },
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                    )
                },
                onGrantClick = {
                    startPermissionActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                }
            )
        }

    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PermissionRequestCard(
    message: @Composable () -> Unit = {},
    onGrantClick: () -> Unit = {}
) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(R.dimen.card_elevation)
        ),
        colors = CardDefaults.cardColors(
            MaterialTheme.colorScheme.errorContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_small))
    ) {
        message()
        Row {
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onGrantClick,
                colors = ButtonDefaults.buttonColors(Color.Transparent),
            ) {
                Text(
                    text = stringResource(R.string.grant_permission),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLargeEmphasized,
                    color = Color(0xff140d07)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AskPermissionsPreview(){
    AskPermissionsBody(false,false)
}