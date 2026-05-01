/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.overlay.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes.Companion.ClamShell
import androidx.compose.material3.MaterialShapes.Companion.SoftBoom
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.graphics.shapes.Morph
import com.sarangem.zenwell.R

@Composable
fun MessageCard(
    modifier: Modifier = Modifier,
    state: Boolean,
    message: String,
    morphPolygonShape: MorphPolygonShape,
    falseStateContent: @Composable () -> Unit = {},
    onClick: () -> Unit = {},
) {
    AnimatedContent(
        targetState = state,
        transitionSpec = {
            fadeIn(
                animationSpec = tween(1000)
            ) togetherWith fadeOut(animationSpec = tween(1000))
        },
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding_small))
            .clip(morphPolygonShape)
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxSize()
            .clickable(onClick = {
                onClick()
            }),
        contentAlignment = Alignment.Center,
    ) { state ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (state) {
                falseStateContent()
            } else {
                BasicText(
                    text = message,
                    style = TextStyle(
                        lineBreak = LineBreak.Paragraph,
                        hyphens = Hyphens.Auto,
                        color = MaterialTheme.colorScheme.onPrimary,
                        lineHeight = 1.1.em,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    ),
                    autoSize = TextAutoSize.StepBased(),
                    softWrap = true,
                    maxLines = message.split(" ").size,
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_medium))
                        .fillMaxSize(0.7f)
                        .wrapContentSize(Alignment.Center)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OpenableMessageCard(
    modifier: Modifier = Modifier,
    showOpen: Boolean = false,
    message: String = "",
    onTimerEnd: () -> Unit = {}
) {
    val animatedRotation = animateFloatAsState(
        targetValue = if (showOpen) 1f else 0f,
        animationSpec = tween(1500, easing = FastOutSlowInEasing)
    )
    MessageCard(
        modifier = modifier,
        state = showOpen,
        message = message,
        morphPolygonShape = MorphPolygonShape(
            morph = Morph(ClamShell, SoftBoom),
            percentage = animatedRotation.value
        ),
        falseStateContent = {
            Text(
                text = stringResource(R.string.open),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                lineHeight = 80.sp,
                autoSize = TextAutoSize.StepBased(maxFontSize = 80.sp),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

        },
        onClick = {
            if (showOpen) {
                onTimerEnd()
            }
        },
    )
}