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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.unit.Dp

val LocalSequenceShowcaseState = compositionLocalOf { SequenceShowcaseState() }

/**
 * Manages the targets of the showcase and the current target index.
 * It also controls the visibility of the showcase.
 */
class SequenceShowcaseState {
    internal var targets = mutableStateMapOf<Int, SequenceShowcaseTarget>()

    var currentTargetIndex by mutableIntStateOf(0)
        private set
    val currentTarget: SequenceShowcaseTarget?
        get() = targets[currentTargetIndex]

    var showCaseVisible by mutableStateOf(false)
        private set

    private var isTransitioning = false

    /**
     * Start the sequence showcase.
     *
     * @param index The index of the target to start with.
     */
    fun start(index: Int = 0) {
        currentTargetIndex = index
        showCaseVisible = true
    }

    /**
     * Move to the next target in the sequence showcase.
     */
    fun next() {
        isTransitioning = true
        showCaseVisible = false
    }

    /**
     * Dismiss the sequence showcase.
     */
    fun dismiss() {
        showCaseVisible = false
        isTransitioning = false
    }

    internal fun onShowcaseViewAppear() {
        if (isTransitioning) {
            isTransitioning = false
        }
    }

    internal fun onShowcaseViewDisappear() {
        if (isTransitioning) {
            currentTargetIndex++
            showCaseVisible = currentTarget != null
        }
    }
}

data class SequenceShowcaseTarget(
    val index: Int,
    val coordinates: LayoutCoordinates,
    val position: ShowcasePosition,
    val alignment: ShowcaseAlignment,
    val duration: Pair<Int, Int>,
    val shape: Shape,
    val shapeMargin: Dp,
    val backgroundAlpha: Float,
    val fixedContent: @Composable () -> Unit = {},
    val dialog: @Composable (Rect) -> Unit
)