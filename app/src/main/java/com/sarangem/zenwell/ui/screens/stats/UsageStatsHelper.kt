/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.stats

import android.app.usage.UsageStatsManager
import android.content.Context
import androidx.compose.ui.graphics.Color
import android.graphics.drawable.Drawable
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class AppUsageData(
    val packageName: String,
    val timeInMinutes: Int,
    val appName: String? = null,
    val icon: Drawable? = null,
    val iconColor: Color = Color.Gray
)

data class DailyAppUsageData(
    val weekDay: Int,
    val weekDayName: String? = null,
    val data: List<AppUsageData>,
    val totalTimeInMinutes: Int = 0
)

val iconColors = listOf(
    Color(0xFF5C6BC0),
    Color(0xFFAB47BC),
    Color(0xFFEC407A),
    Color(0xFFFF7043),
    Color(0xFFFFA726),
    Color(0xFF9CCC65),
    Color(0xFF26A69A),
    Color(0xFF29B6F6)
)

suspend fun getDailyAppUsage(
    context: Context,
    uiState: StateFlow<StatsUiState>
): List<DailyAppUsageData> {
    val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    val dateFormat = SimpleDateFormat("E", Locale.getDefault())
    val cal = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }
    cal.add(Calendar.DAY_OF_YEAR, 1)
    val endTimeMs = cal.timeInMillis
    cal.add(Calendar.DAY_OF_YEAR, -8)
    val startTimeMs = cal.timeInMillis

    val statsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTimeMs, endTimeMs)

    val installedApps = uiState.first { it.installedApps.isNotEmpty() }.installedApps

    val usageByDay = statsList
        ?.filter { it.totalTimeInForeground > 0 }
        ?.groupBy { Calendar.getInstance().apply { timeInMillis = it.firstTimeStamp }.get(Calendar.DAY_OF_YEAR) }
        ?.mapValues { (_, stats) ->
            stats.groupBy { it.packageName }
                .map { (pkgName, appStats) ->
                    val appInfo = installedApps.firstOrNull { pkgName == it.packageName }
                    AppUsageData(
                        packageName = pkgName,
                        timeInMinutes = (appStats.sumOf { it.totalTimeInForeground } / 1000 / 60).toInt(),
                        appName = appInfo?.appName,
                        icon = appInfo?.icon,
                        iconColor = iconColors[pkgName.hashCode().mod(iconColors.size)]
                    )
                }
        } ?: emptyMap()

    return (0..7).map { daysIntoPast ->
        val targetCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -daysIntoPast) }
        val data = usageByDay[targetCal.get(Calendar.DAY_OF_YEAR)] ?: emptyList()
        val sum = data.sumOf { it.timeInMinutes }
        DailyAppUsageData(
            weekDay = targetCal.get(Calendar.DAY_OF_WEEK),
            weekDayName = dateFormat.format(targetCal.time),
            data = data,
            totalTimeInMinutes = sum
        )
    }.reversed()
}

fun getWeeklyAppUsageAverageInMinutes(context: Context): Int {
    val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    val cal = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }
    cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
    val endTimeMs = cal.timeInMillis
    cal.add(Calendar.DAY_OF_YEAR, -7)
    val startTimeMs = cal.timeInMillis

    val statsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, startTimeMs, endTimeMs)
    return (statsList.sumOf { it.totalTimeInForeground } / 7 / 1000 / 60).toInt()
}

fun filterDailyUsage(
    dailyUsage: List<DailyAppUsageData>,
    blockedApps: List<String>?,
    statsFilter: StatsFilter
): List<DailyAppUsageData> {
    return dailyUsage.map { usageData ->
        val filteredData = usageData.data.filter {
            when (statsFilter) {
                is StatsFilter.Custom -> it.packageName == statsFilter.packageName
                StatsFilter.BlockedApps -> blockedApps?.contains(it.packageName) == true
                else -> true
            }
        }
        val sum = filteredData.sumOf { it.timeInMinutes }
        usageData.copy(
            data = filteredData
                .filter { it.timeInMinutes >= 5 && (it.timeInMinutes.toFloat() / sum) >= 0.1 }
                .sortedBy { it.timeInMinutes },
            totalTimeInMinutes = sum
        )
    }
}