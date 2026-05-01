/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.pomodoro

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.sarangem.zenwell.R

@Composable
fun PomodoroProgressPills(
    totalSessions: Int,
    sessionsLeft: Int,
    modifier: Modifier = Modifier
) {
    val pillDescription = stringResource(R.string.pomodoro_sessions_left) + sessionsLeft
    Row(
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding_small))
            .semantics(mergeDescendants = true) {
                contentDescription = pillDescription
            },
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalSessions) { index ->
            val currentSessionIndex = totalSessions - sessionsLeft
            val isCompleted = index < currentSessionIndex
            val isCurrent = index == currentSessionIndex
            val width by animateDpAsState(
                dimensionResource(
                    if (isCurrent) R.dimen.padding_large else R.dimen.padding_medium
                )
            )
            Box(
                modifier = Modifier
                    .height(dimensionResource(R.dimen.padding_medium))
                    .width(width)
                    .clip(MaterialTheme.shapes.small)
                    .background(
                        when {
                            isCurrent -> MaterialTheme.colorScheme.primary
                            isCompleted -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.outlineVariant
                        }
                    )
            )
        }
    }
}