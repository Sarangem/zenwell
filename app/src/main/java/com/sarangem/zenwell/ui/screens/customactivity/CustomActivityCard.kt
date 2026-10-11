/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.customactivity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.sizing
import com.sarangem.zenwell.utils.MyPackageInfo

@Composable
fun CustomActivityCard(
    modifier: Modifier = Modifier,
    item: CustomActivityUiState,
    installedAppsList: List<MyPackageInfo>,
    updateCustomActivity: (CustomActivityUiState) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit
) {
    val isExpandedWidth = currentWindowAdaptiveInfoV2().windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)
    val app = item.appName

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp)
    ) {
        if (isExpandedWidth) {
            Row(
                modifier = Modifier
                    .padding(MaterialTheme.sizing.small)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.sizing.small)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.sizing.small)
                ) {
                    AppListDropdownCard(
                        currentApp = app.packageName,
                        onCurrentAppChange = {
                            updateCustomActivity(item.copy(appName = app.copy(packageName = it)))
                        },
                        installedAppsList = installedAppsList
                    )
                    SaveAndDeleteButton(item.isSaved, onSave, onDelete)
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.sizing.small)
                ) {
                    ViewTitleField(app.viewTitle ?: "") {
                        updateCustomActivity(item.copy(appName = app.copy(viewTitle = it)))
                    }
                    ViewIdField(app.viewId ?: "") {
                        updateCustomActivity(item.copy(appName = app.copy(viewId = it)))
                    }
                    ViewKeywordField(app.viewKeyword ?: "") {
                        updateCustomActivity(item.copy(appName = app.copy(viewKeyword = it)))
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(MaterialTheme.sizing.small)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.sizing.small)
            ) {
                AppListDropdownCard(
                    currentApp = app.packageName,
                    onCurrentAppChange = {
                        updateCustomActivity(item.copy(appName = app.copy(packageName = it)))
                    },
                    installedAppsList = installedAppsList
                )
                ViewTitleField(app.viewTitle ?: "") {
                    updateCustomActivity(item.copy(appName = app.copy(viewTitle = it)))
                }
                ViewIdField(app.viewId ?: "") {
                    updateCustomActivity(item.copy(appName = app.copy(viewId = it)))
                }
                ViewKeywordField(app.viewKeyword ?: "") {
                    updateCustomActivity(item.copy(appName = app.copy(viewKeyword = it)))
                }
                SaveAndDeleteButton(item.isSaved, onSave, onDelete)
            }
        }
    }
}

@Composable
fun ViewTitleField(
    value: String,
    onValueChange: (String) -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(stringResource(R.string.view_title)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
fun ViewIdField(
    value: String,
    onValueChange: (String) -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(stringResource(R.string.view_id)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
fun ViewKeywordField(
    value: String,
    onValueChange: (String) -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(stringResource(R.string.view_keyword)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}