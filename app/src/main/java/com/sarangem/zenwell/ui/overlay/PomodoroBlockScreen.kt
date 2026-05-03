/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.overlay

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.overlay.common.APP_BLOCKED
import com.sarangem.zenwell.ui.overlay.common.OverlayScaffold
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PomodoroBlockScreen(
    modifier: Modifier = Modifier,
    message: String,
    getElapsedTimeInSeconds: () -> Long,
    segmentTime: Int,
    getFormattedTime: () -> String,
    showExit: Boolean = true,
    onExit: () -> Unit = {}
) {
    var elapsedTime by rememberSaveable { mutableFloatStateOf(getElapsedTimeInSeconds().toFloat()) }
    var formattedTime by rememberSaveable { mutableStateOf(getFormattedTime()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(500L)
            elapsedTime = getElapsedTimeInSeconds().toFloat()
            formattedTime = getFormattedTime()
        }
    }

    OverlayScaffold(
        mainPaneRowWeight = 0.5f,
        mainPaneColumnWeight = 0.5f,
        message = message,
        modifier = modifier.fillMaxSize(),
        mainPane = { modifier ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier.aspectRatio(1f).fillMaxSize()
            ) {
                val animatedProgress by animateFloatAsState(
                    targetValue = (segmentTime - elapsedTime) / segmentTime,
                    animationSpec = tween(durationMillis = 1000, easing = CubicBezierEasing(0.0f, 0.0f, 1.0f, 1.0f)),
                )
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize(),
                    progress = { animatedProgress },
                    strokeWidth = dimensionResource(R.dimen.padding_medium),
                )
                Box(
                    modifier = Modifier.fillMaxSize(0.8f),
                    contentAlignment = Alignment.Center
                ) {
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
        },
        showExit = showExit,
        onExit = onExit
    )
}

@Preview(showBackground = true)
@Composable
fun PomodoroBlockScreenPreview() {
    var time by remember { mutableLongStateOf(60L) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            time--
        }
    }
    ZenwellTheme(darkTheme = false) {
        PomodoroBlockScreen(
            message = APP_BLOCKED,
            getElapsedTimeInSeconds = { time },
            segmentTime = 60,
            getFormattedTime = { "%02d:%02d".format(time / 60, time % 60) }
        )
    }
}