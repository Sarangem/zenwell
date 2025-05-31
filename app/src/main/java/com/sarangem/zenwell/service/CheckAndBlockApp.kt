package com.sarangem.zenwell.service

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.sarangem.zenwell.data.BlockType
import com.sarangem.zenwell.data.tables.Schedules
import com.sarangem.zenwell.getCurrentTimeInMinutes
import com.sarangem.zenwell.service.blockingscreen.BlockingWindow
import com.sarangem.zenwell.service.blockingscreen.FullBlockScreen
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend fun checkAndBlockApp(
    context: Context,
    schedule: Schedules?,
    appList: List<String>,
    coroutineScope: CoroutineScope
) {
    val TAG = "Check&BlockApp"

    Log.d(TAG, "$schedule and $appList")
    if (schedule == null) {
        return
    }
    if (appList.isEmpty()) {
        return
    }

    val blockingWindow = BlockingWindow(
        context = context,
        content = { height,width ->
            ZenwellTheme {
                when (schedule.blockType) {
                    BlockType.FullBlock -> FullBlockScreen(
                        modifier = Modifier.fillMaxSize(),
                        height = height,
                        width = width
                    )
                    else -> {}
                }
            }
        }
    )

    var foregroundApp: String? = null
    var previousApp: String? = null
    var previousAppWasBlocked = false

    while (getCurrentTimeInMinutes() < schedule.endTimeInMinutes) {

        foregroundApp = getForegroundApp(context)

        if (previousApp != foregroundApp) {

            if (previousAppWasBlocked) {
                coroutineScope.launch(Dispatchers.Main) {
                    Log.d(TAG, "Closing window")
                    blockingWindow.close()
                }
            }

            if (foregroundApp in appList) {
                previousAppWasBlocked = true
                coroutineScope.launch(Dispatchers.Main) {
                    Log.d(TAG, "Opening window")
                    blockingWindow.open()
                }
            } else {
                previousAppWasBlocked = false
            }
        }

        previousApp = foregroundApp

        // don't check too fast
        delay(1000L)
    }
}

fun getForegroundApp(context: Context): String? {
    var foregroundApp: String? = null
    val usageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    val time = System.currentTimeMillis()
    val usageEvents = usageStatsManager.queryEvents(time - 1000 * 3600, time)
    val event = UsageEvents.Event()

    while (usageEvents.hasNextEvent()) {
        usageEvents.getNextEvent(event)
        if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
            foregroundApp = event.packageName
        }
    }
    return foregroundApp
}