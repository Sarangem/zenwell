package com.sarangem.zenwell.ui.editscreen.fields

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.sarangem.zenwell.R
import com.sarangem.zenwell.utils.getWeekDays

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SelectWeekDays(
    modifier: Modifier = Modifier,
    weekDays: List<Int>,
    updateWeekDays: (List<Int>) -> Unit = {}
) {
    val daysList = getWeekDays()

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
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
                        updateWeekDays(weekDays + day)
                    } else {
                        updateWeekDays(weekDays - day)
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
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.card_elevation))
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(abbr),
                    style = MaterialTheme.typography.labelLarge,
                )
            }

        }
    }
}