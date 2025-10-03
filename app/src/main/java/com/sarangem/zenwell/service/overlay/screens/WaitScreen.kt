package com.sarangem.zenwell.service.overlay.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.window.core.layout.WindowSizeClass
import com.sarangem.zenwell.R
import com.sarangem.zenwell.service.overlay.common.APP_BLOCKED
import com.sarangem.zenwell.service.overlay.common.EXPANDED_WIDTH
import com.sarangem.zenwell.service.overlay.common.MEDIUM_WIDTH
import com.sarangem.zenwell.service.overlay.common.PREVIEW_HEIGHT
import com.sarangem.zenwell.service.overlay.common.TimerMessageCard
import com.sarangem.zenwell.ui.common.TimerBox
import com.sarangem.zenwell.ui.theme.Orbitron
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WaitScreen(
    modifier: Modifier = Modifier,
    onTimerEnd: () -> Unit = {},
    waitTimeInSeconds: Int,
    showOpenDialog: Boolean,
    message: String
) {
    var showOpen by remember { mutableStateOf(false) }
    var time by remember { mutableIntStateOf(waitTimeInSeconds) }
    LaunchedEffect(time) {
        if (time > 0) {
            delay(1000L)
            time--
        } else {
            showOpen = true
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = time / waitTimeInSeconds.toFloat(),
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
    )
    val timerCard: @Composable (Modifier) -> Unit = { modifier ->
        TimerBox(
            progress = animatedProgress,
            trackColor = MaterialTheme.colorScheme.secondary,
            content = {
                Text(
                    text = time.toString(),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Orbitron,
                    maxLines = 1,
                    autoSize = TextAutoSize.StepBased(),
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_medium))
                        .graphicsLayer(scaleY = 1.5f),
                )
            },
            modifier = modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_large))
        )
    }

    val messageCard: @Composable (Modifier) -> Unit = { modifier ->
        TimerMessageCard(
            modifier = modifier,
            showOpenDialog = showOpenDialog,
            showOpen = showOpen,
            message = message,
            onTimerEnd = onTimerEnd
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
            horizontalArrangement = Arrangement.Center
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
@Preview(showBackground = true, heightDp = PREVIEW_HEIGHT, widthDp = MEDIUM_WIDTH)
@Composable
fun WaitScreenColumnPreviewLight() {
    ZenwellTheme(darkTheme = false) {
        WaitScreen(Modifier, { }, Int.MAX_VALUE, true, APP_BLOCKED)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(heightDp = PREVIEW_HEIGHT, widthDp = MEDIUM_WIDTH)
@Composable
fun WaitScreenColumnPreviewDark() {
    ZenwellTheme(darkTheme = true) {
        WaitScreen(Modifier, { }, 10, true, APP_BLOCKED)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true, heightDp = PREVIEW_HEIGHT, widthDp = EXPANDED_WIDTH)
@Composable
fun WaitScreenRowPreviewLight() {
    ZenwellTheme(darkTheme = false) {
        WaitScreen(Modifier, { }, Int.MAX_VALUE, true, APP_BLOCKED)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(heightDp = PREVIEW_HEIGHT, widthDp = EXPANDED_WIDTH)
@Composable
fun WaitScreenRowPreviewDark() {
    ZenwellTheme(darkTheme = true) {
        WaitScreen(Modifier, { }, 10, true, APP_BLOCKED)
    }
}
