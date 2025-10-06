package com.sarangem.zenwell.data

import com.sarangem.zenwell.data.database.tables.AppNames
import com.sarangem.zenwell.data.database.tables.BlockedApps
import com.sarangem.zenwell.data.database.tables.Schedules
import kotlinx.serialization.Serializable

@Serializable
data class BackupData(
    val schedulesList: List<Schedules>,
    val appNamesList: List<AppNames>,
    val blockedAppsList: List<BlockedApps>
)
