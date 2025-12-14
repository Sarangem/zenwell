package com.sarangem.zenwell.model

import com.sarangem.zenwell.database.tables.AppNames
import com.sarangem.zenwell.database.tables.BlockedApps
import com.sarangem.zenwell.database.tables.Schedules
import kotlinx.serialization.Serializable

@Serializable
data class BackupData(
    val schedulesList: List<Schedules>,
    val appNamesList: List<AppNames>,
    val blockedAppsList: List<BlockedApps>
)