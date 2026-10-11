/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.edit.fields

import android.graphics.drawable.Drawable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
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
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.rememberBottomSheetState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.sarangem.zenwell.R
import com.sarangem.zenwell.database.tables.AppNames
import com.sarangem.zenwell.ui.screens.home.SkipGuideButton
import com.sarangem.zenwell.ui.sequenceshowcase.LocalSequenceShowcaseState
import com.sarangem.zenwell.ui.sequenceshowcase.sequenceShowcaseTarget
import com.sarangem.zenwell.ui.theme.sizing
import com.sarangem.zenwell.utils.MyPackageInfo
import com.sarangem.zenwell.utils.getInstalledApps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseAppList(
    appNames: List<AppNames>?,
    viewsList: List<AppNames>,
    updateValue: (List<AppNames>) -> Unit = {},
    scrollState: ScrollState? = null,
    firstEntry: Boolean = false,
    setAsExistingUser: () -> Unit = {}
) {
    val showcaseState = LocalSequenceShowcaseState.current
    val context = LocalContext.current
    var installedAppList by remember { mutableStateOf<List<MyPackageInfo>>(listOf()) }

    LaunchedEffect(viewsList) {
        launch(Dispatchers.IO) {
            val systemApps = getInstalledApps(context)
            val list = mutableListOf<MyPackageInfo>()
            list.addAll(systemApps)

            // Map custom views and pass viewKeyword along
            viewsList.forEach { view ->
                val element = systemApps.firstOrNull { it.packageName == view.packageName }
                element?.let {
                    list.add(
                        MyPackageInfo(
                            packageName = view.packageName,
                            appName = view.viewTitle ?: view.packageName,
                            icon = it.icon,
                            viewId = view.viewId,
                            viewKeyword = view.viewKeyword
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
                .filter { item ->
                    appNames?.any {
                        it.packageName == item.packageName &&
                                it.viewId == item.viewId &&
                                it.viewKeyword == item.viewKeyword
                    } == true
                }
                .joinToString(", ") { it.appName }
                .ifEmpty { null }
        }
    }
    var expanded by remember { mutableStateOf(false) }

    DetailsCardColumn {
        DetailsCard(
            showcase2Modifier(scrollState, setAsExistingUser)
                .clickable {
                    expanded = true
                    showcaseState.dismiss()
                }
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.sizing.small)
            ) {
                Text(
                    text = stringResource(R.string.choose_apps),
                    style = MaterialTheme.typography.bodyLarge
                )
                AnimatedVisibility(installedAppList.isNotEmpty() || appNames?.isEmpty() == true) {
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
            onDismissRequest = {
                expanded = false
                if (firstEntry) showcaseState.start(3)
            },
            sheetState = rememberBottomSheetState(initialValue = SheetValue.Hidden)
        ) {
            BottomSheetContents(
                installedAppList = installedAppList,
                checkedAppList = appNames,
                addAppToList = { appRule ->
                    if (appNames != null) {
                        updateValue(appNames + appRule)
                    }
                },
                removeAppFromList = { appRule ->
                    if (appNames != null) {
                        // Accurately filter out by packageName, viewId, AND viewKeyword
                        updateValue(
                            appNames.filterNot {
                                it.packageName == appRule.packageName &&
                                        it.viewId == appRule.viewId &&
                                        it.viewKeyword == appRule.viewKeyword
                            }
                        )
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
    checkedAppList: List<AppNames>? = listOf(),
    addAppToList: (AppNames) -> Unit = {},
    removeAppFromList: (AppNames) -> Unit = {}
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
            items(installedAppList) { item ->
                val isChecked = checkedAppList.any {
                    it.packageName == item.packageName &&
                            it.viewId == item.viewId &&
                            it.viewKeyword == item.viewKeyword
                }
                AppCard(
                    label = item.appName,
                    icon = item.icon,
                    checkedValue = isChecked,
                    onCheckedChange = { value ->
                        val appRule = AppNames(
                            packageName = item.packageName,
                            viewId = item.viewId,
                            viewKeyword = item.viewKeyword
                        )
                        if (value) addAppToList(appRule)
                        else removeAppFromList(appRule)
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

val showcase2Modifier: @Composable (
    scrollState: ScrollState?,
    skipGuide: () -> Unit
) -> Modifier = { state, skip ->
    Modifier.sequenceShowcaseTarget(
        index = 2,
        shape = MaterialTheme.shapes.medium,
        shapeMargin = 0.dp,
        backgroundAlpha = 0.9f,
        scrollState = state,
        fixedContent = { SkipGuideButton(skip) }
    ) {
        Text(
            text = stringResource(R.string.showcase_2),
            style = MaterialTheme.typography.headlineMedium,
            color = darkColorScheme().onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}