/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.contains
import androidx.navigation3.runtime.get
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.sarangem.zenwell.ui.screens.edit.EditScreenPlaceholder

data class ListDetailScene<T : Any>(
    override val key: Any,
    override val previousEntries: List<NavEntry<T>>,
    val listEntry: NavEntry<T>,
    val detailEntry: NavEntry<T>?,
    val isHome: Boolean
) : Scene<T> {
    override val entries: List<NavEntry<T>> = listOfNotNull(listEntry, detailEntry)
    override val content: @Composable (() -> Unit) = {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier =
                Modifier
                    .weight(0.4f)
                    .animateContentSize()
            ) { listEntry.Content() }
            if(isHome || detailEntry != null) {
                CompositionLocalProvider(LocalBackButtonVisibility provides false) {
                    AnimatedContent(
                        modifier = Modifier.weight(0.6f),
                        targetState = detailEntry,
                        transitionSpec = { fadeIn(tween()) togetherWith fadeOut(tween()) }
                    ) { target ->
                        if (target != null) target.Content() else EditScreenPlaceholder(Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
    companion object {
        fun listPane(isHome: Boolean) = metadata { put(ListKey, isHome) }
        fun detailPane(isHome: Boolean) = metadata { put(DetailKey, isHome) }
    }
    object ListKey : NavMetadataKey<Boolean>
    object DetailKey : NavMetadataKey<Boolean>
}

class ListDetailSceneStrategy<T : Any>(val windowSizeClass: WindowSizeClass) : SceneStrategy<T> {
    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        if (!windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) return null
        val topEntry = entries.lastOrNull() ?: return null
        val listEntry = entries.findLast { it.metadata.contains(ListDetailScene.ListKey) } ?: return null
        val isHome = listEntry.metadata.get<Boolean>(ListDetailScene.ListKey) == true
        val isDetail = topEntry.metadata.contains(ListDetailScene.DetailKey)
        if (!isDetail && topEntry !== listEntry) return null
        return ListDetailScene(
            key = listEntry.contentKey,
            previousEntries = if (isDetail) entries.dropLast(1) else entries.takeWhile { it !== listEntry },
            listEntry = listEntry,
            detailEntry = topEntry.takeIf { isDetail },
            isHome = isHome
        )
    }
}

@Composable
fun <T : Any> rememberListDetailSceneStrategy(): ListDetailSceneStrategy<T> {
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    return remember(windowSizeClass) { ListDetailSceneStrategy(windowSizeClass) }
}

val LocalBackButtonVisibility = compositionLocalOf { true }