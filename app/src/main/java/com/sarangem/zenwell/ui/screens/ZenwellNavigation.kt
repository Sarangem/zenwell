/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.sarangem.zenwell.R
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.ui.screens.home.NewScheduleFAB
import com.sarangem.zenwell.ui.theme.sizing

@Composable
fun AppNavigationRail(
    modifier: Modifier = Modifier,
    backStack: NavBackStack<NavKey>
){
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

@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppNavigationFAB(
    modifier: Modifier = Modifier,
    backStack: NavBackStack<NavKey> = rememberNavBackStack(HomePage),
    addNewSchedule: suspend (Context, Boolean) -> Schedules = { _,_ -> Schedules() },
    openEditScreen: (Schedules) -> Unit = {},
    firstEntry: Int? = null,
    dismissShowcase: () -> Unit = {}
){
    val last = backStack.last()
    AnimatedVisibility(
        last == HomePage || last == StatsPage || last == SettingsPage
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            HorizontalFloatingToolbar(
                expanded = true,
                modifier = Modifier
                    .padding(MaterialTheme.sizing.small)
                    .padding(end = if(last == HomePage) MaterialTheme.sizing.floatingBar else 0.dp)
                    .align(Alignment.BottomCenter)
            ) {
                BottomBarIcon(backStack, HomePage, R.drawable.filled_home, R.string.home)
                BottomBarIcon(backStack, StatsPage, R.drawable.outlined_bar_chart, R.string.stats)
                BottomBarIcon(backStack, SettingsPage, R.drawable.outlined_settings, R.string.settings)
            }
            AnimatedVisibility(
                backStack.last() == HomePage,
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                NewScheduleFAB(
                    modifier,
                    firstEntry,
                    dismissShowcase,
                    addNewSchedule,
                    openEditScreen
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BottomBarIcon(
    backStack: NavBackStack<NavKey>,
    navKey: NavKey,
    @DrawableRes icon: Int,
    @StringRes name: Int
){
    val isSelected = backStack.last() == navKey
    Row(
        modifier = Modifier
            .clip(FloatingToolbarDefaults.ContainerShape)
            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Unspecified)
            .clickable { if (!isSelected) navKey.addToStack(backStack) }
            .padding(MaterialTheme.sizing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){
        Icon(
            painter = painterResource(icon),
            contentDescription = if(isSelected) null else stringResource(name),
            modifier = Modifier.size(MaterialTheme.sizing.large),
            tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else LocalContentColor.current
        )
        AnimatedVisibility(isSelected){
            Text(
                text = stringResource(name),
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 2.dp)
            )
        }
    }
}

fun NavKey.addToStack(backStack : NavBackStack<NavKey>) {
    backStack.removeAll {
        if (this == HomePage) true
        else it::class == this::class
    }
    backStack.add(this)
}