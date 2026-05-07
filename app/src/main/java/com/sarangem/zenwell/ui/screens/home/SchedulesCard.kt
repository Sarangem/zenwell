/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.home

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sarangem.zenwell.R
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.service.PomodoroWindow
import com.sarangem.zenwell.ui.screens.edit.fields.ShowConfirmDialog
import com.sarangem.zenwell.ui.theme.sizing
import com.sarangem.zenwell.utils.minutesToString
import kotlinx.coroutines.delay

@Composable
fun SchedulesCard(
    modifier: Modifier = Modifier,
    schedule: Schedules,
    isClicked: Boolean = false,
    pomodoroWindow: PomodoroWindow?,
    openEditScreen: (Schedules) -> Unit = {},
    openFocusScreen: (Schedules) -> Unit = {},
) {
    val tint =
        if (schedule.isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
    val cardColor = if (isClicked) {
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.25f),
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    } else CardDefaults.cardColors()

    var isPomodoroActive by rememberSaveable {
        mutableStateOf(
            pomodoroWindow?.isActive == true || pomodoroWindow?.isPaused == true
        )
    }

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
                        pomodoroWindow = pomodoroWindow,
                        isPomodoroActive = isPomodoroActive,
                        updatePomodoroActivity = { isPomodoroActive = it }
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
            if (schedule.isActive && schedule.isPomodoro && isPomodoroActive && pomodoroWindow != null) {
                Spacer(modifier = Modifier.size(MaterialTheme.sizing.small))
                PomodoroTimerControls(
                    pomodoroWindow = pomodoroWindow,
                    updatePomodoroActivity = { isPomodoroActive = it },
                    openFocusScreen = { openFocusScreen(schedule) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PomodoroStartButton(
    modifier: Modifier = Modifier,
    schedule: Schedules,
    tint: Color,
    isPomodoroActive: Boolean,
    pomodoroWindow: PomodoroWindow? = null,
    updatePomodoroActivity: (Boolean) -> Unit = {},
) {
    val context = LocalContext.current
    if (schedule.isActive && schedule.isPomodoro && pomodoroWindow != null && !isPomodoroActive) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(MaterialTheme.sizing.small))
                .background(MaterialTheme.colorScheme.primary)
                .clickable(onClick = {
                    pomodoroWindow.onPomodoroStart()
                    updatePomodoroActivity(true)
                })
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
                    text = stringResource(R.string.start),
                    style = MaterialTheme.typography.labelLargeEmphasized,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Spacer(Modifier.width(MaterialTheme.sizing.small))
                Text(
                    text = "(" + schedule.pomodoroWorkTimeInMinutes.toString() + " + " + schedule.pomodoroRestTimeInMinutes.toString() + " " + stringResource(
                        R.string.minutes
                    ) + ")",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    } else if (schedule.isPomodoro) {
        Text(
            text = schedule.pomodoroWorkTimeInMinutes.toString() + " + " + schedule.pomodoroRestTimeInMinutes.toString() + " " + stringResource(
                R.string.minutes
            ),
            style = MaterialTheme.typography.labelLarge,
            color = tint,
            fontWeight = FontWeight.Bold,
        )
    } else {
        Text(
            modifier = modifier,
            text = minutesToString(
                schedule.startTimeInMinutes,
                context
            ) + " " + stringResource(R.string.to) + " " + minutesToString(
                schedule.endTimeInMinutes,
                context
            ),
            style = MaterialTheme.typography.labelLarge,
            color = tint,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun PomodoroTimerControls(
    modifier: Modifier = Modifier,
    pomodoroWindow: PomodoroWindow,
    updatePomodoroActivity: (Boolean) -> Unit = {},
    openFocusScreen: () -> Unit = {},
) {
    var formattedTime by remember { mutableStateOf(pomodoroWindow.getFormattedTime()) }
    var isWorkTime by remember { mutableStateOf(pomodoroWindow.isWorkTime) }
    var isPaused by remember { mutableStateOf(pomodoroWindow.isPaused) }
    LaunchedEffect(Unit) {
        while (pomodoroWindow.isActive || pomodoroWindow.isPaused) {
            delay(500L)
            isWorkTime = pomodoroWindow.isWorkTime
            formattedTime = pomodoroWindow.getFormattedTime()
        }
        updatePomodoroActivity(false)
    }

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
                        pomodoroWindow.onPomodoroEnd()
                        updatePomodoroActivity(false)
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
                onClick = openFocusScreen,
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