package com.sarangem.zenwell.ui.screens.edit.sections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.sarangem.zenwell.R
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.ui.screens.edit.ValidationError
import com.sarangem.zenwell.ui.screens.edit.fields.ClockButton
import com.sarangem.zenwell.ui.screens.edit.fields.SelectWeekDays
import com.sarangem.zenwell.ui.theme.ZenwellTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ChooseRunningTime(
    modifier: Modifier = Modifier,
    schedule: Schedules,
    updateSchedule: (Schedules) -> Unit = {},
    validationError: Set<ValidationError>
) {
    Card(modifier = modifier.padding(dimensionResource(R.dimen.padding_small))) {

        Text(
            text = stringResource(R.string.choose_running_time),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
        )

        Row(
            modifier = Modifier.padding(
                start = dimensionResource(R.dimen.padding_small),
                end = dimensionResource(R.dimen.padding_small),
                bottom = dimensionResource(R.dimen.padding_medium)
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // start time
            ClockButton(
                time = schedule.startTimeInMinutes,
                updateUiState = {
                    updateSchedule(
                        schedule.copy(
                            startTimeInMinutes = it
                        )
                    )
                },
                timePickerTitle = stringResource(R.string.choose_start_time),
                modifier = Modifier.weight(1f)
            )

            Text(
                text = stringResource(R.string.to),
                style = MaterialTheme.typography.bodyLarge,
            )

            // end time
            ClockButton(
                time = schedule.endTimeInMinutes,
                updateUiState = {
                    updateSchedule(
                        schedule.copy(
                            endTimeInMinutes = it
                        )
                    )
                },
                timePickerTitle = stringResource(R.string.choose_end_time),
                modifier = Modifier.weight(1f)
            )
        }


        Text(
            text = stringResource(R.string.choose_week_days),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(
                start = dimensionResource(R.dimen.padding_small),
                end = dimensionResource(R.dimen.padding_small)
            )
        )
        SelectWeekDays(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(R.dimen.padding_small),
                    end = dimensionResource(R.dimen.padding_small),
                    bottom = dimensionResource(R.dimen.padding_small)
                ),
            weekDays = schedule.weekDays,
            updateWeekDays = {
                updateSchedule(
                    schedule.copy(
                        weekDays = it
                    )
                )
            }
        )

        AnimatedVisibility(visible = ValidationError.RunningTime in validationError) {
            Text(
                text = stringResource(R.string.running_time_is_invalid),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
fun ChooseRunningTimeLightPreview() {
    ZenwellTheme {
        ChooseRunningTime(
            schedule = Schedules(),
            validationError = setOf(ValidationError.RunningTime)
        )
    }
}