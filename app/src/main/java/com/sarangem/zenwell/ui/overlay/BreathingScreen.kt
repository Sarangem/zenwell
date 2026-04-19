/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.overlay

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.graphics.shapes.Morph
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.overlay.common.APP_BLOCKED
import com.sarangem.zenwell.ui.overlay.common.MorphPolygonShape
import com.sarangem.zenwell.ui.overlay.common.OverlayScaffold
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import kotlinx.coroutines.delay

@Composable
fun BreathingScreen(
    modifier: Modifier = Modifier,
    message: String = APP_BLOCKED,
    breathingCycleDuration: Int = 10,
    breathingCycleNumber: Int = 2,
    requireManualUnlock: Boolean = true,
    onTimerEnd: () -> Unit = {},
) {
    var showOpen by remember { mutableStateOf(false) }

    OverlayScaffold(
        mainPane = { modifier ->
            BreathingCard(
                modifier = modifier,
                showOpenButton = { showOpen = true },
                onTimerEnd,
                breathingCycleDuration,
                breathingCycleNumber,
                requireManualUnlock
            )
        },
        mainPaneRowWeight = 0.6f,
        mainPaneColumnWeight = 0.7f,
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
    showOpenButton: () -> Unit,
    onTimerEnd: () -> Unit,
    breathingCycleDuration: Int,
    breathingCycleNumber: Int,
    requireManualUnlock: Boolean,
) {
    val yellow500 = Color(0xFFFFC107)
    val purple300 = Color(0xFFBA68C8)
    val purple500 = Color(0xFF9C27B0)

    var inhale by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val halfDuration = breathingCycleDuration * 500L
        repeat(breathingCycleNumber * 2) {
            inhale = !inhale
            delay(halfDuration)
        }
        if (!requireManualUnlock) onTimerEnd()
        showOpenButton()
    }

    val animatedShapeProgress by animateFloatAsState(
        targetValue = if (inhale) 0.6f else 1f,
        animationSpec = tween(easing = LinearEasing, durationMillis = breathingCycleDuration * 500)
    )
    val animatedMorphProgress by animateFloatAsState(
        targetValue = if (inhale) 1f else 0f,
        animationSpec = spring(
            stiffness = Spring.StiffnessVeryLow
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_small))
        ) {
            Box(
                modifier = Modifier
                    .weight(6f)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(animatedShapeProgress)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(yellow500, purple300.copy(alpha = 0.7f)),
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
                        .background(purple500),
                )
            }
            AnimatedContent(
                targetState = inhale,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(align = Alignment.BottomCenter)
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
}


@Preview(showBackground = true)
@Composable
fun BreathingScreenPreviewLight() {
    ZenwellTheme {
        BreathingScreen()
    }
}

@Preview
@Composable
fun BreathingScreenPreviewDark() {
    ZenwellTheme(darkTheme = true) {
        BreathingScreen()
    }
}