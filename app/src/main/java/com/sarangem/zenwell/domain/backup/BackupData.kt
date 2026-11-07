/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.domain.backup

import com.sarangem.zenwell.model.AppNames
import com.sarangem.zenwell.model.Schedules
import kotlinx.serialization.Serializable

@Serializable
data class BackupData(
    var schedule: Schedules = Schedules(),
    var appNamesList: List<AppNames> = listOf(),
)