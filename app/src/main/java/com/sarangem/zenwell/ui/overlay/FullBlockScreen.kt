/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.overlay

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes.Companion.ClamShell
import androidx.compose.material3.MaterialShapes.Companion.SoftBoom
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.graphics.shapes.Morph
import com.sarangem.zenwell.ui.overlay.common.APP_BLOCKED
import com.sarangem.zenwell.ui.overlay.common.MessageCard
import com.sarangem.zenwell.ui.overlay.common.MorphPolygonShape
import com.sarangem.zenwell.ui.overlay.common.OverlayScaffold
import com.sarangem.zenwell.ui.theme.ZenwellTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FullBlockScreen(
    modifier: Modifier = Modifier,
    message: String = APP_BLOCKED
) {
    var isClicked by remember { mutableStateOf(false) }
    val animatedRotation = animateFloatAsState(
        targetValue = if (isClicked) 1f else 0f,
        animationSpec = tween(2000, easing = FastOutSlowInEasing)
    )

    OverlayScaffold(
        modifier = modifier.fillMaxSize(),
        content = {
            MessageCard(
                state = isClicked,
                onClick = { isClicked = !isClicked },
                message = message,
                morphPolygonShape = MorphPolygonShape(
                    morph = Morph(ClamShell, SoftBoom),
                    percentage = animatedRotation.value
                ),
                falseStateContent = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.fillMaxSize(0.45f)
                    )
                }
            )
        }
    )
}


@Preview(showBackground = true)
@Composable
fun FullBlockScreenPreview() {
    ZenwellTheme(darkTheme = false) {
        FullBlockScreen()
    }
}