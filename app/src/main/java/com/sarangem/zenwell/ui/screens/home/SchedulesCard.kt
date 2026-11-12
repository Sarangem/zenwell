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
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sarangem.zenwell.R
import com.sarangem.zenwell.model.Schedules
import com.sarangem.zenwell.service.pomodoro.PomodoroState
import com.sarangem.zenwell.service.pomodoro.PomodoroStatus
import com.sarangem.zenwell.ui.theme.sizing

private val STROKE_HEIGHT = 24.dp
private val STROKE_WIDTH = 12.dp

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

    Box(modifier) {
        Row(
            Modifier
                .padding(
                    top = if (isPomodoroRunning) STROKE_HEIGHT / 2 else 0.dp
                )
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
        ) {
            ScheduleDetails(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(2f),
                schedule, isClicked, openEditScreen
            )

            if (schedule.isPomodoro) {
                AnimatedContent(
                    isPomodoroRunning,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                ) {
                    if (it) {
                        PomodoroTimerControls(pomodoroState, openPomodoroScreen)
                    } else {
                        PomodoroStartButton(
                            startPomodoro = {
                                startPomodoro()
                                openPomodoroScreen()
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            }
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = isPomodoroRunning,
            modifier = Modifier.align(Alignment.TopStart)
        ) { PomodoroProgressBar(pomodoroState) }
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
    LinearWavyProgressIndicator(
        modifier = Modifier
            .height(STROKE_HEIGHT)
            .fillMaxWidth(),
        progress = { animatedProgress },
        stroke = Stroke(
            width = with(LocalDensity.current) { STROKE_WIDTH.toPx() },
            cap = StrokeCap.Round
        ),
        amplitude = { progress ->
            val lowProgress = progress <= 0.1f || progress >= 0.95f
            if (isWorkTime || lowProgress || status == PomodoroStatus.PAUSED) 0f else 1f
        },
        color = if (isWorkTime) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
        trackColor = Color.Transparent,
        gapSize = 0.dp,
        wavelength = 40.dp
    )
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