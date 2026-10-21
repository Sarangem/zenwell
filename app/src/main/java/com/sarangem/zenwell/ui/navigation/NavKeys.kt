/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object HomePage : NavKey
@Serializable data object EditPage : NavKey
@Serializable data object PomodoroPage : NavKey
@Serializable data object SettingsPage : NavKey
@Serializable data object CustomActivityPage : NavKey
@Serializable data object StatsPage: NavKey

fun NavKey.addToStack(backStack : NavBackStack<NavKey>) {
    backStack.removeAll {
        if (this == HomePage) true
        else it::class == this::class
    }
    backStack.add(this)
}