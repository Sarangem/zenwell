/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.stats

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.screens.home.permission.PermissionRequestCard
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import com.sarangem.zenwell.ui.theme.sizing
import com.sarangem.zenwell.utils.MyPackageInfo
import com.sarangem.zenwell.utils.getInstalledApps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun StatsScreen(modifier: Modifier = Modifier) {
    val viewModel: StatsViewModel = hiltViewModel()
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val settingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { viewModel.getStats(context) }

    var installedAppList by remember { mutableStateOf<List<MyPackageInfo>>(listOf()) }
    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            val list = getInstalledApps(context).toMutableList()
            list.sortBy { it.appName.lowercase() }
            installedAppList = list
        }
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.getStats(context) }

    StatsScreen(
        modifier = modifier,
        installedAppList = installedAppList,
        uiState = uiState,
        updateUiState = { viewModel.updateUiState(it) },
        requestPermission = { settingsLauncher.launch(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StatsScreen(
    modifier: Modifier = Modifier,
    installedAppList: List<MyPackageInfo> = listOf(),
    uiState: StatsUiState,
    updateUiState: (StatsUiState) -> Unit = {},
    requestPermission: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.statistics),
                        style = MaterialTheme.typography.headlineSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = MaterialTheme.sizing.small)
                .padding(bottom = MaterialTheme.sizing.medium)
                .padding(bottom = if (!currentWindowAdaptiveInfoV2().windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) MaterialTheme.sizing.floatingBar else 0.dp),
            ) {
            AnimatedContent(!uiState.isPermissionGranted) {
                if(it){
                    PermissionRequestCard(
                        modifier = Modifier.padding(vertical = MaterialTheme.sizing.small),
                        onGrantClick = requestPermission,
                        name = R.string.usage_stats_permission
                    )
                } else if (uiState.isLoading) {
                    LoadingIndicator(
                        Modifier
                            .fillMaxSize()
                            .padding(vertical = MaterialTheme.sizing.image)
                    )
                } else {
                    GraphicalUsageStats(
                        installedAppList = installedAppList,
                        uiState = uiState,
                        updateUiState = updateUiState
                    )
                }
            }
        }
    }
}

@Composable
fun GraphicalUsageStats(
    modifier: Modifier = Modifier,
    installedAppList: List<MyPackageInfo> = listOf(),
    uiState: StatsUiState,
    updateUiState: (StatsUiState) -> Unit = {}
){
    val graph: @Composable (Modifier) -> Unit = { modifier ->
        Column(
            modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UsageBarGraph(
                dailyUsage = uiState.dailyUsage,
                installedAppList = installedAppList,
                blockedApps = uiState.blockedApps,
                statsFilter = uiState.statsFilter,
                modifier = Modifier.fillMaxSize()
            )
            StatsFilterDropdown(
                selectedFilter = uiState.statsFilter,
                onFilterSelected = {
                    updateUiState(
                        uiState.copy(
                            statsFilter = it
                        )
                    )
                },
                installedAppList = installedAppList
            )
        }
    }
    if(currentWindowAdaptiveInfoV2().windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)){
        Row(
            modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.sizing.small)
        ) {
            MotivationalCard(uiState.dailyUsage, uiState.weeklyAverageInMinutes, Modifier.weight(1f))
            graph(Modifier.weight(4f))
        }
    } else {
        Column(
            modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.sizing.small)
        ) {
            MotivationalCard(uiState.dailyUsage, uiState.weeklyAverageInMinutes)
            graph(Modifier)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatsScreenPreview() {
    ZenwellTheme(darkTheme = true) {
        StatsScreen(uiState = StatsUiState(
            isPermissionGranted = false
        ))
    }
}