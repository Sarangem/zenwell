/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.stats

import android.app.usage.UsageStatsManager
import android.content.Context
import androidx.compose.ui.graphics.Color
import android.graphics.drawable.Drawable
import java.util.Calendar

data class AppUsageData(
    val packageName: String,
    val timeInMinutes: Int,
    val appName: String? = null, // only for UsageBarGraph()
    val icon: Drawable? = null, // only for UsageBarGraph()
    val iconColor: Color = Color.Gray // only for UsageBarGraph()
)

data class DailyAppUsageData(
    val weekDay: Int,
    val weekDayName: String? = null, // only for UsageBarGraph()
    val data: List<AppUsageData>,
    val totalTimeInMinutes: Int = 0 // only for UsageBarGraph()
)

fun getDailyAppUsage(context: Context): List<DailyAppUsageData> {
    val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    val cal = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }
    cal.add(Calendar.DAY_OF_YEAR, 1)
    val endTimeMs = cal.timeInMillis
    cal.add(Calendar.DAY_OF_YEAR, -8)
    val startTimeMs = cal.timeInMillis

    val statsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTimeMs, endTimeMs)
    val usageByDay = statsList
        ?.filter { it.totalTimeInForeground > 0 }
        ?.groupBy { Calendar.getInstance().apply { timeInMillis = it.firstTimeStamp }.get(Calendar.DAY_OF_YEAR) }
        ?.mapValues { (_, stats) ->
            stats.groupBy { it.packageName }
                .map { (pkg, appStats) ->
                    AppUsageData(
                        pkg,
                        (appStats.sumOf { it.totalTimeInForeground } / 1000 / 60).toInt()
                    )
                }
        } ?: emptyMap()
    return (0..7).map { daysIntoPast ->
        val targetCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -daysIntoPast) }
        val data = usageByDay[targetCal.get(Calendar.DAY_OF_YEAR)] ?: emptyList()
        DailyAppUsageData(
            weekDay = targetCal.get(Calendar.DAY_OF_WEEK),
            data = data
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