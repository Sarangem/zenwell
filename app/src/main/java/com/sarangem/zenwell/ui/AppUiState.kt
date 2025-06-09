package com.sarangem.zenwell.ui

import com.sarangem.zenwell.data.tables.Schedules

data class AppUiState(
    val isShowingHomePage: Boolean = true,
    val isShowingSettingsPage: Boolean = false,
    val schedule: Schedules = Schedules(),
    val appNames: List<String>? = null
)
