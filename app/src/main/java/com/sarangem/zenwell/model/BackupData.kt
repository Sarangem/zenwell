package com.sarangem.zenwell.model

import com.sarangem.zenwell.database.tables.Schedules
import kotlinx.serialization.Serializable

@Serializable
data class BackupData(
    var schedule: Schedules = Schedules(),
    var appNamesList: List<Pair<String, String?>> = listOf(),
)