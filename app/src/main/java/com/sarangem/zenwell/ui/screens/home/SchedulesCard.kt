/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.home

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sarangem.zenwell.R
import com.sarangem.zenwell.domain.time.is24Hour
import com.sarangem.zenwell.domain.time.minutesToString
import com.sarangem.zenwell.model.Schedules
import com.sarangem.zenwell.model.runsAllDay
import com.sarangem.zenwell.service.pomodoro.PomodoroState
import com.sarangem.zenwell.service.pomodoro.PomodoroStatus
import com.sarangem.zenwell.ui.theme.sizing

@Composable
fun SchedulesCard(
    modifier: Modifier = Modifier,
    schedule: Schedules,
    isClicked: Boolean = false,
    startPomodoro: () -> Unit = {},
    pomodoroState: PomodoroState = PomodoroState(),
    openEditScreen: () -> Unit = {},
    openPomodoroScreen: () -> Unit = {},
) {
    val isPomodoroRunning = pomodoroState.schedule.id == schedule.id &&
            pomodoroState.status != PomodoroStatus.IDLE
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    Column(
        modifier = modifier.clip(MaterialTheme.shapes.medium)
    ) {
        AnimatedVisibility(isPomodoroRunning) { PomodoroProgressBar(pomodoroState) }

        Row(
            Modifier
                .animateContentSize()
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
        ) {
            ScheduleDetails(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(2f),
                schedule, isClicked, openEditScreen
            )

            if(schedule.isPomodoro){
                AnimatedContent(
                    isPomodoroRunning,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                ) {
                    if (it) {
                        PomodoroTimerControls(pomodoroState, openPomodoroScreen)
                    } else {
                        PomodoroStartButton(
                            startPomodoro = {
                                startPomodoro()
                                openPomodoroScreen()
                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PomodoroProgressBar(state: PomodoroState) = with(state) {
    val animatedProgress by key(isWorkTime){
        animateFloatAsState(
            targetValue = progress,
            animationSpec = tween(durationMillis = 200, easing = LinearEasing),
        )
    }
    LinearProgressIndicator(
        modifier = Modifier
            .height(MaterialTheme.sizing.medium)
            .fillMaxWidth(),
        progress = { animatedProgress },
        color = if (isWorkTime) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
        trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        strokeCap = StrokeCap.Square,
        gapSize = 0.dp,
        drawStopIndicator = {}
    )
}

@Composable
fun ScheduleDetails(
    modifier: Modifier = Modifier,
    schedule: Schedules,
    isClicked: Boolean,
    openEditScreen: () -> Unit = {}
) {
    val context = LocalContext.current
    val is24Hour = remember { is24Hour(context) }
    val tint = if (schedule.isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline

    Column(
        modifier
            .background(
                if (isClicked) {
                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.25f)
                } else {
                    MaterialTheme.colorScheme.surfaceContainer
                }
            )
            .clickable(
                onClick = openEditScreen,
                onClickLabel = stringResource(R.string.edit_this_schedule)
            )
            .padding(MaterialTheme.sizing.medium)
    ) {
        Text(
            text = schedule.title,
            style = MaterialTheme.typography.headlineSmall,
            color = tint,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.size(MaterialTheme.sizing.extraSmall))

        if (schedule.isPomodoro) {
            Text(
                text = stringResource(
                    R.string.pomodoro_time,
                    schedule.pomodoroWorkTimeInMinutes,
                    schedule.pomodoroRestTimeInMinutes
                ),
                style = MaterialTheme.typography.labelLarge,
                color = tint
            )
        } else {
            Text(
                text = if (schedule.runsAllDay()) {
                    stringResource(R.string.all_day)
                } else {
                    stringResource(
                        R.string.schedule_time,
                        minutesToString(schedule.startTimeInMinutes, is24Hour),
                        minutesToString(schedule.endTimeInMinutes, is24Hour)
                    )
                },
                style = MaterialTheme.typography.labelLarge,
                color = tint,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun PomodoroTimerControls(
    state: PomodoroState,
    openPomodoroScreen: () -> Unit = {}
) = with(state) {
    Column(
        modifier = Modifier
            .background(
                if (isWorkTime) {
                    MaterialTheme.colorScheme.tertiaryContainer
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                }
            )
            .clickable(
                onClick = openPomodoroScreen,
                onClickLabel = stringResource(R.string.open_pomodoro_timer)
            )
            .padding(MaterialTheme.sizing.small),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painterResource(if (isWorkTime) R.drawable.filled_work else R.drawable.outlined_local_cafe),
            contentDescription = stringResource(if (isWorkTime) R.string.work_segment else R.string.rest_segment),
            modifier = Modifier.size(MaterialTheme.sizing.large)
        )
        Text(
            text = formattedTime,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            maxLines = 1
        )
        AnimatedVisibility(status == PomodoroStatus.PAUSED) {
            Icon(
                painter = painterResource(R.drawable.filled_pause),
                contentDescription = stringResource(R.string.currently_paused)
            )
        }
    }
}

@Composable
fun PomodoroStartButton(startPomodoro: () -> Unit) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .clickable(onClick = startPomodoro)
            .padding(MaterialTheme.sizing.small),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painterResource(R.drawable.filled_navigation),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(MaterialTheme.sizing.image)
        )
        Text(
            stringResource(R.string.start),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}