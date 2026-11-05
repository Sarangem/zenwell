/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation3.runtime.NavKey
import com.sarangem.zenwell.R

enum class NavIcons(
    val key: NavKey,
    @StringRes val labelRes: Int,
    private val filledIconRes: Int,
    private val outlinedIconRes: Int,
    private val expandedScreenKeys: Set<NavKey>
) {
    HOME(
        labelRes = R.string.home,
        filledIconRes = R.drawable.filled_home,
        outlinedIconRes = R.drawable.outlined_home,
        key = HomePage,
        expandedScreenKeys = setOf(HomePage, EditPage)
    ),
    STATS(
        labelRes = R.string.stats,
        filledIconRes = R.drawable.filled_bar_chart,
        outlinedIconRes = R.drawable.outlined_bar_chart,
        key = StatsPage,
        expandedScreenKeys = setOf(StatsPage)
    ),
    SETTINGS(
        labelRes = R.string.settings,
        filledIconRes = R.drawable.filled_settings,
        outlinedIconRes = R.drawable.outlined_settings,
        key = SettingsPage,
        expandedScreenKeys = setOf(SettingsPage, CustomActivityPage)
    );

    fun isSelected(currentKey: NavKey?): Boolean = currentKey in expandedScreenKeys
    @DrawableRes fun iconRes(isSelected: Boolean): Int = if (isSelected) filledIconRes else outlinedIconRes
}