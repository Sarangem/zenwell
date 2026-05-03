/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data object SizingDefaults {
    internal const val TINY = 2
    internal const val EXTRA_SMALL = 4
    internal const val SMALL = 8
    internal const val MEDIUM = 16
    internal const val LARGE = 24
    internal const val FLOATING_BAR = 88
    internal const val IMAGE = 48
    internal const val DEFAULT = SMALL
}

data class Sizing(
    val tiny: Dp = SizingDefaults.TINY.dp,
    val extraSmall: Dp = SizingDefaults.EXTRA_SMALL.dp,
    val small: Dp = SizingDefaults.SMALL.dp,
    val medium: Dp = SizingDefaults.MEDIUM.dp,
    val large: Dp = SizingDefaults.LARGE.dp,
    val floatingBar: Dp = SizingDefaults.FLOATING_BAR.dp,
    val image: Dp = SizingDefaults.IMAGE.dp,
    val default: Dp = SizingDefaults.DEFAULT.dp
)

val LocalSizing = compositionLocalOf { Sizing() }
val MaterialTheme.sizing: Sizing
    @Composable
    @ReadOnlyComposable
    get() = LocalSizing.current