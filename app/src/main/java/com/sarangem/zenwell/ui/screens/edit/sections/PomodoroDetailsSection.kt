package com.sarangem.zenwell.ui.screens.edit.sections

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sarangem.zenwell.R
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.ui.screens.edit.ValidationError
import com.sarangem.zenwell.ui.screens.edit.DetailsCardColumn
import com.sarangem.zenwell.ui.screens.edit.DetailsCardWithNumberField
import com.sarangem.zenwell.ui.screens.edit.LabelDetailsCard
import com.sarangem.zenwell.ui.screens.edit.LabelState

@Composable
fun PomodoroDetailsSection(
    schedule: Schedules,
    validationErrors: Set<ValidationError>,
    updateSchedule: (Schedules) -> Unit = {}
){
    DetailsCardColumn {

        // pomodoro work time
        DetailsCardWithNumberField(
            mainText = stringResource(R.string.work_time),
            textFieldValue = schedule.pomodoroWorkTimeInMinutes,
            updateSchedule = {
                updateSchedule(
                    schedule.copy(
                        pomodoroWorkTimeInMinutes = it
                    )
                )
            },
            suffixText = stringResource(R.string.minutes),
            isStacked = true
        )

        // pomodoro rest time
        DetailsCardWithNumberField(
            mainText = stringResource(R.string.rest_time),
            textFieldValue = schedule.pomodoroRestTimeInMinutes,
            updateSchedule = {
                updateSchedule(
                    schedule.copy(
                        pomodoroRestTimeInMinutes = it
                    )
                )
            },
            suffixText = stringResource(R.string.minutes),
            isStacked = true
        )

        // pomodoro sessions count
        DetailsCardWithNumberField(
            mainText = stringResource(R.string.number_of_pomodoro_sessions),
            textFieldValue = schedule.pomodoroSessionNumber,
            updateSchedule = {
                updateSchedule(
                    schedule.copy(
                        pomodoroSessionNumber = it
                    )
                )
            },
            isError = ValidationError.PomodoroSessionNumber in validationErrors,
            errorMessage = stringResource(R.string.pomodoro_session_number_invalid),
            isStacked = true
        )

        LabelDetailsCard(
            mainText = stringResource(R.string.actions_to_show_in_work_time),
            labelList = listOf(
                LabelState(
                    title = stringResource(R.string.pause_resume),
                    isSelected = schedule.showPauseInWorkTime,
                    onSelectChange = {
                        updateSchedule(
                            schedule.copy(
                                showPauseInWorkTime = it
                            )
                        )
                    }
                ),
                LabelState(
                    title = stringResource(R.string.skip),
                    isSelected = schedule.showSkipInWorkTime,
                    onSelectChange = {
                        updateSchedule(
                            schedule.copy(
                                showSkipInWorkTime = it
                            )
                        )
                    }
                )
            )
        )
        LabelDetailsCard(
            mainText = stringResource(R.string.actions_to_show_in_rest_time),
            labelList = listOf(
                LabelState(
                    title = stringResource(R.string.pause_resume),
                    isSelected = schedule.showPauseInRestTime,
                    onSelectChange = {
                        updateSchedule(
                            schedule.copy(
                                showPauseInRestTime = it
                            )
                        )
                    }
                ),
                LabelState(
                    title = stringResource(R.string.skip),
                    isSelected = schedule.showSkipInRestTime,
                    onSelectChange = {
                        updateSchedule(
                            schedule.copy(
                                showSkipInRestTime = it
                            )
                        )
                    }
                )
            )
        )
    }
}