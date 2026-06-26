/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.edit.fields

import android.graphics.drawable.Drawable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.drawable.toDrawable
import coil3.compose.AsyncImage
import com.sarangem.zenwell.R
import com.sarangem.zenwell.database.tables.AppNames
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import com.sarangem.zenwell.ui.theme.sizing
import com.sarangem.zenwell.utils.MyPackageInfo
import com.sarangem.zenwell.utils.getInstalledApps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseAppList(
    appNames: List<String>?,
    viewsList: List<AppNames>,
    updateValue: (List<String>) -> Unit = {},
) {
    val context = LocalContext.current
    var installedAppList by remember { mutableStateOf<List<MyPackageInfo>>(listOf()) }
    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            val list = getInstalledApps(context).toMutableList()
            viewsList.forEach { view ->
                val element = list.firstOrNull { it.packageName == view.title.substringBefore(":id/") }
                element?.let {
                    list.add(
                        MyPackageInfo(
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
    }
    val selectedAppsText by remember(installedAppList, appNames) {
        derivedStateOf {
            installedAppList
                .filter { appNames?.contains(it.packageName) == true }
                .joinToString(", ") { it.appName }
                .ifEmpty { null }
        }
    }
    var expanded by remember { mutableStateOf(false) }

    DetailsCardColumn {
        DetailsCard(Modifier.clickable { expanded = true }) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.sizing.small)
            ){
                Text(
                    text = stringResource(R.string.choose_apps),
                    style = MaterialTheme.typography.bodyLarge
                )
                AnimatedVisibility(installedAppList.isNotEmpty() || appNames?.isEmpty() == true){
                    Text(
                        text = selectedAppsText ?: stringResource(R.string.none_selected_yet),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
            IconButton(onClick = { expanded = true }) {
                Icon(
                    painterResource(R.drawable.filled_arrow_right),
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
                installedAppList = installedAppList,
                checkedAppList = appNames,
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
    installedAppList: List<MyPackageInfo>,
    checkedAppList: List<String>? = listOf(),
    addAppToList: (String) -> Unit = {},
    removeAppFromList: (String) -> Unit = {}
) {
    if (installedAppList.isEmpty() || checkedAppList == null) {
        LoadingIndicator(
            Modifier
                .fillMaxWidth()
                .padding(vertical = MaterialTheme.sizing.image)
        )
    } else {
        LazyColumn(
            modifier = modifier
                .padding(MaterialTheme.sizing.small)
                .clip(RoundedCornerShape(MaterialTheme.sizing.large)),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.sizing.tiny)
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
        Spacer(Modifier.width(MaterialTheme.sizing.small))
        AsyncImage(
            icon,
            contentDescription = null,
            modifier = Modifier
                .padding(MaterialTheme.sizing.small)
                .clip(RoundedCornerShape(MaterialTheme.sizing.small))
                .size(MaterialTheme.sizing.image)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(MaterialTheme.sizing.small)
                .weight(1f)
        )
        Checkbox(
            checked = checkedValue,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(MaterialTheme.sizing.small)
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
            installedAppList = listOf(
                MyPackageInfo(appName = "Calendar", icon = icon, packageName = "c"),
                MyPackageInfo(appName = "Messages", icon = icon, packageName = "m"),
                MyPackageInfo(appName = "YouTube", icon = icon, packageName = "y")
            )
        )
    }
}