/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.model

import com.sarangem.zenwell.database.tables.Schedules
import kotlinx.serialization.Serializable

@Serializable
data class BackupData(
    var schedule: Schedules = Schedules(),
    var appNamesList: List<Pair<String, String?>> = listOf(),
)