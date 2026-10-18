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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sarangem.zenwell.service.pomodoro.PomodoroManager
import com.sarangem.zenwell.ui.overlay.common.APP_BLOCKED
import com.sarangem.zenwell.ui.overlay.common.OverlayScaffold
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import com.sarangem.zenwell.ui.theme.sizing

@Composable
fun PomodoroBlockScreen(
    modifier: Modifier = Modifier,
    message: String,
    pomodoroManager: PomodoroManager,
    showExit: Boolean = true,
    onExit: () -> Unit = {}
) {
    val uiState by pomodoroManager.state.collectAsStateWithLifecycle()
    PomodoroBlockScreen(
        modifier = modifier,
        message = message,
        progress = uiState.progress,
        formattedTime = uiState.formattedTime,
        showExit = showExit,
        onExit = onExit
    )
}

@Composable
fun PomodoroBlockScreen(
    modifier: Modifier = Modifier,
    message: String,
    progress: Float,
    formattedTime: String,
    showExit: Boolean = true,
    onExit: () -> Unit = {}
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = 200,
            easing = LinearEasing
        )
    )
    OverlayScaffold(
        mainPaneRowWeight = 0.5f,
        mainPaneColumnWeight = 0.5f,
        message = message,
        modifier = modifier.fillMaxSize(),
        mainPane = { paneModifier ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = paneModifier
                    .aspectRatio(1f)
                    .fillMaxSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize(),
                    progress = { animatedProgress },
                    strokeWidth = MaterialTheme.sizing.medium,
                )
                Box(
                    modifier = Modifier.fillMaxSize(0.8f),
                    contentAlignment = Alignment.Center
                ) {
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
        },
        showExit = showExit,
        onExit = onExit
    )
}

@Preview(showBackground = true)
@Composable
private fun PomodoroBlockScreenPreview() {
    ZenwellTheme(darkTheme = false) {
        PomodoroBlockScreen(
            message = APP_BLOCKED,
            progress = 0.65f,
            formattedTime = "18:45"
        )
    }
}