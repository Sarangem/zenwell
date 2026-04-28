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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import com.sarangem.zenwell.R
import com.sarangem.zenwell.utils.PackageInfo

@Composable
fun CustomActivityCard(
    modifier: Modifier,
    appName: CustomActivityUiState,
    installedAppsList: List<PackageInfo>,
    updateAppNames: (CustomActivityUiState) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit
) {
    val isExpandedWidth = currentWindowAdaptiveInfo().windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp)
    ) {

        if(isExpandedWidth){
            Row(
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_small))
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
                ) {
                    AppListDropdownCard(
                        currentApp = appName.packageName,
                        onCurrentAppChange = {
                            updateAppNames(appName.copy(packageName = it))
                        },
                        installedAppsList = installedAppsList
                    )
                    SaveAndDeleteButton(appName.isSaved, onSave, onDelete)
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
                ) {
                    ViewTitleField(appName.viewTitle) { updateAppNames(appName.copy(viewTitle = it)) }
                    ViewIdField(appName.viewId) { updateAppNames(appName.copy(viewId = it)) }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_small))
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
            ){
                AppListDropdownCard(
                    currentApp = appName.packageName,
                    onCurrentAppChange = {
                        updateAppNames(appName.copy(packageName = it))
                    },
                    installedAppsList = installedAppsList
                )
                ViewTitleField(appName.viewTitle) { updateAppNames(appName.copy(viewTitle = it)) }
                ViewIdField(appName.viewId) { updateAppNames(appName.copy(viewId = it)) }
                SaveAndDeleteButton(appName.isSaved, onSave, onDelete)
            }
        }

    }
}

@Composable
fun ViewTitleField(
    value: String,
    onValueChange: (String) -> Unit = {}
){
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
){
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(stringResource(R.string.view_id)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}