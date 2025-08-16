package com.sarangem.zenwell.ui

import com.sarangem.zenwell.data.database.tables.Schedules

data class AppUiState(

    // navigation
    val isShowingHomePage: Boolean = true,
    val isShowingSettingsPage: Boolean = false,

    // real information
    val schedule: Schedules = Schedules(),
    val appNames: List<String>? = null,

    // error cases
    val isRunningTimeInvalid: Boolean = false,
    val isNotificationTimeInvalid: Boolean = false,
    val isPomodoroSessionNumberInvalid: Boolean = false
)
