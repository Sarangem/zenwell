/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.settings

import android.content.ClipData
import android.content.Context
import android.os.Build
import android.os.PowerManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.sarangem.zenwell.CRASH_LOG_FILE
import com.sarangem.zenwell.GITHUB_REPO_ISSUES_URL
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.sizing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CrashLogDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboard.current
    val uriHandler = LocalUriHandler.current
    val coroutineScope = rememberCoroutineScope()
    var crashLogText by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        val log = withContext(Dispatchers.IO) {
            val file = File(context.filesDir, CRASH_LOG_FILE)
            if (file.exists()) file.readText() else null
        }
        crashLogText = log
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.app_crash_log)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((LocalWindowInfo.current.containerDpSize.height.value * 0.4).dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceDim)
                    .verticalScroll(rememberScrollState())
                    .horizontalScroll(rememberScrollState())
                    .padding(MaterialTheme.sizing.small)
            ) {
                SelectionContainer {
                    Text(
                        text = crashLogText ?: stringResource(R.string.no_logs_found),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }
        },
        confirmButton = {
            AnimatedVisibility(crashLogText != null) {
                Column {
                    Button(
                        onClick = {
                            val title = URLEncoder.encode(
                                "App Crash Report",
                                StandardCharsets.UTF_8.toString()
                            )
                            val log = """
                                [SYS] OS: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT}) | OEM: ${Build.BRAND} | Model: ${Build.MODEL}
                                [BUILD] ID: ${Build.ID} | Fingerprint: ${Build.FINGERPRINT}
                                [STATE] Batt. Opt. Ignored: ${(context.getSystemService(Context.POWER_SERVICE) as PowerManager).isIgnoringBatteryOptimizations(context.packageName)}
                                $crashLogText
                            """.trimIndent().take(1000)
                            val body = URLEncoder.encode(
                                "### Crash Log\n```\n$log\n```",
                                StandardCharsets.UTF_8.toString()
                            )
                            uriHandler.openUri("$GITHUB_REPO_ISSUES_URL?title=$title&body=$body")
                        },
                        modifier = Modifier.padding(bottom = MaterialTheme.sizing.small)
                    ) {
                        Text(stringResource(R.string.report_issue))
                    }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                clipboardManager.setClipEntry(
                                    ClipData.newPlainText("Crash Log", crashLogText).toClipEntry()
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(stringResource(R.string.copy_to_clipboard))
                    }
                }
            }
        },
        dismissButton = {
            Column {
                AnimatedVisibility(crashLogText != null) {
                    TextButton(
                        modifier = Modifier.padding(bottom = MaterialTheme.sizing.small),
                        onClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                File(context.filesDir, CRASH_LOG_FILE).delete()
                                crashLogText = null
                            }
                        }
                    ) {
                        Text(stringResource(R.string.clear))
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.ok))
                }
            }
        }
    )
}