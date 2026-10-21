/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.sarangem.zenwell.R
import com.sarangem.zenwell.domain.time.is24Hour
import com.sarangem.zenwell.domain.time.minutesToString
import com.sarangem.zenwell.model.Schedules
import com.sarangem.zenwell.ui.theme.sizing

@Composable
fun PomodoroStartButton(
    schedule: Schedules,
    tint: Color,
    startPomodoro: () -> Unit = {},
    isPomodoroRunning: Boolean = false,
) {
    val context = LocalContext.current
    val is24Hour = remember { is24Hour(context) }
    if (!schedule.isPomodoro) {
        Text(
            text = stringResource(
                R.string.schedule_time,
                minutesToString(schedule.startTimeInMinutes, is24Hour),
                minutesToString(schedule.endTimeInMinutes, is24Hour)
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