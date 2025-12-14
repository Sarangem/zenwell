package com.sarangem.zenwell.ui.screens.edit.sections

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sarangem.zenwell.R
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.ui.screens.edit.DetailsCardWithRangeNumberField

@Composable
fun MultiplicationTableDetailsSection(
    schedule: Schedules,
    updateSchedule: (Schedules) -> Unit = {}
){
    // range of numbers to select multiplication table
    // '''2''' * 1 = 2
    DetailsCardWithRangeNumberField(
        mainText = stringResource(R.string.range_of_multiplication_table_number),
        isStacked = true,
        firstFieldValue = schedule.multiplicationMinNum,
        lastFieldValue = schedule.multiplicationMaxNum,
        updateFirstValue = {
            updateSchedule(
                schedule.copy(
                    multiplicationMinNum = it
                )
            )
        },
        updateLastValue = {
            updateSchedule(
                schedule.copy(
                    multiplicationMaxNum =  it
                )
            )
        }
    )

    // range of numbers to select multiplier
    // 2 * '''1''' = 2
    DetailsCardWithRangeNumberField(
        mainText = stringResource(R.string.range_of_multiplier),
        isStacked = true,
        firstFieldValue = schedule.multiplierMinNum,
        lastFieldValue = schedule.multiplierMaxNum,
        updateFirstValue = {
            updateSchedule(
                schedule.copy(
                    multiplierMinNum = it
                )
            )
        },
        updateLastValue = {
            updateSchedule(
                schedule.copy(
                    multiplierMaxNum =  it
                )
            )
        }
    )
}