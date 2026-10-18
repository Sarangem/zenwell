/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sarangem.zenwell.R
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.service.pomodoro.PomodoroState
import com.sarangem.zenwell.service.pomodoro.PomodoroStatus
import com.sarangem.zenwell.ui.screens.edit.fields.ShowConfirmDialog
import com.sarangem.zenwell.ui.theme.sizing
import com.sarangem.zenwell.utils.minutesToString

@Composable
fun SchedulesCard(
    modifier: Modifier = Modifier,
    schedule: Schedules,
    isClicked: Boolean = false,
    startPomodoro: () -> Unit = {},
    endPomodoro: () -> Unit = {},
    pomodoroState: PomodoroState = PomodoroState(),
    openEditScreen: (Schedules) -> Unit = {},
    openPomodoroScreen: (Schedules) -> Unit = {},
) {
    val tint = if (schedule.isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
    val cardColor = if (isClicked) {
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.25f),
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    } else CardDefaults.cardColors()
    val isPomodoroRunning = pomodoroState.schedule.id == schedule.id &&
        pomodoroState.status != PomodoroStatus.IDLE

    Card(
        colors = cardColor,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(MaterialTheme.sizing.medium)) {
            Row(
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = schedule.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = tint,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.size(MaterialTheme.sizing.extraSmall))
                    PomodoroStartButton(schedule, tint, startPomodoro, isPomodoroRunning)
                }
                IconButton(
                    onClick = { openEditScreen(schedule) },
                    colors = IconButtonDefaults.iconButtonColors(MaterialTheme.colorScheme.surfaceDim),
                ) {
                    Icon(
                        painterResource(R.drawable.outlined_edit),
                        contentDescription = stringResource(R.string.edit_this_schedule),
                        tint = tint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            AnimatedVisibility(isPomodoroRunning) {
                Spacer(modifier = Modifier.size(MaterialTheme.sizing.small))
                PomodoroTimerControls(
                    state = pomodoroState,
                    endPomodoro = endPomodoro,
                    openPomodoroScreen = { openPomodoroScreen(schedule) }
                )
            }
        }
    }
}

@Composable
fun PomodoroStartButton(
    schedule: Schedules,
    tint: Color,
    startPomodoro: () -> Unit = {},
    isPomodoroRunning: Boolean = false,
) {
    if (!schedule.isPomodoro) {
        Text(
            text = stringResource(
                R.string.schedule_time,
                minutesToString(schedule.startTimeInMinutes),
                minutesToString(schedule.endTimeInMinutes)
            ),
            style = MaterialTheme.typography.labelLarge,
            color = tint,
            fontWeight = FontWeight.Bold,
        )
    } else {
        AnimatedContent(
            targetState = isPomodoroRunning,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
        ) { running ->
            if (running) {
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
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(MaterialTheme.sizing.small))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable(onClick = startPomodoro)
                ) {
                    Row(Modifier.padding(MaterialTheme.sizing.extraSmall)) {
                        Icon(
                            painterResource(R.drawable.filled_navigation),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .size(MaterialTheme.sizing.medium)
                                .align(Alignment.CenterVertically)
                        )
                        Text(
                            text = stringResource(
                                R.string.pomodoro_time_with_start,
                                schedule.pomodoroWorkTimeInMinutes,
                                schedule.pomodoroRestTimeInMinutes
                            ),
                            style = MaterialTheme.typography.labelLargeEmphasized,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PomodoroTimerControls(
    modifier: Modifier = Modifier,
    state: PomodoroState,
    endPomodoro: () -> Unit = {},
    openPomodoroScreen: () -> Unit = {},
) {
    val formattedTime = state.formattedTime
    val isWorkTime = state.isWorkTime
    val isPaused = state.status == PomodoroStatus.PAUSED

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceDim),
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier
                .weight(0.75f)
                .fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = MaterialTheme.sizing.medium,
                        vertical = MaterialTheme.sizing.small
                    )
            ) {
                Column(Modifier.align(Alignment.TopEnd)) {
                    Icon(
                        painterResource(if (isWorkTime) R.drawable.filled_work else R.drawable.outlined_local_cafe),
                        contentDescription = stringResource(if (isWorkTime) R.string.work_time else R.string.rest_time),
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(MaterialTheme.sizing.large)
                    )
                    if(isPaused){
                        Icon(
                            painterResource(R.drawable.filled_pause),
                            contentDescription = stringResource(R.string.pause),
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(MaterialTheme.sizing.large)
                        )
                    }
                }
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.displayMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .graphicsLayer(scaleY = 1.5f)
                        .align(Alignment.Center)
                )
            }
        }
        Spacer(modifier = Modifier.size(MaterialTheme.sizing.extraSmall))
        Column(
            Modifier
                .weight(0.25f)
                .fillMaxHeight()
        ) {
            var isStopClicked by remember { mutableStateOf(false) }
            if(isStopClicked){
                ShowConfirmDialog(
                    icon = R.drawable.filled_bold_close,
                    title = stringResource(R.string.end),
                    description = stringResource(R.string.do_you_want_to_end_this_session),
                    onConfirm = {
                        endPomodoro()
                        isStopClicked = false
                    },
                    onDismiss = { isStopClicked = false }
                )
            }

            Button(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(MaterialTheme.sizing.extraSmall),
                onClick = {
                    isStopClicked = true
                },
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(MaterialTheme.sizing.medium)
            ) {
                Icon(
                    painterResource(R.drawable.filled_bold_close),
                    contentDescription = stringResource(R.string.end),
                    tint = MaterialTheme.colorScheme.onError,
                )
            }
            Spacer(modifier = Modifier.height(MaterialTheme.sizing.extraSmall))
            Button(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(MaterialTheme.sizing.extraSmall),
                onClick = openPomodoroScreen,
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.inversePrimary),
                shape = RoundedCornerShape(MaterialTheme.sizing.medium)
            ) {
                Icon(
                    painterResource(R.drawable.filled_zoom_out_map),
                    contentDescription = stringResource(R.string.enter_focus_mode),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}