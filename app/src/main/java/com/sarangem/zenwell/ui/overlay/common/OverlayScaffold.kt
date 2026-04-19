/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.overlay.common

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.window.core.layout.WindowSizeClass
import com.sarangem.zenwell.R

const val APP_BLOCKED = "This app is fully blocked"

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OverlayScaffold(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    floatingActionButton: @Composable () -> Unit = {}
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        floatingActionButton = floatingActionButton
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }

}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OverlayScaffold(
    modifier: Modifier = Modifier,
    mainPane: @Composable (Modifier) -> Unit,
    @FloatRange(
        from = 0.0,
        to = 1.0,
        fromInclusive = false,
        toInclusive = false
    ) mainPaneRowWeight: Float = 0.5f,
    @FloatRange(
        from = 0.0,
        to = 1.0,
        fromInclusive = false,
        toInclusive = false
    ) mainPaneColumnWeight: Float = 0.5f,
    supportingPane: @Composable (Modifier) -> Unit,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isExpandedWidth =
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    val isCompactHeight =
        !windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)
    val showRowLayout = isExpandedWidth || isCompactHeight

    OverlayScaffold(
        modifier = modifier,
        content = {
            if (showRowLayout) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    mainPane(
                        Modifier
                            .weight(mainPaneRowWeight)
                            .padding(dimensionResource(R.dimen.padding_small))
                    )
                    supportingPane(Modifier.weight(1f - mainPaneRowWeight))
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    mainPane(
                        Modifier
                            .weight(mainPaneColumnWeight)
                            .padding(dimensionResource(R.dimen.padding_small))
                    )
                    supportingPane(Modifier.weight(1f - mainPaneColumnWeight))
                }
            }
        }
    )
}

@Composable
fun OverlayScaffold(
    modifier: Modifier = Modifier,
    mainPane: @Composable (Modifier) -> Unit,
    @FloatRange(from = 0.0, to = 1.0) mainPaneRowWeight: Float = 0.5f,
    @FloatRange(from = 0.0, to = 1.0) mainPaneColumnWeight: Float = 0.5f,
    showOpen: Boolean = false,
    message: String,
    onTimerEnd: () -> Unit = {}
) {
    OverlayScaffold(
        modifier = modifier,
        mainPane = mainPane,
        mainPaneRowWeight = mainPaneRowWeight,
        mainPaneColumnWeight = mainPaneColumnWeight,
        supportingPane = { modifier ->
            OpenableMessageCard(
                modifier = modifier,
                showOpen = showOpen,
                message = message,
                onTimerEnd = onTimerEnd,
            )
        }
    )
}