/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.home

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.sarangem.zenwell.R
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.service.pomodoro.PomodoroState
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import com.sarangem.zenwell.ui.theme.sizing
import com.sarangem.zenwell.ui.common.sequenceshowcase.LocalSequenceShowcaseState
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    scheduleClicked: Int = 0,
    viewModel: HomeViewModel = hiltViewModel(),
    openEditScreen: (Schedules) -> Unit = {},
    openPomodoroScreen: (Schedules) -> Unit = {},
) {
    val showcaseState = LocalSequenceShowcaseState.current
    val homeUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pomodoroState by viewModel.pomodoroState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val singlePomodoroSessionRes = stringResource(R.string.error_single_pomodoro)
    
    // permission checks
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.recheckPermission() }

    // start intro showcase
    val lazyListState = rememberLazyListState()
    LaunchedEffect(
        homeUiState.userPreferences.firstEntry,
        homeUiState.showAccessibilityPermissionRationale
    ) {
        if (!homeUiState.userPreferences.firstEntry) return@LaunchedEffect
        showcaseState.dismiss()
        if (homeUiState.showAccessibilityPermissionRationale) {
            lazyListState.animateScrollToItem(0)
            showcaseState.start(0)
        } else {
            showcaseState.start(1)
        }
    }

    HomeScreen(
        modifier,
        homeUiState,
        lazyListState,
        scheduleClicked,
        viewModel::addNewSchedule,
        viewModel::updateUiState,
        recheckPermission = { viewModel.recheckPermission() },
        openEditScreen = openEditScreen,
        openPomodoroScreen = openPomodoroScreen,
        setAsExistingUser = viewModel::setAsExistingUser,
        enableAllSchedules = viewModel::enableAllSchedules,
        userScrollEnabled = !showcaseState.showCaseVisible,
        startPomodoro = { schedule ->
            val started = viewModel.startPomodoro(context, schedule)
            if (!started) {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = singlePomodoroSessionRes
                    )
                }
            }
        },
        endPomodoro = viewModel::endPomodoro,
        pomodoroState = pomodoroState,
        snackbarHostState = snackbarHostState
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    lazyListState: LazyListState = rememberLazyListState(),
    scheduleClicked: Int = 0,
    addNewSchedule: suspend (Context, Boolean) -> Schedules = { _, _ -> Schedules() },
    updateUiState: (HomeUiState) -> Unit = {},
    recheckPermission: () -> Unit = {},
    openEditScreen: (Schedules) -> Unit = {},
    openPomodoroScreen: (Schedules) -> Unit = {},
    setAsExistingUser: () -> Unit = {},
    enableAllSchedules: () -> Unit = {},
    userScrollEnabled: Boolean = true,
    startPomodoro: (Schedules) -> Unit = {},
    endPomodoro: () -> Unit = {},
    pomodoroState: PomodoroState = PomodoroState(),
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            Column {
                SnackbarHost(snackbarHostState)
                Spacer(Modifier.height(MaterialTheme.sizing.floatingBar))
            }
        },
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
            if(currentWindowAdaptiveInfoV2()
                .windowSizeClass
                .isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)
            ){
                NewScheduleFAB(
                    uiState.userPreferences.firstEntry,
                    addNewSchedule,
                    openEditScreen,
                    setAsExistingUser
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            state = lazyListState,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = MaterialTheme.sizing.floatingBar),
            userScrollEnabled = userScrollEnabled
        ) {
            item {
                AccessibilityPermissionCard(
                    recheckPermission = recheckPermission,
                    showCard = uiState.showAccessibilityPermissionRationale,
                    setAsExistingUser = setAsExistingUser
                )
            }
            item { EnableAllSchedulesCard(uiState.schedulesList, enableAllSchedules) }
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
                        openPomodoroScreen = openPomodoroScreen,
                        startPomodoro = { startPomodoro(schedule) },
                        endPomodoro = endPomodoro,
                        pomodoroState = pomodoroState
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ZenwellTheme {
        HomeScreen(
            uiState = HomeUiState(
                showAccessibilityPermissionRationale = true,
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