package com.sarangem.zenwell.service.ui.screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.window.core.layout.WindowSizeClass
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.database.tables.Schedules
import com.sarangem.zenwell.service.PomodoroWindow
import com.sarangem.zenwell.service.ui.APP_BLOCKED
import com.sarangem.zenwell.service.ui.EXPANDED_WIDTH
import com.sarangem.zenwell.service.ui.MEDIUM_WIDTH
import com.sarangem.zenwell.service.ui.PREVIEW_HEIGHT
import com.sarangem.zenwell.service.ui.TimerMessageCard
import com.sarangem.zenwell.ui.commonui.TimerBox
import com.sarangem.zenwell.ui.theme.Orbitron
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import kotlinx.coroutines.delay

@Composable
fun PomodoroBlockScreen(
    modifier: Modifier = Modifier,
    message: String,
    pomodoroWindow: PomodoroWindow
) {
    val timerCard: @Composable (Modifier) -> Unit = { modifier ->
        PomodoroTimerCard(
            modifier = modifier,
            pomodoroWindow = pomodoroWindow
        )
    }
    val messageCard: @Composable (Modifier) -> Unit = { modifier ->
        TimerMessageCard(
            modifier = modifier,
            message = message,
        )
    }

    if (currentWindowAdaptiveInfo()
            .windowSizeClass
            .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    ) {
        Row(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(dimensionResource(R.dimen.padding_small)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            timerCard(Modifier.weight(0.5f))
            messageCard(Modifier.weight(0.5f))
        }
    } else {
        Column(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(dimensionResource(R.dimen.padding_small)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            timerCard(Modifier.weight(0.5f))
            messageCard(Modifier.weight(0.5f))
        }
    }
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


@Preview(showBackground = true, heightDp = PREVIEW_HEIGHT, widthDp = MEDIUM_WIDTH)
@Composable
fun PomodoroBlockScreenColumnPreviewLight() {
    ZenwellTheme(darkTheme = false) {
        PomodoroBlockScreen(
            Modifier,
            APP_BLOCKED,
            PomodoroWindow(Schedules(isPomodoro = true), context = LocalContext.current)
        )
    }
}

@Preview(heightDp = PREVIEW_HEIGHT, widthDp = MEDIUM_WIDTH)
@Composable
fun PomodoroBlockScreenColumnPreviewDark() {
    ZenwellTheme(darkTheme = true) {
        PomodoroBlockScreen(
            Modifier,
            APP_BLOCKED,
            PomodoroWindow(Schedules(isPomodoro = true), context = LocalContext.current)
        )
    }
}

@Preview(showBackground = true, heightDp = PREVIEW_HEIGHT, widthDp = EXPANDED_WIDTH)
@Composable
fun PomodoroBlockScreenRowPreviewLight() {
    ZenwellTheme(darkTheme = false) {
        PomodoroBlockScreen(
            Modifier,
            APP_BLOCKED,
            PomodoroWindow(Schedules(isPomodoro = true), context = LocalContext.current)
        )
    }
}

@Preview(heightDp = PREVIEW_HEIGHT, widthDp = EXPANDED_WIDTH)
@Composable
fun PomodoroBlockScreenRowPreviewDark() {
    ZenwellTheme(darkTheme = true) {
        PomodoroBlockScreen(
            Modifier,
            APP_BLOCKED,
            PomodoroWindow(Schedules(isPomodoro = true), context = LocalContext.current)
        )
    }
}
