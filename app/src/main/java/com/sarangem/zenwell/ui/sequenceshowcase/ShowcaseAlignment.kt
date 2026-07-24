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
 * A sealed interface defines the alignment of the dialog relative to the target element in the Showcase.
 * It has four possible values:
 * - `Start`: Aligns the dialog to the start of the target element.
 * - `End`: Aligns the dialog to the end of the target element.
 * - `CenterHorizontal`: Centers the dialog horizontally relative to the target element.
 * - `Default`: Chooses the alignment based on the position of the target element on the screen.
 *    If the target element is in the right half of the screen, it aligns the dialog to the end of the target element,
 *    otherwise it aligns to the start.
 */
sealed interface ShowcaseAlignment {
    data object Start : ShowcaseAlignment
    data object End : ShowcaseAlignment
    data object CenterHorizontal : ShowcaseAlignment
    data object Default : ShowcaseAlignment
}