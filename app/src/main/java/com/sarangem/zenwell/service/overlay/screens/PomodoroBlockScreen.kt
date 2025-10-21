package com.sarangem.zenwell.service.overlay.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import com.sarangem.zenwell.R
import com.sarangem.zenwell.service.PomodoroWindow
import com.sarangem.zenwell.service.overlay.common.OverlayScaffold
import com.sarangem.zenwell.ui.common.TimerBox
import com.sarangem.zenwell.ui.theme.Orbitron
import kotlinx.coroutines.delay

@Composable
fun PomodoroBlockScreen(
    modifier: Modifier = Modifier,
    message: String,
    pomodoroWindow: PomodoroWindow
) {
    OverlayScaffold(
        mainPane = { modifier ->
            PomodoroTimerCard(
                modifier = modifier,
                pomodoroWindow = pomodoroWindow
            )
        },
        mainPaneRowWeight = 0.5f,
        mainPaneColumnWeight = 0.5f,
        message = message,
        modifier = modifier.fillMaxSize()
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PomodoroTimerCard(
    modifier: Modifier = Modifier,
    pomodoroWindow: PomodoroWindow,
) {
    var elapsedTime by rememberSaveable { mutableIntStateOf(pomodoroWindow.getElapsedTimeInSeconds()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            elapsedTime = pomodoroWindow.getElapsedTimeInSeconds().coerceAtLeast(0)
        }
    }
    val totalTime = pomodoroWindow.currentSegmentTime / 1000
    val animatedProgress by animateFloatAsState(
        targetValue = (totalTime - elapsedTime).toFloat() / totalTime,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
    )

    TimerBox(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_large)),
        progress = animatedProgress
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
        ) {
            val minutes = elapsedTime / 60
            val seconds = elapsedTime % 60
            Text(
                text = if (minutes < 10) "0$minutes" else minutes.toString(),
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_small))
                    .graphicsLayer(scaleY = 1.5f),
                fontWeight = FontWeight.Bold,
                fontFamily = Orbitron,
                maxLines = 1,
                autoSize = TextAutoSize.StepBased()
            )
            Text(
                text = if (seconds < 10) "0$seconds" else seconds.toString(),
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_small))
                    .graphicsLayer(scaleY = 1.5f),
                fontWeight = FontWeight.Bold,
                fontFamily = Orbitron,
                maxLines = 1,
                autoSize = TextAutoSize.StepBased()
            )
        }
    }
}

/* TODO: Remove AppBlockerService dependency on composable functions and add previews */