/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.sarangem.zenwell.GITHUB_REPO_ISSUES_URL
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import com.sarangem.zenwell.ui.theme.sizing

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    openCustomActivityScreen: () -> Unit = {}
){
    val viewModel: SettingsViewModel = hiltViewModel()
    val context = LocalContext.current

    val createBackup = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        viewModel.createBackup(uri, context)
    }
    val restoreBackup = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        viewModel.restoreBackup(uri, context)
    }

    SettingsScreen(modifier, openCustomActivityScreen,
        createBackup = { createBackup.launch("zenwell_backup.json") },
        restoreBackup = { restoreBackup.launch(arrayOf("application/json")) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    openCustomActivityScreen: () -> Unit = {},
    createBackup: () -> Unit = {},
    restoreBackup: () -> Unit = {}
) {
    val uriHandler = LocalUriHandler.current
    var showCrashLog by remember { mutableStateOf(false) }
    if (showCrashLog) CrashLogDialog { showCrashLog = false }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        style = MaterialTheme.typography.headlineSmall,
                        maxLines = 1,
                    )
                },
            )
        }
    ) { innerPadding ->

        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = MaterialTheme.sizing.small)
                .padding(bottom = MaterialTheme.sizing.medium)
                .padding(bottom = if (!currentWindowAdaptiveInfo().windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) MaterialTheme.sizing.floatingBar else 0.dp),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.sizing.medium)
        ) {
            Column {
                Text(
                    stringResource(R.string.data_backup),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = MaterialTheme.sizing.extraSmall)
                )
                Column(
                    modifier = modifier.clip(MaterialTheme.shapes.medium),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.sizing.tiny)
                ) {
                    SettingsActionCard(
                        modifier = Modifier.fillMaxWidth(),
                        icon = R.drawable.outlined_backup,
                        title = stringResource(R.string.export_data),
                        description = stringResource(R.string.export_data_description),
                        onClick = createBackup
                    )
                    SettingsActionCard(
                        modifier = Modifier.fillMaxWidth(),
                        icon = R.drawable.filled_settings_backup_restore,
                        title = stringResource(R.string.restore_data),
                        description = stringResource(R.string.restore_data_description),
                        onClick = restoreBackup
                    )
                }
            }

            Column {
                Text(
                    stringResource(R.string.report_issue),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = MaterialTheme.sizing.extraSmall)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.sizing.tiny)
                ) {
                    SettingsActionCard(
                        modifier = Modifier.fillMaxWidth(),
                        icon = R.drawable.outlined_bug_report,
                        title = stringResource(R.string.app_crash_log),
                        description = stringResource(R.string.app_crash_log_description),
                        onClick = { showCrashLog = true }
                    )
                    SettingsActionCard(
                        modifier = Modifier.fillMaxWidth(),
                        icon = R.drawable.outlined_report,
                        title = stringResource(R.string.report_issue),
                        description = stringResource(R.string.report_issue_description),
                        onClick = { uriHandler.openUri(GITHUB_REPO_ISSUES_URL) }
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    Modifier
                        .clickable(onClick = openCustomActivityScreen)
                        .padding(MaterialTheme.sizing.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.edit_custom_views),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = openCustomActivityScreen) {
                        Icon(
                            painterResource(R.drawable.filled_arrow_right),
                            contentDescription = null
                        )
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    ZenwellTheme {
        SettingsScreen()
    }
}