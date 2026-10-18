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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.sizing

@Composable
fun ProgressPills(
    totalSessions: Int,
    sessionsCompleted: Int,
    modifier: Modifier = Modifier
) {
    val pillDescription = stringResource(R.string.pomodoro_sessions_left)
    Row(
        modifier = modifier
            .padding(MaterialTheme.sizing.small)
            .semantics(mergeDescendants = true) {
                contentDescription = pillDescription + (totalSessions - sessionsCompleted)
            },
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.sizing.tiny),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalSessions) { index ->
            val isCompleted = index < sessionsCompleted
            val isCurrent = index == sessionsCompleted
            val width by animateDpAsState(if (isCurrent) MaterialTheme.sizing.large else MaterialTheme.sizing.medium)
            Box(
                modifier = Modifier
                    .height(MaterialTheme.sizing.medium)
                    .width(width)
                    .clip(MaterialTheme.shapes.small)
                    .background(
                        when {
                            isCurrent -> MaterialTheme.colorScheme.primary
                            isCompleted -> MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)
                            else -> MaterialTheme.colorScheme.outlineVariant
                        }
                    )
            )
        }
    }
}