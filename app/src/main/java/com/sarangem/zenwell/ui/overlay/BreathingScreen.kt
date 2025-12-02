package com.sarangem.zenwell.ui.overlay

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes.Companion.Gem
import androidx.compose.material3.MaterialShapes.Companion.Sunny
import androidx.compose.material3.MaterialShapes.Companion.VerySunny
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.graphics.shapes.Morph
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.overlay.common.APP_BLOCKED
import com.sarangem.zenwell.ui.overlay.common.EXPANDED_WIDTH
import com.sarangem.zenwell.ui.overlay.common.MEDIUM_WIDTH
import com.sarangem.zenwell.ui.overlay.common.MorphPolygonShape
import com.sarangem.zenwell.ui.overlay.common.OverlayScaffold
import com.sarangem.zenwell.ui.overlay.common.PREVIEW_HEIGHT
import com.sarangem.zenwell.ui.theme.Purple2
import com.sarangem.zenwell.ui.theme.Purple3
import com.sarangem.zenwell.ui.theme.Yellow3
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import kotlinx.coroutines.delay

@Composable
fun BreathingScreen(
    modifier: Modifier = Modifier,
    onTimerEnd: () -> Unit = {},
    breathingCycleDuration: Int,
    breathingCycleNumber: Int,
    showOpenDialog: Boolean,
    message: String
) {
    var showOpen by remember { mutableStateOf(false) }

    OverlayScaffold(
        mainPane = { modifier ->
            BreathingCard(
                modifier = modifier,
                onTimerEnd = { showOpen = true },
                breathingCycleDuration = breathingCycleDuration,
                breathingCycleNumber = breathingCycleNumber
            )
        },
        mainPaneRowWeight = 0.6f,
        mainPaneColumnWeight = 0.7f,
        showOpenDialog = showOpenDialog,
        showOpen = showOpen,
        message = message,
        onTimerEnd = onTimerEnd,
        modifier = modifier.fillMaxSize()
    )
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
            inhale = !inhale
            delay(halfDuration)
        }
        completedBreathingCycle--
    }

    val animatedShapeProgress by animateFloatAsState(
        targetValue = if (inhale) 0.6f else 1f,
        animationSpec = tween(easing = LinearEasing, durationMillis = halfDuration.toInt())
    )
    val animatedMorphProgress by animateFloatAsState(
        targetValue = if (inhale) 1f else 0f,
        animationSpec = spring(
            stiffness = Spring.StiffnessVeryLow
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
            .padding(dimensionResource(R.dimen.padding_small)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .weight(6f)
                .padding(dimensionResource(R.dimen.padding_small)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(animatedShapeProgress)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Yellow3, Purple2.copy(alpha = 0.7f)),
                            center = Offset.Unspecified,
                        ),
                        shape = Sunny.toShape()
                    ),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize(0.07f)
                    .aspectRatio(1f)
                    .clip(
                        MorphPolygonShape(
                            morph = Morph(VerySunny, Gem),
                            percentage = animatedMorphProgress
                        )
                    )
                    .background(Purple3),
            )
        }
        AnimatedContent(
            targetState = inhale,
            modifier = Modifier
                .weight(1f)
                .padding(dimensionResource(R.dimen.padding_small))
                .wrapContentSize(align = Alignment.Center)
        ) { textState ->
            Text(
                text = stringResource(if (textState) R.string.inhale else R.string.exhale),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
fun BreathingScreenColumnPreviewLight() {
    ZenwellTheme(darkTheme = false) {
        BreathingScreen(Modifier, { }, 10, 2, true, APP_BLOCKED)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(heightDp = PREVIEW_HEIGHT, widthDp = MEDIUM_WIDTH)
@Composable
fun BreathingScreenColumnPreviewDark() {
    ZenwellTheme(darkTheme = true) {
        BreathingScreen(Modifier, { }, 10, 2, true, APP_BLOCKED)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true, heightDp = PREVIEW_HEIGHT, widthDp = EXPANDED_WIDTH)
@Composable
fun BreathingScreenRowPreviewLight() {
    ZenwellTheme(darkTheme = false) {
        BreathingScreen(Modifier, { }, 10, 2, true, APP_BLOCKED)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(heightDp = PREVIEW_HEIGHT, widthDp = EXPANDED_WIDTH)
@Composable
fun BreathingScreenRowPreviewDark() {
    ZenwellTheme(darkTheme = true) {
        BreathingScreen(Modifier, { }, 10, 2, true, APP_BLOCKED)
    }
}