/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.pomodoro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sarangem.zenwell.service.pomodoro.PomodoroState
import com.sarangem.zenwell.R
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.service.pomodoro.PomodoroStatus
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import com.sarangem.zenwell.ui.theme.sizing

@Composable
fun PomodoroScreen(
    modifier: Modifier = Modifier,
    goBack: () -> Unit = {}
) {
    val viewModel: PomodoroViewModel = hiltViewModel()
    val manager = viewModel.pomodoroManager
    val uiState by manager.state.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.status) {
        if (uiState.status == PomodoroStatus.IDLE) {
            goBack()
        }
    }
    
    PomodoroScreen(
        modifier = modifier,
        goBack = goBack,
        uiState = uiState,
        onEnd = manager::onPomodoroEnd,
        onPause = manager::onSegmentPause,
        onResume = manager::onSegmentResume,
        onSkip = manager::onSegmentEnd
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(
    modifier: Modifier = Modifier,
    goBack: () -> Unit = {},
    uiState: PomodoroState,
    onEnd: () -> Unit = {},
    onPause: () -> Unit = {},
    onResume: () -> Unit = {},
    onSkip: () -> Unit = {},
) {
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
                isPaused = uiState.status == PomodoroStatus.PAUSED,
                isWorkTime = uiState.isWorkTime,
                showPauseInWorkTime = uiState.schedule.showPauseInWorkTime,
                showSkipInWorkTime = uiState.schedule.showSkipInWorkTime,
                showPauseInRestTime = uiState.schedule.showPauseInRestTime,
                showSkipInRestTime = uiState.schedule.showSkipInRestTime,
                onEnd = onEnd,
                onPause = onPause,
                onResume = onResume,
                onSkip = onSkip
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(MaterialTheme.sizing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProgressPills(
                totalSessions = uiState.schedule.pomodoroSessionNumber,
                sessionsCompleted = uiState.sessionsCompleted
            )
            CircularPomodoroTimer(
                progress = uiState.progress,
                formattedTime = uiState.formattedTime,
                isWork = uiState.isWorkTime,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.height(MaterialTheme.sizing.floatingBar))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FocusScreenPreview() {
    ZenwellTheme {
        PomodoroScreen(
            uiState = PomodoroState(
                remainingMillis = 15 * 60 * 1000L,
                formattedTime = "15:00",
                sessionsCompleted = 3,
                schedule = Schedules(title = "Study pomodoro")
            )
        )
    }
}
