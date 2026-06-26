/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.overlay

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.sarangem.zenwell.ui.overlay.common.APP_BLOCKED
import com.sarangem.zenwell.ui.overlay.common.OverlayScaffold
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import com.sarangem.zenwell.ui.theme.sizing
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TimerScreen(
    modifier: Modifier = Modifier,
    message: String = APP_BLOCKED,
    timerDurationInSeconds: Int = 10,
    requireManualUnlock: Boolean = true,
    onTimerEnd: () -> Unit = {},
    showExit: Boolean = true,
    onExit: () -> Unit = {}
) {
    var showOpen by remember { mutableStateOf(false) }
    var time by remember { mutableIntStateOf(timerDurationInSeconds) }
    LaunchedEffect(time) {
        if (time > 0) {
            delay(1.seconds)
            time--
        } else {
            if(!requireManualUnlock) onTimerEnd()
            showOpen = true
        }
    }

    OverlayScaffold(
        mainPane = { modifier ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier.aspectRatio(1f).fillMaxSize()
            ) {
                val animatedProgress by animateFloatAsState(
                    targetValue = time / timerDurationInSeconds.toFloat(),
                    animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
                )
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize(),
                    progress = { animatedProgress },
                    strokeWidth = MaterialTheme.sizing.medium,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Box(
                    modifier = Modifier.fillMaxSize(0.8f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = time.toString(),
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                        autoSize = TextAutoSize.StepBased(),
                        modifier = Modifier.padding(MaterialTheme.sizing.small)
                    )
                }
            }
        },
        mainPaneRowWeight = 0.6f,
        showOpen = showOpen,
        message = message,
        onTimerEnd = onTimerEnd,
        showExit = showExit,
        onExit = onExit,
        modifier = modifier.fillMaxSize()
    )
}

@Preview(showBackground = true)
@Composable
fun TimerScreenPreview() {
    ZenwellTheme(darkTheme = false) {
        TimerScreen()
    }
}