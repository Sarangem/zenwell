/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.sarangem.zenwell.R
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.service.AppBlockerService
import com.sarangem.zenwell.service.PomodoroWindow
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import com.sarangem.zenwell.ui.theme.sizing
import androidx.core.content.edit
import com.sarangem.zenwell.database.tables.UserPreferences

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    scheduleClicked: Int = 0,
    viewModel: HomeViewModel = hiltViewModel(),
    openEditScreen: (Schedules) -> Unit = {},
    openFocusScreen: (Schedules) -> Unit = {}
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val homeUiState by viewModel.uiState.collectAsState()
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.recheckPermission(context) }
    LaunchedEffect(homeUiState.schedulesList) { viewModel.recheckPermission(context) }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { viewModel.recheckPermission(context) }
    val grantNotificationPermission = {
        val showRationale = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && (activity?.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) ?: false)
        val sharedPrefs = context.getSharedPreferences("permissions_prefs", Context.MODE_PRIVATE)
        val hasRequested = sharedPrefs.getBoolean("has_requested_notification", false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && (showRationale || !hasRequested)) {
            sharedPrefs.edit { putBoolean("has_requested_notification", true) }
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
            } else {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
            }
            context.startActivity(intent)
        }
    }
    val accessibilityPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { viewModel.recheckPermission(context) }
    val grantAccessibilityPermission = { accessibilityPermissionLauncher.launch(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }

    HomeScreen(
        modifier,
        homeUiState,
        scheduleClicked,
        viewModel::addNewSchedule,
        viewModel::updateUiState,
        viewModel::updateUserPreferences,
        grantAccessibilityPermission,
        grantNotificationPermission,
        openEditScreen,
        openFocusScreen
    ) { AppBlockerService.instance?.getPomodoroWindow(it) }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    scheduleClicked: Int = 0,
    addNewSchedule: suspend (Context, Boolean) -> Schedules = { _, _ -> Schedules() },
    updateUiState: (HomeUiState) -> Unit = {},
    updateUserPreferences: (UserPreferences) -> Unit = {},
    grantAccessibilityPermission: () -> Unit = {},
    grantNotificationPermission: () -> Unit = {},
    openEditScreen: (Schedules) -> Unit = {},
    openFocusScreen: (Schedules) -> Unit = {},
    getPomodoroWindow: (Int) -> PomodoroWindow? = { null }
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            )
        },
        floatingActionButton = {
            if(currentWindowAdaptiveInfo().windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND))
                NewScheduleFAB(
                    addNewSchedule = addNewSchedule,
                    openEditScreen = openEditScreen
                )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = MaterialTheme.sizing.floatingBar)
        ) {
            item {
                AnimatedVisibility(uiState.showAccessibilityPermissionRationale) {
                    AccessibilityPermissionCard { grantAccessibilityPermission() }
                }
            }
            item {
                AnimatedVisibility(uiState.showNotificationPermissionRationale) {
                    NotificationPermissionCard(
                        onGrantClick = grantNotificationPermission,
                        onDeny = {
                            updateUserPreferences(
                                uiState.userPreferences.copy(
                                    showNotificationPermissionCard = false
                                )
                            )
                        }
                    )
                }
            }
            item {
                AnimatedVisibility(uiState.schedulesList.isNotEmpty()) {
                    SchedulesFilterChips(
                        modifier = Modifier.fillMaxWidth(),
                        filter = uiState.currentFilter,
                        updateFilter = {
                            updateUiState(
                                uiState.copy(
                                    currentFilter = it
                                )
                            )
                        }
                    )
                }
            }
            item {
                AnimatedVisibility(uiState.schedulesList.isEmpty()) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillParentMaxHeight()
                    ) {
                        Text(
                            text = stringResource(R.string.home_screen_description),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(MaterialTheme.sizing.small)
                        )
                    }
                }
            }
            items(
                items = uiState.schedulesList,
                key = { it.id }
            ) { schedule ->
                val showSchedule = when (uiState.currentFilter) {
                    SchedulesFilter.All -> true
                    SchedulesFilter.Regular -> !schedule.isPomodoro
                    SchedulesFilter.Pomodoro -> schedule.isPomodoro
                }
                if (showSchedule) {
                    SchedulesCard(
                        modifier = Modifier
                            .animateItem()
                            .fillMaxWidth()
                            .padding(MaterialTheme.sizing.small),
                        schedule = schedule,
                        isClicked = schedule.id == scheduleClicked,
                        openEditScreen = openEditScreen,
                        openFocusScreen = openFocusScreen,
                        pomodoroWindow = getPomodoroWindow(schedule.id),
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
fun HomeScreenPreviewLightMode() {
    ZenwellTheme(darkTheme = false) {
        HomeScreen(
            uiState = HomeUiState(
                showAccessibilityPermissionRationale = true,
                showNotificationPermissionRationale = true
            )
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun HomeScreenPreviewDarkMode() {
    ZenwellTheme(darkTheme = true) {
        HomeScreen(
            uiState = HomeUiState(
                schedulesList = listOf(
                    Schedules(
                        id = 1,
                        title = "Schedule 1",
                        startTimeInMinutes = 9 * 60 + 14,
                        endTimeInMinutes = 10 * 60 + 17
                    ),
                    Schedules(
                        id = 2,
                        title = "A reallyyyyyyyyyyyyyyyyyyyyyyyyy long name",
                        startTimeInMinutes = 0,
                        endTimeInMinutes = 1 * 60 + 1
                    ),
                    Schedules(
                        id = 3,
                        title = "A very long schedules title with spaces in between",
                        startTimeInMinutes = 7 * 60 + 34,
                        endTimeInMinutes = 11 * 60 + 12,
                        isActive = false
                    )
                )
            ),
            scheduleClicked = 1,
        )
    }
}