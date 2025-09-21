package com.sarangem.zenwell.ui.editscreen

import com.sarangem.zenwell.data.database.tables.Schedules

data class EditUiState(

    val schedule: Schedules = Schedules(),
    val appNames: List<String>? = null,

    val isRunningTimeInvalid: Boolean = false,
    val isNotificationTimeInvalid: Boolean = false,
    val isPomodoroSessionNumberInvalid: Boolean = false
)
