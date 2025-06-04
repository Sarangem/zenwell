package com.sarangem.zenwell.ui

import com.sarangem.zenwell.data.tables.Schedules

data class AppUiState(
    val isShowingHomePage: Boolean = true,
    val isShowingSettingsPage: Boolean = false,
    val scheduleId: Int = 0,
    val scheduleInfo: Schedules = Schedules(),
    val appNames: List<String> = emptyList<String>()
)