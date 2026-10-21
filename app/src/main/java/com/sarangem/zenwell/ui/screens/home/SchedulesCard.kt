/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.home

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sarangem.zenwell.R
import com.sarangem.zenwell.model.Schedules
import com.sarangem.zenwell.service.pomodoro.PomodoroState
import com.sarangem.zenwell.service.pomodoro.PomodoroStatus
import com.sarangem.zenwell.ui.theme.sizing

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
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

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
                    PomodoroStartButton(
                        schedule = schedule,
                        tint = tint,
                        startPomodoro = {
                            startPomodoro()
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        },
                        isPomodoroRunning = isPomodoroRunning
                    )
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