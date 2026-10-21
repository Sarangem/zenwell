/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.customactivity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallExtendedFloatingActionButton
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sarangem.zenwell.R
import com.sarangem.zenwell.model.AppNames
import com.sarangem.zenwell.ui.screens.edit.fields.ShowConfirmDialog
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import com.sarangem.zenwell.ui.theme.sizing
import com.sarangem.zenwell.domain.packageinfo.MyPackageInfo
import com.sarangem.zenwell.domain.packageinfo.getInstalledApps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CustomActivityScreen(
    modifier: Modifier = Modifier,
    viewModel: CustomActivityViewModel,
    showTopAppBar: Boolean = true,
    goBack: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var installedAppList by remember { mutableStateOf<List<MyPackageInfo>>(listOf()) }
    LaunchedEffect(Unit) {
        viewModel.initialize()
        launch(Dispatchers.IO) {
            installedAppList = getInstalledApps(context).sortedBy { it.appName.lowercase() }
        }
    }

    CustomActivityScreen(
        modifier = modifier,
        goBack = goBack,
        showTopAppBar = showTopAppBar,
        installedAppList = installedAppList,
        uiState = uiState,
        onAddNew = viewModel::onAddNew,
        onUpdate = viewModel::updateUiState,
        onSave = viewModel::onSave,
        onDelete = viewModel::onDelete,
        onReset = viewModel::onReset
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CustomActivityScreen(
    modifier: Modifier = Modifier,
    goBack: () -> Unit = {},
    showTopAppBar: Boolean = true,
    installedAppList: List<MyPackageInfo> = listOf(),
    uiState: List<CustomActivityUiState>,
    onAddNew: () -> Unit = {},
    onUpdate: (Int, CustomActivityUiState) -> Unit = { _, _ -> },
    onSave: (CustomActivityUiState) -> Unit = {},
    onDelete: (CustomActivityUiState) -> Unit = {},
    onReset: () -> Unit = {}
) {
    var showResetDialog by remember { mutableStateOf(false) }
    if (showResetDialog) {
        ShowConfirmDialog(
            icon = R.drawable.filled_history,
            title = stringResource(R.string.restore_custom_views),
            description = stringResource(R.string.restore_custom_views_description),
            onDismiss = { showResetDialog = false },
            onConfirm = {
                showResetDialog = false
                onReset()
            }
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            if (showTopAppBar) {
                TopAppBar(
                    title = { Text(stringResource(R.string.edit_custom_views)) },
                    navigationIcon = {
                        IconButton(onClick = goBack) {
                            Icon(
                                painterResource(R.drawable.filled_arrow_back),
                                contentDescription = stringResource(R.string.go_back)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { showResetDialog = true }) {
                            Icon(
                                painterResource(R.drawable.filled_history),
                                contentDescription = stringResource(R.string.restore_custom_views)
                            )
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                if (!showTopAppBar) {
                    SmallFloatingActionButton(
                        onClick = { showResetDialog = true },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(MaterialTheme.sizing.small),
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.sizing.small)
                        ) {
                            Icon(
                                painterResource(R.drawable.filled_history),
                                contentDescription = null
                            )
                            Text(stringResource(R.string.restore_custom_views))
                        }
                    }
                }
                SmallExtendedFloatingActionButton(
                    onClick = onAddNew
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.sizing.small)) {
                        Icon(
                            painterResource(R.drawable.filled_add),
                            contentDescription = null
                        )
                        Text(stringResource(R.string.add_new_custom_view))
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(MaterialTheme.sizing.small)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.sizing.tiny)
        ) {
            itemsIndexed(
                items = uiState,
                key = { index, item -> if (item.appName.id != 0) item.appName.id else "draft_$index" }
            ) { index, item ->
                val isFirst = index == 0
                val isLast = index == uiState.size - 1
                val cardShape = RoundedCornerShape(
                    topStart = if (isFirst) MaterialTheme.shapes.medium.topStart else CornerSize(0.dp),
                    topEnd = if (isFirst) MaterialTheme.shapes.medium.topEnd else CornerSize(0.dp),
                    bottomStart = if (isLast) MaterialTheme.shapes.medium.bottomStart else CornerSize(0.dp),
                    bottomEnd = if (isLast) MaterialTheme.shapes.medium.bottomEnd else CornerSize(0.dp)
                )
                CustomActivityCard(
                    item = item,
                    installedAppsList = installedAppList,
                    updateCustomActivity = { onUpdate(index, it) },
                    onSave = { onSave(item) },
                    onDelete = { onDelete(item) },
                    modifier = Modifier.clip(cardShape)
                )
            }
            item {
                Spacer(Modifier.height(MaterialTheme.sizing.floatingBar))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomActivityScreenPreview() {
    ZenwellTheme {
        CustomActivityScreen(
            uiState = listOf(
                CustomActivityUiState(
                    appName = AppNames(id = 1, packageName = "youtube", viewId = "shorts", viewTitle = "Youtube Shorts"),
                    isSaved = true
                ),
                CustomActivityUiState(
                    appName = AppNames(id = 2, packageName = "insta", viewId = "reels", viewTitle = "Instagram Reels"),
                    isSaved = true
                )
            ),
            installedAppList = listOf(
                MyPackageInfo("youtube", "youtube", Color.Red.toArgb().toDrawable())
            )
        )
    }
}