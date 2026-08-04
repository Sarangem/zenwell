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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SequenceShowcase(
    state: SequenceShowcaseState = remember { SequenceShowcaseState() },
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalSequenceShowcaseState provides state) {
        Box(modifier = Modifier.fillMaxWidth()) {
            content()
            state.currentTarget?.let { target ->
                ShowcaseView(
                    visible = state.showCaseVisible,
                    targetCoordinates = target.coordinates,
                    position = target.position,
                    alignment = target.alignment,
                    animationDuration = target.duration,
                    hasAppeared = { appeared ->
                        if (appeared) {
                            state.onShowcaseViewAppear()
                        } else {
                            state.onShowcaseViewDisappear()
                        }
                    },
                    shape = target.shape,
                    shapeMargin = target.shapeMargin,
                    backgroundAlpha = target.backgroundAlpha,
                    fixedContent = target.fixedContent
                ) { targetRect ->
                    target.dialog(targetRect)
                }
            }
        }
    }
}

/**
 * Creates a Modifier that marks a Composable as a target for the SequenceShowcase.
 *
 * @param index The index of the target in the sequence.
 * @param position The position of the dialog relative to the target element.
 * @param alignment The alignment of the dialog relative to the target element respectively.
 * @param shape The shape of highlight around the target element.
 * @param shapeMargin the margin or padding between highlighted shape and target element.
 * @param animationDuration The duration of the fade enter and exit animation.
 * @param backgroundAlpha The alpha value of the background overlay.
 * @param fixedContent The content displayed at a fixed position on the screen regardless of target element
 * @param dialog The content of the dialog.
 */
@Composable
fun Modifier.sequenceShowcaseTarget(
    index: Int,
    showcaseState: SequenceShowcaseState = LocalSequenceShowcaseState.current,
    position: ShowcasePosition = ShowcasePosition.Default,
    alignment: ShowcaseAlignment = ShowcaseAlignment.Default,
    shape: Shape = CircleShape,
    shapeMargin: Dp = 8.dp,
    animationDuration: Pair<Int, Int> = 700 to 700,
    backgroundAlpha: Float = 0.6f,
    fixedContent: @Composable () -> Unit = {},
    dialog: @Composable (Rect) -> Unit,
): Modifier = onGloballyPositioned { coordinates ->
    showcaseState.targets[index] = SequenceShowcaseTarget(
        index = index,
        coordinates = coordinates,
        position = position,
        alignment = alignment,
        shape = shape,
        shapeMargin = shapeMargin,
        duration = animationDuration,
        backgroundAlpha = backgroundAlpha,
        fixedContent = fixedContent,
        dialog = dialog
    )
}