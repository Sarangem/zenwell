/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.edit.fields

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import com.sarangem.zenwell.utils.PackageInfo
import com.sarangem.zenwell.utils.getInstalledApps
import androidx.core.graphics.drawable.toDrawable
import com.sarangem.zenwell.database.tables.AppNames

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseAppList(
    appNames: List<String>?,
    viewsList: List<AppNames>,
    updateValue: (List<String>) -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }

    DetailsCardColumn{
        DetailsCard(
            modifier = Modifier.clickable { expanded = true }
        ) {
            Text(
                text = stringResource(R.string.choose_apps),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                    contentDescription = stringResource(R.string.show_apps_to_block)
                )
            }
        }
    }


    if (expanded) {
        ModalBottomSheet(
            onDismissRequest = { expanded = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            BottomSheetContents(
                getInstalledApps = { getInstalledApps(it) },
                checkedAppList = appNames,
                viewsList = viewsList,
                addAppToList = {
                    if (appNames != null) {
                        updateValue(appNames + it)
                    }
                },
                removeAppFromList = {
                    if (appNames != null) {
                        updateValue(appNames - it)
                    }
                }
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BottomSheetContents(
    modifier: Modifier = Modifier,
    getInstalledApps: (Context) -> List<PackageInfo>,
    viewsList: List<AppNames> = listOf(),
    checkedAppList: List<String>? = listOf(),
    addAppToList: (String) -> Unit = {},
    removeAppFromList: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var installedAppList by remember { mutableStateOf<List<PackageInfo>>(listOf()) }
    LaunchedEffect(Unit) {
        val list = getInstalledApps(context).toMutableList()
        viewsList.forEach { view ->
            val element = list.firstOrNull { it.packageName == view.title.substringBefore(":id/") }
            element?.let {
                list.add(
                    PackageInfo(
                        packageName = view.title,
                        appName = view.viewTitle ?: "",
                        icon = element.icon
                    )
                )
            }
        }
        list.sortBy { it.appName.lowercase() }
        installedAppList = list
    }

    if (installedAppList.isEmpty() || checkedAppList == null) {
        LoadingIndicator(
            Modifier
                .fillMaxWidth()
                .padding(
                    top = dimensionResource(R.dimen.image_size),
                    bottom = dimensionResource(R.dimen.image_size)
                )
        )
    } else {
        LazyColumn(
            modifier = modifier
                .padding(dimensionResource(R.dimen.padding_small))
                .clip(RoundedCornerShape(dimensionResource(R.dimen.padding_large))),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
        ) {
            items(installedAppList) { app ->
                AppCard(
                    label = app.appName,
                    icon = app.icon,
                    checkedValue = app.packageName in checkedAppList,
                    onCheckedChange = { value ->
                        if (value) addAppToList(app.packageName)
                        else removeAppFromList(app.packageName)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    label: String,
    icon: Drawable? = null,
    checkedValue: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceDim),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(dimensionResource(R.dimen.padding_small)))

            Image(
                painter = rememberDrawablePainter(icon),
                contentDescription = null,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_small))
                    .clip(RoundedCornerShape(dimensionResource(R.dimen.padding_small)))
                    .size(dimensionResource(R.dimen.image_size))
            )


        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_small))
                .weight(1f)
        )
        Checkbox(
            checked = checkedValue,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
fun ShowBottomSheetPreview() {
    val icon = Color.Red.toArgb().toDrawable()
    ZenwellTheme {
        BottomSheetContents(
            getInstalledApps = { _ ->
                listOf(
                    PackageInfo(appName = "Calendar", icon = icon, packageName = "calendar"),
                    PackageInfo(appName = "Messages", icon = icon, packageName = "messages"),
                    PackageInfo(appName = "Youtube", icon = icon, packageName = "youtube")
                )
            },
        )
    }
}