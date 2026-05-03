/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.edit.fields

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.sizing
import java.util.Calendar
import java.util.Locale

@Composable
fun SelectWeekDays(
    weekDays: List<Int>,
    updateValue: (List<Int>) -> Unit = {}
) {
    val daysList = getWeekDays()
    val isExpandedWidth = currentWindowAdaptiveInfo().windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)

    DetailsCard {
        if (isExpandedWidth) {
            Text(
                text = stringResource(R.string.choose_week_days),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            WeekDayButtonGroup(
                modifier = Modifier.weight(2f),
                daysList = daysList,
                weekDays = weekDays,
                updateValue = updateValue
            )
        } else {
            Column(Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.choose_week_days),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = MaterialTheme.sizing.small)
                )
                WeekDayButtonGroup(
                    modifier = Modifier.fillMaxWidth(),
                    daysList = daysList,
                    weekDays = weekDays,
                    updateValue = updateValue
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WeekDayButtonGroup(
    modifier: Modifier = Modifier,
    daysList: List<Pair<Int, Int>>,
    weekDays: List<Int>,
    updateValue: (List<Int>) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.sizing.tiny)
    ) {
        daysList.forEachIndexed { index, (day, abbr) ->
            val shape = when (index) {
                0 -> ButtonGroupDefaults.connectedLeadingButtonShape
                daysList.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShape
                else -> ButtonGroupDefaults.connectedMiddleButtonPressShape
            }

            ToggleButton(
                checked = day in weekDays,
                onCheckedChange = { checked ->
                    if (checked) {
                        updateValue(weekDays + day)
                    } else {
                        updateValue(weekDays - day)
                    }
                },
                shapes = ToggleButtonDefaults.shapes(
                    shape = shape,
                    checkedShape = shape,
                    pressedShape = shape
                ),
                colors = ToggleButtonDefaults.toggleButtonColors(
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceDim,
                    disabledContentColor = MaterialTheme.colorScheme.onSurface,
                    checkedContainerColor = MaterialTheme.colorScheme.primary,
                    checkedContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(abbr),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

fun getWeekDays(): List<Pair<Int, Int>> {
    val calendar = Calendar.getInstance(Locale.getDefault())
    val firstDay = calendar.firstDayOfWeek

    val daysList = listOf(
        Calendar.SUNDAY to R.string.sunday_abbr,
        Calendar.MONDAY to R.string.monday_abbr,
        Calendar.TUESDAY to R.string.tuesday_abbr,
        Calendar.WEDNESDAY to R.string.wednesday_abbr,
        Calendar.THURSDAY to R.string.thursday_abbr,
        Calendar.FRIDAY to R.string.friday_abbr,
        Calendar.SATURDAY to R.string.saturday_abbr,
    )

    val startIndex = daysList.indexOfFirst { it.first == firstDay }
    return if (startIndex == -1) daysList
    else daysList.drop(startIndex) + daysList.take(startIndex)
}