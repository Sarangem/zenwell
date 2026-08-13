/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.stats

import com.sarangem.zenwell.utils.MyPackageInfo

data class StatsUiState(
    val isPermissionGranted: Boolean = true,
    val isLoading: Boolean = true,
    val installedApps: List<MyPackageInfo> = listOf(),
    val dailyUsage: List<DailyAppUsageData> = emptyList(),
    val filteredDailyUsage: List<DailyAppUsageData> = emptyList(),
    val weeklyAverageInMinutes: Int = 0,
    val blockedApps: List<String>? = null,
    val statsFilter: StatsFilter = StatsFilter.AllApps
)