/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.pomodoro

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_MEDIUM_LOWER_BOUND
import com.sarangem.zenwell.R
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.ui.screens.AppViewModelProvider
import com.sarangem.zenwell.ui.theme.ZenwellTheme

@Composable
fun FocusScreen(
    modifier: Modifier = Modifier,
    schedule: Schedules,
    goBack: () -> Unit = {}
) {
    val viewModel: PomodoroViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(schedule.id) {
        viewModel.startObserving(schedule)
    }
    FocusScreen(
        modifier,
        goBack,
        uiState,
        viewModel::onEnd,
        viewModel::onPauseOrResume,
        viewModel::onSkip
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusScreen(
    modifier: Modifier = Modifier,
    goBack: () -> Unit = {},
    uiState: PomodoroUiState,
    onEnd: () -> Unit = {},
    onPauseOrResume: () -> Unit = {},
    onSkip: () -> Unit = {},
) {
    val animatedProgress by key(uiState.isWorkTime){
        animateFloatAsState(
            targetValue = (uiState.segmentTime - uiState.elapsedTime).toFloat() / uiState.segmentTime,
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        )
    }
    Scaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(
                            painterResource(R.drawable.filled_arrow_back),
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                },
                title = {
                    Text(
                        text = uiState.schedule.title,
                        style = MaterialTheme.typography.headlineSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
            )
        },
        floatingActionButton = {
            PomodoroControls(
                isPaused = uiState.isPaused,
                isWorkTime = uiState.isWorkTime,
                showPauseInWorkTime = uiState.schedule.showPauseInWorkTime,
                showSkipInWorkTime = uiState.schedule.showSkipInWorkTime,
                showPauseInRestTime = uiState.schedule.showPauseInRestTime,
                showSkipInRestTime = uiState.schedule.showSkipInRestTime,
                onEnd = {
                    onEnd()
                    goBack()
                },
                onPauseOrResume = onPauseOrResume,
                onSkip = onSkip
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { innerPadding ->
        if (!uiState.isServiceRunning) {
            Box(
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "⚠\uFE0F⚠\uFE0F\n" + stringResource(R.string.accessibility_service_not_started) + "\n⚠\uFE0F⚠\uFE0F",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            AnimatedContent(
                targetState = uiState.isCompleted,
                modifier = Modifier.fillMaxSize()
            ) {
                if (it) {
                    Box(
                        Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "\uD83C\uDF89\uD83C\uDF89\n" + stringResource(R.string.pomodoro_session_completed) + "\n\uD83C\uDF89\uD83C\uDF89",
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    val isCompactHeight = !currentWindowAdaptiveInfo().windowSizeClass.isHeightAtLeastBreakpoint(HEIGHT_DP_MEDIUM_LOWER_BOUND)
                    Column(
                        modifier = Modifier
                            .verticalScroll(
                                state = rememberScrollState(),
                                enabled = isCompactHeight
                            )
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PomodoroProgressPills(
                            totalSessions = uiState.schedule.pomodoroSessionNumber,
                            sessionsLeft = uiState.sessionsLeft,
                            modifier = Modifier
                        )
                        PomodoroTimer(
                            modifier = Modifier
                                .padding(dimensionResource(R.dimen.padding_small))
                                .then(
                                    if (isCompactHeight) {
                                        Modifier.size(dimensionResource(R.dimen.circular_pomodoro_timer_size))
                                    } else {
                                        Modifier.weight(1f)
                                    }
                                ),
                            progress = animatedProgress,
                            formattedTime = uiState.formattedTime,
                            isWork = uiState.isWorkTime
                        )
                        Spacer(Modifier.height(dimensionResource(R.dimen.floating_action_button_height)))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FocusScreenPreview() {
    ZenwellTheme {
        FocusScreen(
            uiState = PomodoroUiState(
                elapsedTime = 600,
                formattedTime = "15:00",
                isServiceRunning = true,
                sessionsLeft = 4,
                schedule = Schedules(title = "Schedule 1")
            )
        )
    }
}

@Preview
@Composable
fun FocusScreenRestPreview() {
    ZenwellTheme(darkTheme = true) {
        FocusScreen(
            uiState = PomodoroUiState(
                elapsedTime = 150,
                formattedTime = "5:00",
                isServiceRunning = true,
                isWorkTime = false,
                isCompleted = true
            )
        )
    }
}