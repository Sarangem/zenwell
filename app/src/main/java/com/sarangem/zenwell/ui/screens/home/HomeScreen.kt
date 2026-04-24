/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.home

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sarangem.zenwell.R
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.service.AppBlockerService
import com.sarangem.zenwell.service.PomodoroWindow
import com.sarangem.zenwell.ui.screens.AppViewModelProvider
import com.sarangem.zenwell.ui.theme.ZenwellTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    scheduleClicked: Int = 0,
    openEditScreen: (Schedules) -> Unit = {},
    openFocusScreen: (Schedules) -> Unit = {},
    openSettingsScreen: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val homeUiState by viewModel.uiState.collectAsState()
    LaunchedEffect(homeUiState.schedulesList) {
        viewModel.recheckNotificationPermission(context)
    }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        viewModel.recheckNotificationPermission(context)
    }
    val grantNotificationPermission = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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
    ) {
        viewModel.updateUiState(
            homeUiState.copy(
                showAccessibilityPermissionRationale = AppBlockerService.instance == null
            )
        )
    }
    val grantAccessibilityPermission = { accessibilityPermissionLauncher.launch(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }

    HomeScreen(
        modifier,
        homeUiState,
        scheduleClicked,
        viewModel::addNewSchedule,
        viewModel::updateUiState,
        grantAccessibilityPermission,
        grantNotificationPermission,
        openEditScreen,
        openFocusScreen,
        openSettingsScreen
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    scheduleClicked: Int = 0,
    addNewSchedule: suspend (Context, Boolean) -> Schedules = { _, _ -> Schedules() },
    updateUiState: (HomeUiState) -> Unit = {},
    grantAccessibilityPermission: () -> Unit = {},
    grantNotificationPermission: () -> Unit = {},
    openEditScreen: (Schedules) -> Unit = {},
    openFocusScreen: (Schedules) -> Unit = {},
    openSettingsScreen: () -> Unit = {},
    pomodoroWindow: PomodoroWindow? = null
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
                },
                actions = {
                    IconButton(onClick = openSettingsScreen) {
                        Icon(
                            painterResource(R.drawable.outlined_settings),
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            NewScheduleFAB(
                addNewSchedule = addNewSchedule,
                openEditScreen = openEditScreen,
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedVisibility(uiState.showAccessibilityPermissionRationale) {
                AccessibilityPermissionCard {
                    grantAccessibilityPermission()
                }
            }
            AnimatedVisibility(uiState.showNotificationPermissionRationale) {
                NotificationPermissionCard {
                    grantNotificationPermission()
                }
            }
            AnimatedContent(
                targetState = uiState.schedulesList.isEmpty(),
                modifier = Modifier.fillMaxSize()
            ){state ->
                if(state){
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = stringResource(R.string.home_screen_description),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                        )
                    }
                } else {
                    Column(Modifier.fillMaxSize()){
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
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            contentPadding = PaddingValues(
                                bottom = dimensionResource(R.dimen.floating_action_button_height)
                            )
                        ) {
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
                                            .padding(dimensionResource(R.dimen.padding_small)),
                                        schedule = schedule,
                                        isClicked = schedule.id == scheduleClicked,
                                        openEditScreen = openEditScreen,
                                        openFocusScreen = openFocusScreen,
                                        pomodoroWindow = pomodoroWindow ?: AppBlockerService.instance?.getPomodoroWindow(schedule.id),
                                    )
                                }
                            }
                        }
                    }
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
            pomodoroWindow = null
        )
    }
}
