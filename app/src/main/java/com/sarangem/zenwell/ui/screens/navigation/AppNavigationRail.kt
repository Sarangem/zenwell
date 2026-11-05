/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.navigation

import androidx.compose.animation.AnimatedContent
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

@Composable
fun AppNavigationRail(
    modifier: Modifier = Modifier,
    backStack: NavBackStack<NavKey>
) {
    val last = backStack.last()

    NavigationRail(modifier.fillMaxHeight()) {
        NavIcons.entries.forEach { navIcon ->

            val isSelected = navIcon.isSelected(last)
            NavigationRailItem(
                selected = isSelected,
                onClick = { navIcon.key.addToStack(backStack) },
                icon = {
                    AnimatedContent(isSelected) {
                        Icon(
                            painter = painterResource(navIcon.iconRes(it)),
                            contentDescription = null
                        )
                    }
            },
                label = { Text(stringResource(navIcon.labelRes)) }
            )
        }

    }
}