/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.contains
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.metadata
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import androidx.navigation3.ui.NavDisplay
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.ui.screens.customactivity.CustomActivityScreen
import com.sarangem.zenwell.ui.screens.customactivity.CustomActivityViewModel
import com.sarangem.zenwell.ui.screens.edit.EditScreen
import com.sarangem.zenwell.ui.screens.edit.EditScreenPlaceholder
import com.sarangem.zenwell.ui.screens.edit.EditViewModel
import com.sarangem.zenwell.ui.screens.home.HomeScreen
import com.sarangem.zenwell.ui.screens.pomodoro.FocusScreen
import com.sarangem.zenwell.ui.screens.settings.SettingsScreen
import com.sarangem.zenwell.ui.screens.stats.StatsScreen
import kotlinx.serialization.Serializable

@Composable
fun ZenwellAppScreen() {
    val backStack = rememberNavBackStack(HomePage)
    val editViewModel: EditViewModel = hiltViewModel()
    val customActivityViewModel: CustomActivityViewModel = hiltViewModel()

    NavDisplay(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        backStack = backStack,
        sceneStrategies = listOf( rememberListDetailSceneStrategy()),
        transitionSpec = {
            slideInHorizontally(initialOffsetX = { it }, animationSpec = tween()) togetherWith
                    ExitTransition.KeepUntilTransitionsFinished
        },
        popTransitionSpec = {
            EnterTransition.None togetherWith
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween())
        },
        predictivePopTransitionSpec = {
            EnterTransition.None togetherWith
                    scaleOut(targetScale = 0.7f, transformOrigin = TransformOrigin(pivotFractionX = 0.5f, pivotFractionY = 0.5f))
        },
        entryProvider = entryProvider {
            entry<HomePage>(
                metadata = ListDetailScene.listPane()
            ) {
                HomeScreen(
                    modifier = Modifier.fillMaxSize(),
                    scheduleClicked = editViewModel.uiState.collectAsState().value.schedule.id,
                    openEditScreen = { schedules ->
                        backStack.removeAll { it is EditPage }
                        editViewModel.initialize(schedules)
                        backStack.add(EditPage)
                    },
                    openFocusScreen = { backStack.add(PomodoroPage(it)) },
                    openSettingsScreen = { backStack.add(SettingsPage) },
                    openStatsScreen = { backStack.add(StatsPage) }
                )
            }

            entry<EditPage>(
                metadata = ListDetailScene.detailPane()
            ) {
                EditScreen(
                    modifier = Modifier.fillMaxSize(),
                    viewModel = editViewModel,
                    showTopAppBar = LocalBackButtonVisibility.current,
                    goBack = { backStack.removeLastOrNull() }
                )
            }

            entry<PomodoroPage> { key ->
                FocusScreen(
                    modifier = Modifier.fillMaxSize(),
                    schedule = key.schedule,
                    goBack = { backStack.removeLastOrNull() }
                )
            }

            entry<SettingsPage>(
                metadata = NavDisplay.transitionSpec {
                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween()) togetherWith
                            slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween())
                } + NavDisplay.popTransitionSpec {
                    slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween()) togetherWith
                            slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween())
                }
            ) {
                SettingsScreen(
                    modifier = Modifier.fillMaxSize(),
                    openCustomActivityScreen = {
                        customActivityViewModel.initialize()
                        backStack.add(CustomActivityPage)
                    },
                    goBack = { backStack.removeLastOrNull() }
                )
            }

            entry<CustomActivityPage> {
                CustomActivityScreen(
                    modifier = Modifier.fillMaxSize(),
                    viewModel = customActivityViewModel,
                    goBack = { backStack.removeLastOrNull() }
                )
            }

            entry<StatsPage> {
                StatsScreen(
                    modifier = Modifier.fillMaxSize(),
                    goBack = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}


@Serializable data object HomePage : NavKey
@Serializable data object EditPage : NavKey
@Serializable data class PomodoroPage(val schedule: Schedules) : NavKey
@Serializable data object SettingsPage : NavKey
@Serializable data object CustomActivityPage : NavKey
@Serializable data object StatsPage: NavKey

@Composable
fun <T : Any> rememberListDetailSceneStrategy(): ListDetailSceneStrategy<T> {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    return remember(windowSizeClass) { ListDetailSceneStrategy(windowSizeClass) }
}
class ListDetailSceneStrategy<T : Any>(val windowSizeClass: WindowSizeClass) : SceneStrategy<T> {
    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        if (!windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) return null
        val topEntry = entries.lastOrNull() ?: return null
        val listEntry = entries.findLast { it.metadata.contains(ListDetailScene.ListKey) } ?: return null
        val isDetail = topEntry.metadata.contains(ListDetailScene.DetailKey)
        if (!isDetail &&  topEntry !== listEntry) return null
        return ListDetailScene(
            key = listEntry.contentKey,
            previousEntries = if (isDetail) entries.dropLast(1) else entries.takeWhile { it !== listEntry },
            listEntry = listEntry,
            detailEntry = topEntry.takeIf { isDetail }
        )
    }
}
data class ListDetailScene<T : Any>(
    override val key: Any,
    override val previousEntries: List<NavEntry<T>>,
    val listEntry: NavEntry<T>,
    val detailEntry: NavEntry<T>?
) : Scene<T> {
    override val entries: List<NavEntry<T>> = listOfNotNull(listEntry, detailEntry)
    override val content: @Composable (() -> Unit) = {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.weight(0.4f)) {
                listEntry.Content()
            }
            CompositionLocalProvider(LocalBackButtonVisibility provides false) {
                Column(modifier = Modifier.weight(0.6f)) {
                    AnimatedContent(
                        targetState = detailEntry,
                        transitionSpec = { fadeIn(tween()) togetherWith fadeOut(tween()) },
                    ) { target ->
                        if (target != null) target.Content() else EditScreenPlaceholder(Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
    companion object {
        fun listPane() = metadata { put(ListKey, true) }
        fun detailPane() = metadata { put(DetailKey, true) }
    }
    object ListKey : NavMetadataKey<Boolean>
    object DetailKey : NavMetadataKey<Boolean>
}
val LocalBackButtonVisibility = compositionLocalOf { true }