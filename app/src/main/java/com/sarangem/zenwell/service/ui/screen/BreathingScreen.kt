package com.sarangem.zenwell.service.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.TextAutoSize
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.sarangem.zenwell.R
import com.sarangem.zenwell.service.ui.APP_BLOCKED
import com.sarangem.zenwell.service.ui.EXPANDED_WIDTH
import com.sarangem.zenwell.service.ui.MEDIUM_WIDTH
import com.sarangem.zenwell.service.ui.PREVIEW_HEIGHT
import com.sarangem.zenwell.service.ui.TimerMessageCard
import com.sarangem.zenwell.ui.commonui.TimerBox
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import com.sarangem.zenwell.ui.commonui.isExpandedWidth
import kotlinx.coroutines.delay

@Composable
fun BreathingScreen(
    modifier: Modifier = Modifier,
    onTimerEnd: () -> Unit = {},
    breathingCycleDuration: Int,
    breathingCycleNumber: Int,
    showOpenDialog: Boolean,
    message: String,
    width: Float
) {
    var showOpen by remember { mutableStateOf(false) }

    val breathingCard: @Composable (Modifier) -> Unit = { modifier ->
        BreathingCard(
            modifier = modifier,
            onTimerEnd = { showOpen = true },
            breathingCycleDuration = breathingCycleDuration,
            breathingCycleNumber = breathingCycleNumber
        )
    }
    val messageCard: @Composable (Modifier) -> Unit = { modifier ->
        TimerMessageCard(
            modifier = modifier,
            showOpenDialog = showOpenDialog,
            showOpen = showOpen,
            message = message,
            onTimerEnd = onTimerEnd,
        )
    }

    if (isExpandedWidth(width)) {
        Row(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(dimensionResource(R.dimen.padding_small)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            breathingCard(Modifier.weight(0.6f))
            messageCard(Modifier.weight(0.4f))
        }
    } else {
        Column(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(dimensionResource(R.dimen.padding_small)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            breathingCard(Modifier.weight(0.6f))
            messageCard(Modifier.weight(0.4f))
        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BreathingCard(
    modifier: Modifier = Modifier,
    onTimerEnd: () -> Unit,
    breathingCycleDuration: Int,
    breathingCycleNumber: Int
) {
    var completedBreathingCycle by remember { mutableIntStateOf(breathingCycleNumber) }
    val halfDuration = (breathingCycleDuration / 2) * 1000L
    var inhale by remember { mutableStateOf(false) }

    LaunchedEffect(completedBreathingCycle) {
        if (completedBreathingCycle <= 0) {
            onTimerEnd()
        }
        repeat(2) {
            delay(halfDuration)
            inhale = !inhale
        }
        completedBreathingCycle--
    }

    val animatedShapeProgress by animateDpAsState(
        targetValue = if (inhale) 200.dp else 20.dp,
        animationSpec = spring(
            stiffness = Spring.StiffnessVeryLow
        )
    )

    TimerBox(
        modifier = modifier
            .fillMaxSize()
            .padding(
                top = dimensionResource(R.dimen.padding_small),
                start = dimensionResource(R.dimen.padding_medium),
                end = dimensionResource(R.dimen.padding_medium)
            ),
        progress = 1f,
        strokeWidthInDp = dimensionResource(R.dimen.padding_small),
        cornerRadiusInDp = animatedShapeProgress,
    ) {
        AnimatedContent(
            targetState = inhale
        ) { textState ->
            Text(
                text = stringResource(if (textState) R.string.inhale else R.string.exhale),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                lineHeight = 1.1.em,
                autoSize = TextAutoSize.StepBased(),
                maxLines = 1,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.image_size))
                    .fillMaxSize()
                    .wrapContentSize(align = Alignment.Center)
            )
        }
    }
}


@Preview(showBackground = true, heightDp = PREVIEW_HEIGHT, widthDp = MEDIUM_WIDTH)
@Composable
fun BreathingScreenColumnPreviewLight() {
    ZenwellTheme(darkTheme = false) {
        BreathingScreen(Modifier, { }, 3, 5, true, APP_BLOCKED, MEDIUM_WIDTH.toFloat())
    }
}

@Preview(heightDp = PREVIEW_HEIGHT, widthDp = MEDIUM_WIDTH)
@Composable
fun BreathingScreenColumnPreviewDark() {
    ZenwellTheme(darkTheme = true) {
        BreathingScreen(Modifier, { }, 3, 5, true, APP_BLOCKED, MEDIUM_WIDTH.toFloat())
    }
}

@Preview(showBackground = true, heightDp = PREVIEW_HEIGHT, widthDp = EXPANDED_WIDTH)
@Composable
fun BreathingScreenRowPreviewLight() {
    ZenwellTheme(darkTheme = false) {
        BreathingScreen(Modifier, { }, 3, 5, true, APP_BLOCKED, EXPANDED_WIDTH.toFloat())
    }
}

@Preview(heightDp = PREVIEW_HEIGHT, widthDp = EXPANDED_WIDTH)
@Composable
fun BreathingScreenRowPreviewDark() {
    ZenwellTheme(darkTheme = true) {
        BreathingScreen(Modifier, { }, 3, 5, true, APP_BLOCKED, EXPANDED_WIDTH.toFloat())
    }
}
