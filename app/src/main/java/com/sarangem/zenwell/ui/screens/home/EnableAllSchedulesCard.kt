/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.sarangem.zenwell.R
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.ui.theme.sizing

@Composable
fun EnableAllSchedulesCard(
    schedulesList: List<Schedules>,
    enableAll: () -> Unit = {}
) {
    val schedulesInactive = remember(schedulesList) { schedulesList.count { !it.isActive } }
    AnimatedVisibility(schedulesInactive > 0) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.sizing.small)
                .shadow(elevation = MaterialTheme.sizing.tiny, shape = MaterialTheme.shapes.medium)
                .clip(MaterialTheme.shapes.medium)
                .background(darkColorScheme().error)
        ) {
            Text(
                text = pluralStringResource(
                    id = R.plurals.schedules_inactive,
                    count = schedulesInactive,
                    schedulesInactive, schedulesList.size
                ),
                color = darkColorScheme().onError,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(MaterialTheme.sizing.small)
                    .weight(1f)
            )
            Box(
                Modifier
                    .background(darkColorScheme().errorContainer)
                    .clickable(onClick = enableAll)
            ) {
                Text(
                    stringResource(R.string.enable_all),
                    color = darkColorScheme().onErrorContainer,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(MaterialTheme.sizing.small)
                )
            }
        }
    }
}