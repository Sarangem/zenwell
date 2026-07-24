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

/**
 * A sealed interface that represents the position of a showcase dialog relative to a target element.
 * It has three possible values:
 * - `Top`: The showcase dialog should be positioned above the target element.
 * - `Bottom`: The showcase dialog should be positioned below the target element.
 * - `Default`: The showcase dialog's position is determined based on the location of the target element on the screen.
 *    If the target element is in the upper half of the screen, the showcase dialog will be positioned below it, and vice versa.
 */
sealed interface ShowcasePosition {
    data object Top : ShowcasePosition
    data object Bottom : ShowcasePosition
    data object Default : ShowcasePosition
}