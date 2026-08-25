/*
 * This file contains code from:
 * https://github.com/jocoand/compose-showcase/
 *
 * Copyright (c) 2024 jocoand
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sarangem.zenwell.ui.sequenceshowcase

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Displays a dialog with a background overlay.
 * *
 * @param visible determines if the Showcase is visible or not.
 * @param targetCoordinates the coordinates of the target element that the Showcase is highlighting.
 * @param position the position of the dialog relative to the target element.
 * @param alignment the alignment of the dialog relative to the target element.
 * @param animationDuration the duration of the fade in and fade out animation respectively.
 * @param hasAppeared: callback function that is invoked when the display state of the Showcase changes.
 * @param shape the shape of highlight around the target element.
 * @param shapeMargin the margin or padding between highlighted shape and target element.
 * @param backgroundAlpha the alpha value of the background overlay.
 * @param fixedContent the content displayed at a fixed position on the screen regardless of target element
 * @param dialog the content of the dialog.
 */
@Composable
fun ShowcaseView(
    visible: Boolean,
    targetCoordinates: LayoutCoordinates,
    position: ShowcasePosition = ShowcasePosition.Default,
    alignment: ShowcaseAlignment = ShowcaseAlignment.Default,
    animationDuration: Pair<Int, Int> = 700 to 700,
    hasAppeared: (Boolean) -> Unit = {},
    shape: Shape = CircleShape,
    shapeMargin: Dp,
    backgroundAlpha: Float = 0.6f,
    fixedContent: @Composable () -> Unit = {},
    dialog: @Composable (Rect) -> Unit
) {
    // Prevent crash if coordinates are not attached
    if (!targetCoordinates.isAttached) {
        Log.w("ShowcaseView", "Target coordinates are not attached, skipping showcase")
        return
    }

    val transition =  remember { MutableTransitionState(false) }

    val density = LocalDensity.current
    val marginPx = with(density) { shapeMargin.toPx() }
    val targetRect = targetCoordinates.boundsInRoot()
    val highlightBounds = Rect(
        left = targetRect.left - marginPx,
        top = targetRect.top - marginPx,
        right = targetRect.right + marginPx,
        bottom = targetRect.bottom + marginPx
    )

    AnimatedVisibility(
        visibleState = transition,
        enter = fadeIn(tween(animationDuration.first)),
        exit = fadeOut(tween(animationDuration.second))
    ) {
        Box {
            ShowcaseBackground(
                coordinates = targetCoordinates,
                drawHighlight = {
                    withTransform({
                        translate(
                            left = highlightBounds.topLeft.x,
                            top = highlightBounds.topLeft.y
                        )
                    }) {
                        drawOutline(
                            color = Color.White,
                            outline = shape.createOutline(
                                size = highlightBounds.size,
                                layoutDirection = layoutDirection,
                                density = this
                            ),
                            blendMode = BlendMode.Clear
                        )
                    }
                },
                backgroundAlpha = backgroundAlpha
            )
            fixedContent()
            ShowcaseDialog(
                targetRect = targetCoordinates.boundsInRoot(),
                position = position,
                alignment = alignment,
                highlightBounds = highlightBounds,
                content = dialog
            )
        }
    }
    LaunchedEffect(visible) {
        transition.targetState = visible
    }
    LaunchedEffect(transition.isIdle) {
        if (transition.isIdle) {
            hasAppeared(transition.targetState)
        }
    }
}

/**
 * Draws the background overlay and the highlight around the target element.
 *
 * @param coordinates the coordinates of the target element that the Showcase is highlighting.
 * @param drawHighlight draws the highlight around the target element.
 */
@Composable
private fun ShowcaseBackground(
    coordinates: LayoutCoordinates,
    backgroundAlpha: Float,
    drawHighlight: DrawScope.(LayoutCoordinates) -> Unit
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(alpha = backgroundAlpha)
    ) {
        // Overlay
        drawRect(
            Color.Black.copy(alpha = backgroundAlpha),
            size = Size(size.width, size.height)
        )
        drawHighlight(coordinates)
    }
}

/**
 * A Composable function that positions and displays the dialog.
 *
 * @param targetRect te bounding rectangle of the target element.
 * @param position the position of the dialog relative to the target element.
 * @param alignment the alignment of the dialog relative to the target element.
 * @param highlightBounds the bounding rectangle of the highlight.
 * @param content the content of the dialog.
 */
@Composable
private fun ShowcaseDialog(
    targetRect: Rect,
    position: ShowcasePosition,
    alignment: ShowcaseAlignment,
    highlightBounds: Rect,
    content: @Composable (Rect) -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val container = LocalWindowInfo.current.containerDpSize
    val density = LocalDensity.current

    val screenHeight = with(density) { container.height.toPx() }
    val screenWidth = with(density) { container.width.toPx() }

    val verticalSpacerPx = with(density) { 16.dp.toPx() }
    val horizontalSpacerPx = with(density) { 16.dp.toPx() }

    Box(
        modifier = Modifier
            .widthIn(max = container.width - 32.dp)
            .offset{ IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .onGloballyPositioned {
                val dialogHeight = it.size.height
                val dialogWidth = it.size.width
                val highlightCenterX = highlightBounds.center.x

                offsetX = when (alignment) {
                    ShowcaseAlignment.Start -> highlightBounds.left
                    ShowcaseAlignment.End -> highlightBounds.right - dialogWidth
                    ShowcaseAlignment.CenterHorizontal -> (highlightCenterX - dialogWidth / 2)
                    ShowcaseAlignment.Default -> {
                        if (highlightCenterX > screenWidth / 2) {
                            highlightBounds.right - dialogWidth
                        } else {
                            highlightBounds.left
                        }
                    }
                }.coerceIn(horizontalSpacerPx, screenWidth - horizontalSpacerPx - dialogWidth)

                offsetY = when (position) {
                    ShowcasePosition.Top -> highlightBounds.top - verticalSpacerPx - dialogHeight
                    ShowcasePosition.Bottom -> highlightBounds.bottom + verticalSpacerPx
                    ShowcasePosition.Default -> {
                        if (targetRect.center.y > screenHeight / 2 + verticalSpacerPx) {
                            highlightBounds.top - verticalSpacerPx - dialogHeight
                        } else {
                            highlightBounds.bottom + verticalSpacerPx
                        }
                    }
                }
            }
    ) {
        content(highlightBounds)
    }
}