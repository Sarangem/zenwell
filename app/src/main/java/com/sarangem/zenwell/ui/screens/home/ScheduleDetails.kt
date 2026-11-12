/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.sarangem.zenwell.R
import com.sarangem.zenwell.domain.time.is24Hour
import com.sarangem.zenwell.domain.time.minutesToString
import com.sarangem.zenwell.model.Schedules
import com.sarangem.zenwell.model.runsAllDay
import com.sarangem.zenwell.ui.theme.sizing

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
                    MaterialTheme.colorScheme.surfaceContainerHigh
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