/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.home

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.sarangem.zenwell.R
import com.sarangem.zenwell.service.pomodoro.PomodoroState
import com.sarangem.zenwell.service.pomodoro.PomodoroStatus
import com.sarangem.zenwell.ui.screens.edit.fields.ShowConfirmDialog
import com.sarangem.zenwell.ui.theme.sizing

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