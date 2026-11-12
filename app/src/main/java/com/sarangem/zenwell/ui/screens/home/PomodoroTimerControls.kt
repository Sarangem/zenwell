/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sarangem.zenwell.R
import com.sarangem.zenwell.service.pomodoro.PomodoroState
import com.sarangem.zenwell.service.pomodoro.PomodoroStatus
import com.sarangem.zenwell.ui.theme.sizing

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
            modifier = Modifier.size(20.dp)
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