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
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import androidx.navigation3.ui.NavDisplay
import androidx.window.core.layout.WindowSizeClass
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.ui.screens.ListDetailScene.Companion.DETAIL_KEY
import com.sarangem.zenwell.ui.screens.ListDetailScene.Companion.LIST_KEY
import com.sarangem.zenwell.ui.screens.ListDetailScene.Companion.detailPane
import com.sarangem.zenwell.ui.screens.ListDetailScene.Companion.listPane
import com.sarangem.zenwell.ui.screens.edit.EditScreen
import com.sarangem.zenwell.ui.screens.edit.EditScreenPlaceholder
import com.sarangem.zenwell.ui.screens.focus.FocusScreen
import com.sarangem.zenwell.ui.screens.home.HomeScreen
import com.sarangem.zenwell.ui.screens.settings.SettingsScreen
import kotlinx.serialization.Serializable

@Composable
fun ZenwellAppScreen() {

    val backStack = rememberNavBackStack(HomePage)
    NavDisplay(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        backStack = backStack,
        sceneStrategy = rememberListDetailSceneStrategy {
            EditScreenPlaceholder(modifier = Modifier.fillMaxSize())
        },
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
        entryProvider = { key ->
            when (key) {
                is HomePage -> NavEntry(
                    key = key,
                    metadata = listPane()
                ) {
                    HomeScreen(
                        modifier = Modifier.fillMaxSize(),
                        scheduleClicked = (backStack.lastOrNull() as? EditPage)?.schedule?.id ?: 0,
                        openEditScreen = { schedules ->
                            backStack.removeAll { it is EditPage }
                            backStack.add(EditPage(schedules))
                        },
                        openFocusScreen = { backStack.add(FocusPage(it)) },
                        openSettingsScreen = { backStack.add(SettingsPage) }
                    )
                }

                is EditPage -> NavEntry(
                    key = key,
                    metadata = detailPane()
                ) {
                    EditScreen(
                        modifier = Modifier.fillMaxSize(),
                        schedule = key.schedule,
                        showTopAppBar = LocalBackButtonVisibility.current,
                        goBack = { backStack.removeLastOrNull() }
                    )
                }

                is FocusPage -> NavEntry(key) {
                    FocusScreen(
                        modifier = Modifier.fillMaxSize(),
                        schedule = key.schedule,
                        goBack = { backStack.removeLastOrNull() }
                    )
                }

                is SettingsPage -> NavEntry(
                    key = key,
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
                        goBack = { backStack.removeLastOrNull() }
                    )
                }

                else -> error("Unknown route: $key")
            }
        }
    )
}


// navigation keys
@Serializable data object HomePage : NavKey
@Serializable data class EditPage(val schedule: Schedules) : NavKey
@Serializable data class FocusPage(val schedule: Schedules) : NavKey
@Serializable data object SettingsPage : NavKey


@Composable
fun <T : Any> rememberListDetailSceneStrategy(
    placeholder: @Composable () -> Unit = {}
): ListDetailSceneStrategy<T> {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    return remember(windowSizeClass, placeholder) {
        ListDetailSceneStrategy(windowSizeClass, placeholder)
    }
}
class ListDetailSceneStrategy<T : Any>(
    val windowSizeClass: WindowSizeClass,
    val placeholder: @Composable () -> Unit
) : SceneStrategy<T> {
    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        val topEntry = entries.lastOrNull() ?: return null
        if (!windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            return null
        }
        val isList = topEntry.metadata.containsKey(LIST_KEY)
        val isDetail = topEntry.metadata.containsKey(DETAIL_KEY)
        if (!isList && !isDetail) return null
        val listEntry = entries.findLast { it.metadata.containsKey(LIST_KEY) } ?: return null
        val previous = entries.takeWhile { it != listEntry }

        return ListDetailScene(
            key = listEntry.contentKey,
            previousEntries = previous,
            listEntry = listEntry,
            detailEntry = if (isDetail) topEntry else null,
            placeholder = placeholder
        )
    }
}
class ListDetailScene<T : Any>(
    override val key: Any,
    override val previousEntries: List<NavEntry<T>>,
    val listEntry: NavEntry<T>,
    val detailEntry: NavEntry<T>?,
    val placeholder: @Composable () -> Unit
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
                        transitionSpec = {
                            fadeIn(animationSpec = tween()) togetherWith fadeOut(animationSpec = tween())
                        }
                    ) { targetDetail ->
                        if (targetDetail != null) {
                            targetDetail.Content()
                        } else {
                            placeholder()
                        }
                    }
                }
            }
        }
    }
    companion object {
        internal const val LIST_KEY = "List"
        internal const val DETAIL_KEY = "Detail"
        fun listPane() = mapOf(LIST_KEY to true)
        fun detailPane() = mapOf(DETAIL_KEY to true)
    }
}
val LocalBackButtonVisibility = compositionLocalOf { true }