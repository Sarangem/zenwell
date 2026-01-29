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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.sarangem.zenwell.R
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.ui.overlay.common.APP_BLOCKED
import com.sarangem.zenwell.ui.overlay.common.EXPANDED_WIDTH
import com.sarangem.zenwell.ui.overlay.common.MEDIUM_WIDTH
import com.sarangem.zenwell.ui.overlay.common.OverlayScaffold
import com.sarangem.zenwell.ui.overlay.common.PREVIEW_HEIGHT
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WaitScreen(
    modifier: Modifier = Modifier,
    schedule: Schedules,
    onTimerEnd: () -> Unit = {},
) {
    var showOpen by remember { mutableStateOf(false) }
    var time by remember { mutableIntStateOf(schedule.timerDurationInSeconds) }
    LaunchedEffect(time) {
        if (time > 0) {
            delay(1000L)
            time--
        } else {
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
                    targetValue = time / schedule.timerDurationInSeconds.toFloat(),
                    animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
                )
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize(),
                    progress = { animatedProgress },
                    strokeWidth = dimensionResource(R.dimen.padding_medium),
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
                        modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                    )
                }
            }
        },
        mainPaneRowWeight = 0.6f,
        showOpenDialog = schedule.requireManualUnlock,
        showOpen = showOpen,
        message = schedule.message,
        onTimerEnd = onTimerEnd,
        modifier = modifier.fillMaxSize()
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true, heightDp = PREVIEW_HEIGHT, widthDp = MEDIUM_WIDTH)
@Composable
fun WaitScreenColumnPreviewLight() {
    ZenwellTheme(darkTheme = false) {
        WaitScreen(schedule = Schedules(message=APP_BLOCKED))
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(heightDp = PREVIEW_HEIGHT, widthDp = MEDIUM_WIDTH)
@Composable
fun WaitScreenColumnPreviewDark() {
    ZenwellTheme(darkTheme = true) {
        WaitScreen(schedule = Schedules(message=APP_BLOCKED))
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true, heightDp = PREVIEW_HEIGHT, widthDp = EXPANDED_WIDTH)
@Composable
fun WaitScreenRowPreviewLight() {
    ZenwellTheme(darkTheme = false) {
        WaitScreen(schedule = Schedules(message=APP_BLOCKED))
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(heightDp = PREVIEW_HEIGHT, widthDp = EXPANDED_WIDTH)
@Composable
fun WaitScreenRowPreviewDark() {
    ZenwellTheme(darkTheme = true) {
        WaitScreen(schedule = Schedules(message=APP_BLOCKED))
    }
}
