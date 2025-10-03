package com.sarangem.zenwell.ui.editscreen.sections

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.database.tables.Schedules
import com.sarangem.zenwell.ui.editscreen.details.DetailsCardWithNumberField

@Composable
fun BreathingDetailsSection(
    schedule: Schedules,
    updateSchedule: (Schedules) -> Unit = {}
) {
    
    // breathing cycle duration
    DetailsCardWithNumberField(
        mainText = stringResource(R.string.breathing_cycle_duration),
        textFieldValue = schedule.breathingCycleDuration,
        updateSchedule = {
            updateSchedule(
                schedule.copy(
                    breathingCycleDuration = it
                )
            )
        },
        suffixText = stringResource(R.string.seconds),
        isStacked = true,
    )

    // breathing cycles number
    DetailsCardWithNumberField(
        mainText = stringResource(R.string.number_of_breathing_cycles),
        textFieldValue = schedule.breathingCycleNumber,
        updateSchedule = {
            updateSchedule(
                schedule.copy(
                    breathingCycleNumber = it
                )
            )
        },
        isStacked = true,
    )
    
}