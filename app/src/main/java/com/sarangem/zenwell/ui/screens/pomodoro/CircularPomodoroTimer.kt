/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.pomodoro

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.sizing

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CircularPomodoroTimer(
    modifier: Modifier = Modifier,
    progress: Float,
    formattedTime: String,
    isWork: Boolean
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.aspectRatio(1f, matchHeightConstraintsFirst = false)
    ) {
        CircularWavyProgressIndicator(
            modifier = Modifier.fillMaxSize(),
            progress = { progress },
            stroke = Stroke(
                width = with(LocalDensity.current) { MaterialTheme.sizing.medium.toPx() },
                cap = StrokeCap.Round
            ),
            trackStroke = Stroke(
                width = with(LocalDensity.current) { MaterialTheme.sizing.medium.toPx() },
                cap = StrokeCap.Round
            ),
            amplitude = { progress ->
                val lowProgress = progress <= 0.1f || progress >= 0.95f
                if (isWork || lowProgress) {
                    0f
                } else {
                    1f
                }
            },
            color = if (isWork) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
            wavelength = 40.dp
        )
        Box(
            modifier = Modifier.fillMaxSize(0.8f),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(if (isWork) R.drawable.filled_work else R.drawable.outlined_local_cafe),
                    contentDescription = stringResource(if (isWork) R.string.work_time else R.string.rest_time),
                    modifier = Modifier
                        .weight(1f, false)
                        .size(MaterialTheme.sizing.image)
                )
                Text(
                    text = formattedTime,
                    modifier = Modifier.padding(MaterialTheme.sizing.small),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    autoSize = TextAutoSize.StepBased()
                )
            }
        }
    }
}