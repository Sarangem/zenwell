/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.overlay.common

import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumExtendedFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.sizing

const val APP_BLOCKED = "This app is fully blocked"

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OverlayScaffold(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    floatingActionButton: @Composable () -> Unit = {},
    showExit: Boolean = true,
    onExit: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.sizing.small)
            ) {
                floatingActionButton()
                if(showExit){
                    MediumExtendedFloatingActionButton(
                        onClick = onExit,
                        icon = {
                            Icon(
                                painterResource(R.drawable.filled_exit_to_app),
                                contentDescription = null
                            )
                        },
                        text = {
                            Text(
                                text = stringResource(R.string.exit),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
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
    showExit: Boolean = true,
    onExit: () -> Unit = {}
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isExpandedWidth =
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    val isCompactHeight =
        !windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)
    val showRowLayout = isExpandedWidth || isCompactHeight
    val fabPadding = Modifier.padding(bottom = if(showExit) MaterialTheme.sizing.floatingBar else 0.dp)

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
                            .padding(MaterialTheme.sizing.small)
                    )
                    supportingPane(fabPadding.weight(1f - mainPaneRowWeight))
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    mainPane(
                        Modifier
                            .weight(mainPaneColumnWeight)
                            .padding(MaterialTheme.sizing.small)
                    )
                    supportingPane(fabPadding.weight(1f - mainPaneColumnWeight))
                }
            }
        },
        showExit = showExit,
        onExit = onExit
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
    onTimerEnd: () -> Unit = {},
    showExit: Boolean = true,
    onExit: () -> Unit = {}
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
        },
        showExit = showExit,
        onExit = onExit
    )
}