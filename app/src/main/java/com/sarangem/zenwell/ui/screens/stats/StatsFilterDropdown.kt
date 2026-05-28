/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImage
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.sizing
import com.sarangem.zenwell.utils.MyPackageInfo

sealed interface StatsFilter {
    data object AllApps : StatsFilter
    data object BlockedApps : StatsFilter
    data class Custom(val packageName: String) : StatsFilter
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsFilterDropdown(
    modifier: Modifier = Modifier,
    selectedFilter: StatsFilter,
    installedAppList: List<MyPackageInfo>,
    onFilterSelected: (StatsFilter) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedText = when (selectedFilter) {
        is StatsFilter.AllApps -> stringResource(R.string.all_apps)
        is StatsFilter.BlockedApps -> stringResource(R.string.blocked_apps_only)
        is StatsFilter.Custom -> installedAppList
            .firstOrNull { it.packageName == selectedFilter.packageName}
            ?.appName ?: ""
    }

    Row(
        modifier = modifier.padding(MaterialTheme.sizing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.sizing.small)
    ) {
        Text(stringResource(R.string.bar_graph_for),)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            AsyncImage(
                if (selectedFilter is StatsFilter.Custom)
                    installedAppList.firstOrNull { it.packageName == selectedFilter.packageName }
                else null,
                contentDescription = null,
                modifier = Modifier
                    .size(MaterialTheme.sizing.image)
                    .padding(end = MaterialTheme.sizing.small)
            )
            OutlinedTextField(
                modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                shape = MaterialTheme.shapes.large
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.all_apps)) },
                    onClick = {
                        onFilterSelected(StatsFilter.AllApps)
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.blocked_apps_only)) },
                    onClick = {
                        onFilterSelected(StatsFilter.BlockedApps)
                        expanded = false
                    }
                )
                installedAppList.forEach { appInfo ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (appInfo.icon != null) {
                                    AsyncImage(
                                        appInfo.icon,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(MaterialTheme.sizing.image)
                                            .padding(end = MaterialTheme.sizing.small)
                                    )
                                }
                                Text(text = appInfo.appName)
                            }
                        },
                        onClick = {
                            onFilterSelected(
                                StatsFilter.Custom(appInfo.packageName)
                            )
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}