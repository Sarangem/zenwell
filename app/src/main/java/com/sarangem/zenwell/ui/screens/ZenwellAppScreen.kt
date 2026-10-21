/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.sarangem.zenwell.ui.screens.customactivity.CustomActivityScreen
import com.sarangem.zenwell.ui.screens.customactivity.CustomActivityViewModel
import com.sarangem.zenwell.ui.screens.edit.EditScreen
import com.sarangem.zenwell.ui.screens.edit.EditViewModel
import com.sarangem.zenwell.ui.screens.home.HomeScreen
import com.sarangem.zenwell.ui.screens.home.HomeViewModel
import com.sarangem.zenwell.ui.screens.pomodoro.PomodoroScreen
import com.sarangem.zenwell.ui.screens.settings.SettingsScreen
import com.sarangem.zenwell.ui.screens.stats.StatsScreen
import com.sarangem.zenwell.ui.common.sequenceshowcase.SequenceShowcase
import com.sarangem.zenwell.ui.navigation.*

@Composable
fun ZenwellAppScreen() {
    val backStack = rememberNavBackStack(HomePage)
    val homeViewModel: HomeViewModel = hiltViewModel()
    val editViewModel: EditViewModel = hiltViewModel()
    val customActivityViewModel: CustomActivityViewModel = hiltViewModel()

    val isExpanded = currentWindowAdaptiveInfoV2().windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)
    val firstEntry = homeViewModel.uiState.collectAsState().value.userPreferences.firstEntry
    SequenceShowcase {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                if(!isExpanded) AppNavigationFAB(
                    backStack = backStack,
                    addNewSchedule = homeViewModel::addNewSchedule,
                    openEditScreen = { schedules ->
                        EditPage.addToStack(backStack)
                        editViewModel.initialize(schedules)
                    },
                    firstEntry = firstEntry,
                    setAsExistingUser = homeViewModel::setAsExistingUser
                )
            }
        ) { padding -> padding
            Row(Modifier.fillMaxSize()) {
                if(isExpanded) AppNavigationRail(backStack = backStack)
                NavDisplay(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface),
                    backStack = backStack,
                    sceneStrategies = listOf(rememberListDetailSceneStrategy()),
                    transitionSpec = { fadeIn(tween()) togetherWith fadeOut(tween()) },
                    popTransitionSpec = { fadeIn(tween()) togetherWith fadeOut(tween()) },
                    entryProvider = entryProvider {
                        entry<HomePage>(
                            metadata = ListDetailScene.listPane(true)
                        ) {
                            HomeScreen(
                                modifier = Modifier.fillMaxSize(),
                                viewModel = homeViewModel,
                                scheduleClicked = editViewModel.uiState.collectAsState().value.schedule.id,
                                openEditScreen = { schedules ->
                                    EditPage.addToStack(backStack)
                                    editViewModel.initialize(schedules)
                                },
                                openPomodoroScreen = { PomodoroPage.addToStack(backStack) }
                            )
                        }

                        entry<EditPage>(
                            metadata = ListDetailScene.detailPane(true)
                        ) {
                            EditScreen(
                                modifier = Modifier.fillMaxSize(),
                                viewModel = editViewModel,
                                showTopAppBar = LocalBackButtonVisibility.current,
                                goBack = { backStack.remove(EditPage) },
                                setAsExistingUser = { homeViewModel.setAsExistingUser() },
                                firstEntry = firstEntry
                            )
                        }

                        entry<PomodoroPage> {
                            PomodoroScreen(
                                modifier = Modifier.fillMaxSize(),
                                goBack = { backStack.remove(PomodoroPage) }
                            )
                        }

                        entry<StatsPage>(
                            metadata = ListDetailScene.listPane(false) +
                                    NavDisplay.predictivePopTransitionSpec { fadeIn(tween()) togetherWith fadeOut(tween()) }
                        ) { StatsScreen(Modifier.fillMaxSize()) }

                        entry<SettingsPage>(
                            metadata = ListDetailScene.listPane(false) +
                                    NavDisplay.predictivePopTransitionSpec { fadeIn(tween()) togetherWith fadeOut(tween()) }
                        ) {
                            SettingsScreen(
                                modifier = Modifier.fillMaxSize(),
                                openCustomActivityScreen = {
                                    CustomActivityPage.addToStack(backStack)
                                    customActivityViewModel.initialize()
                                }
                            )
                        }

                        entry<CustomActivityPage>(
                            metadata = ListDetailScene.detailPane(false)
                        ) {
                            CustomActivityScreen(
                                modifier = Modifier.fillMaxSize(),
                                viewModel = customActivityViewModel,
                                showTopAppBar = LocalBackButtonVisibility.current,
                                goBack = { backStack.removeLastOrNull() }
                            )
                        }
                    }
                )
            }
        }
    }
}