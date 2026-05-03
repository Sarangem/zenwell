/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.customactivity

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import coil3.compose.AsyncImage
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.sizing
import com.sarangem.zenwell.utils.PackageInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListDropdownCard(
    currentApp: String,
    onCurrentAppChange: (String) -> Unit,
    installedAppsList: List<PackageInfo>,
) {
    var expanded by remember { mutableStateOf(false) }
    val currentApp = installedAppsList.firstOrNull { it.packageName == currentApp }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            readOnly = true,
            value = TextFieldValue(currentApp?.appName ?: stringResource(R.string.select_app)),
            onValueChange = {},
            leadingIcon = {
                AsyncImage(
                    currentApp?.icon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(MaterialTheme.sizing.small)
                        .clip(RoundedCornerShape(MaterialTheme.sizing.small))
                        .size(MaterialTheme.sizing.image)
                )
            },
            trailingIcon = @Composable {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = !expanded },
            shape = MaterialTheme.shapes.large
        ) {
            installedAppsList.forEach {
                DropdownMenuItem(
                    text = {
                        Text(
                            it.appName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    onClick = {
                        onCurrentAppChange(it.packageName)
                        expanded = !expanded
                    },
                    leadingIcon = {
                        AsyncImage(
                            it.icon,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(MaterialTheme.sizing.small)
                                .clip(RoundedCornerShape(MaterialTheme.sizing.small))
                                .size(MaterialTheme.sizing.image)
                        )
                    },
                )
            }
        }
    }

}