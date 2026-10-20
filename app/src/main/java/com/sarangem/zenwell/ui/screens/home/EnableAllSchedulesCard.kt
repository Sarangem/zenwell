/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.sarangem.zenwell.R
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.ui.common.PermissionRequestCard

@Composable
fun EnableAllSchedulesCard(
    schedulesList: List<Schedules>,
    enableAll: () -> Unit = {}
) {
    val schedulesInactive = remember(schedulesList) { schedulesList.count { !it.isActive } }
    AnimatedVisibility(schedulesInactive > 0) {
        PermissionRequestCard(
            text = pluralStringResource(
                id = R.plurals.schedules_inactive,
                count = schedulesInactive,
                schedulesInactive, schedulesList.size
            ),
            buttonText = stringResource(R.string.enable_all),
            onClick = enableAll,
            cardColor = darkColorScheme().error.copy(alpha = 0.9f),
        )
    }
}