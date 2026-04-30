/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.settings

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sarangem.zenwell.GITHUB_REPO_ISSUES_URL
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.ZenwellTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    openCustomActivityScreen: () -> Unit = {},
    goBack: () -> Unit = {}
) {
    val uriHandler = LocalUriHandler.current
    var showCrashLog by remember { mutableStateOf(false) }
    if (showCrashLog) {
        CrashLogDialog { showCrashLog = false }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(
                            painterResource(R.drawable.filled_arrow_back),
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                },
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
                .padding(horizontal = dimensionResource(R.dimen.padding_small))
                .padding(bottom = dimensionResource(R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {
            DataTransferCard(Modifier.fillMaxWidth())

            Column {
                Text(
                    stringResource(R.string.report_issue),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_tiny))
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
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
                        .padding(dimensionResource(R.dimen.padding_small)),
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
        Column {
            DataTransferCard(Modifier.fillMaxWidth()){}
            Spacer(Modifier.weight(1f))
        }
    }
}