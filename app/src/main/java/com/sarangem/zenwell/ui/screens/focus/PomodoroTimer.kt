/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.focus

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Work
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.sarangem.zenwell.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PomodoroTimer(
    modifier: Modifier = Modifier,
    progress:  Float,
    formattedTime: String,
    isWork: Boolean
){
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.aspectRatio(1f).fillMaxSize()
    ) {
        CircularWavyProgressIndicator(
            modifier = Modifier.fillMaxSize(),
            progress = { progress },
            stroke = Stroke(
                width = with(LocalDensity.current) {
                    dimensionResource(R.dimen.padding_medium).toPx()
                },
                cap = StrokeCap.Round
            ),
            trackStroke = Stroke(
                width = with(LocalDensity.current) {
                    dimensionResource(R.dimen.padding_medium).toPx()
                },
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
            color = if(isWork) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
            wavelength = dimensionResource(R.dimen.pomodoro_rest_time_wavelength)
        )
        Box(
            modifier = Modifier.fillMaxSize(0.8f),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Icon(
                    imageVector = if (isWork) Icons.Filled.Work else Icons.Filled.LocalCafe,
                    contentDescription = stringResource(if (isWork) R.string.work_time else R.string.rest_time),
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_large))
                        .size(dimensionResource(R.dimen.image_size))
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = formattedTime,
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    autoSize = TextAutoSize.StepBased()
                )
            }
        }
    }
}