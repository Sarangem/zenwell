package com.sarangem.zenwell.service.blockingscreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes.Companion.Circle
import androidx.compose.material3.MaterialShapes.Companion.Square
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.graphics.shapes.Morph
import com.sarangem.zenwell.APP_BLOCKED
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import kotlinx.coroutines.delay

@Composable
fun BreathingScreen(
    modifier: Modifier = Modifier,
    onTimerEnd: () -> Unit = {},
    breathingCycleDuration: Int,
    breathingCycleNumber: Int,
    showOpenDialog: Boolean,
    message: String,
    height: Float,
    width: Float
) {
    var showOpen by remember { mutableStateOf(false) }
    val timerEnd = {
        if (showOpenDialog) {
            showOpen = true
        } else {
            onTimerEnd()
        }
    }

    Card(modifier = modifier) {

        if ((height < 480 && width > 600) || (height < 900 && width > 800)) {
            BreathingScreenRow(timerEnd, breathingCycleDuration, breathingCycleNumber, message, showOpen, onTimerEnd)
        } else {
            BreathingScreenColumn(timerEnd, breathingCycleDuration, breathingCycleNumber, message, showOpen, onTimerEnd)
        }
    }
}


// -- CARDS -- //

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BreathingCard(
    modifier: Modifier = Modifier,
    onTimerEnd: () -> Unit,
    breathingCycleDuration: Int,
    breathingCycleNumber: Int
) {
    var time by remember { mutableIntStateOf(breathingCycleDuration) }
    var completedBreathingCycle by remember { mutableIntStateOf(breathingCycleNumber) }
    val halfDuration = breathingCycleDuration / 2
    LaunchedEffect(time) {
        if (time >= 0) {
            delay(1000L)
            time--
        } else {
            completedBreathingCycle--
            time = breathingCycleDuration
        }
        if (completedBreathingCycle <= 0){
            onTimerEnd()
        }
    }

    val morph = Morph(Square, Circle)
    val infiniteTransition = rememberInfiniteTransition()
    val animatedProgress = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween( halfDuration * 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
    )

    Box(
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding_small))
            .clip(MorphPolygonShape(morph, animatedProgress.value))
            .background(MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        var text by remember { mutableIntStateOf( R.string.inhale ) }
        text = if (time > halfDuration) {
            R.string.inhale
        } else {
            R.string.exhale
        }
        AnimatedContent(
            targetState = text
        ) { textState ->
            Text(
                text = stringResource(textState),
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.image_size))
                    .fillMaxSize()
                    .wrapContentSize(align = Alignment.Center)
            )
        }
    }
}


// -- IMPLEMENTATIONS -- //

@Composable
fun BreathingScreenColumn(
    onTimerEnd: () -> Unit = {},
    breathingCycleDuration: Int,
    breathingCycleNumber: Int,
    message: String,
    showOpen: Boolean,
    timerEnd: () -> Unit = {}
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.weight(0.1F))

        BreathingCard(
            modifier = Modifier.weight(0.9F),
            onTimerEnd = onTimerEnd,
            breathingCycleNumber = breathingCycleNumber,
            breathingCycleDuration = breathingCycleDuration
        )
        Spacer(Modifier.weight(0.2f))
        MessageCard(
            message = message,
            modifier = Modifier.weight(0.9F),
            showOpenDialog = showOpen,
            onClick = timerEnd
        )

        Spacer(Modifier.weight(0.1F))
    }
}

@Composable
fun BreathingScreenRow(
    onTimerEnd: () -> Unit = {},
    breathingCycleDuration: Int,
    breathingCycleNumber: Int,
    message: String,
    showOpen: Boolean,
    timerEnd: () -> Unit = {}
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(Modifier.weight(0.1F))

        BreathingCard(
            modifier = Modifier.weight(0.9F),
            onTimerEnd = onTimerEnd,
            breathingCycleNumber = breathingCycleNumber,
            breathingCycleDuration = breathingCycleDuration
        )
        Spacer(Modifier.weight(0.2f))
        MessageCard(
            message = message,
            modifier = Modifier.weight(0.9F),
            showOpenDialog = showOpen,
            onClick = timerEnd
        )

        Spacer(Modifier.weight(0.1F))
    }
}


// -- PREVIEW -- //

@Preview(showBackground = true, heightDp = 400, widthDp = 400)
@Composable
fun BreathingScreenCompactPreviewLight() {
    ZenwellTheme(darkTheme = false) {
        BreathingScreen(Modifier, { }, 3, 5, true, APP_BLOCKED, 400f, 400f)
    }
}

@Preview(heightDp = 400, widthDp = 400)
@Composable
fun BreathingScreenCompactPreviewDark() {
    ZenwellTheme(darkTheme = true) {
        BreathingScreen(Modifier, { }, 3, 5, true, APP_BLOCKED, 400f, 400f)
    }
}

@Preview(showBackground = true, heightDp = 700, widthDp = 500)
@Composable
fun BreathingScreenColumnPreviewLight() {
    ZenwellTheme(darkTheme = false) {
        BreathingScreen(Modifier, { }, 3, 5, true, APP_BLOCKED, 700f, 500f)
    }
}

@Preview(heightDp = 700, widthDp = 500)
@Composable
fun BreathingScreenColumnPreviewDark() {
    ZenwellTheme(darkTheme = true) {
        BreathingScreen(Modifier, { }, 3, 5, true, APP_BLOCKED, 700f, 500f)
    }
}

@Preview(showBackground = true, heightDp = 400, widthDp = 700)
@Composable
fun BreathingScreenRowPreviewLight() {
    ZenwellTheme(darkTheme = false) {
        BreathingScreen(Modifier, { }, 3, 5, true, APP_BLOCKED, 400f, 700f)
    }
}

@Preview(heightDp = 400, widthDp = 700)
@Composable
fun BreathingScreenRowPreviewDark() {
    ZenwellTheme(darkTheme = true) {
        BreathingScreen(Modifier, { }, 3, 5, true, APP_BLOCKED, 400f, 700f)
    }
}
