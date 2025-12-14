package com.sarangem.zenwell.model

import androidx.navigation3.runtime.NavKey
import com.sarangem.zenwell.database.tables.Schedules
import kotlinx.serialization.Serializable

@Serializable
data object HomePage : NavKey

@Serializable
data object SettingsPage : NavKey

@Serializable
data class FocusPage(val schedules: Schedules) : NavKey