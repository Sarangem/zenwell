package com.sarangem.zenwell.ui.screens.edit.sections

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.database.tables.Schedules
import com.sarangem.zenwell.ui.screens.edit.ValidationError
import com.sarangem.zenwell.ui.screens.edit.DetailsCardWithNumberField
import com.sarangem.zenwell.ui.screens.edit.DetailsCardWithSwitch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WaitDetailsSection(
    schedule: Schedules,
    validationErrors: Set<ValidationError>,
    updateSchedule: (Schedules) -> Unit = {}
){

    // open time
    DetailsCardWithNumberField(
        mainText = stringResource(R.string.open_time),
        textFieldValue = schedule.openTimeInMinutes,
        updateSchedule = {
            updateSchedule(
                schedule.copy(
                    openTimeInMinutes = it
                )
            )
        },
        suffixText = stringResource(R.string.minutes),
        isStacked = true,
    )

    // ask before opening
    DetailsCardWithSwitch(
        mainText = stringResource(R.string.choose_wait_enter_button),
        checked = schedule.waitEnterButton,
        isStacked = true,
        onCheckedChange = {
            updateSchedule(
                schedule.copy(
                    waitEnterButton = it
                )
            )
        }
    )

    // notification time
    DetailsCardWithNumberField(
        mainText = stringResource(R.string.send_notification_before_closing),
        textFieldValue = schedule.notificationTimeInMinutes,
        updateSchedule = {
            updateSchedule(
                schedule.copy(
                    notificationTimeInMinutes = it
                )
            )
        },
        suffixText = stringResource(R.string.minutes),
        isStacked = true,
        isError = ValidationError.NotificationTime in validationErrors,
        errorMessage = stringResource(R.string.notification_time_invalid)
    )

}