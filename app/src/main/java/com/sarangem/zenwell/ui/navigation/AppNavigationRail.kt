/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.navigation

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.sarangem.zenwell.R

@Composable
fun AppNavigationRail(
    modifier: Modifier = Modifier,
    backStack: NavBackStack<NavKey>
) {
    val last = backStack.last()
    NavigationRail(modifier.fillMaxHeight()) {
        NavigationRailItem(
            selected = last == HomePage || last == EditPage,
            onClick = { HomePage.addToStack(backStack) },
            icon = { Icon(painterResource(R.drawable.filled_home), contentDescription = null) },
            label = { Text(stringResource(R.string.home)) }
        )
        NavigationRailItem(
            selected = last == StatsPage,
            onClick = { StatsPage.addToStack(backStack) },
            icon = { Icon(painterResource(R.drawable.outlined_bar_chart), contentDescription = null) },
            label = { Text(stringResource(R.string.stats)) }
        )
        NavigationRailItem(
            selected = last == SettingsPage || last == CustomActivityPage,
            onClick = { SettingsPage.addToStack(backStack) },
            icon = { Icon(painterResource(R.drawable.outlined_settings), contentDescription = null) },
            label = { Text(stringResource(R.string.settings)) }
        )
    }
}