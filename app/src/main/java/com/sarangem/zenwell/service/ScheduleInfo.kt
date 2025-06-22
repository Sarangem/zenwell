package com.sarangem.zenwell.service

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.sarangem.zenwell.data.BlockType
import com.sarangem.zenwell.data.tables.Schedules
import com.sarangem.zenwell.service.blockingscreen.BlockingWindow
import com.sarangem.zenwell.service.blockingscreen.BreathingScreen
import com.sarangem.zenwell.service.blockingscreen.FullBlockScreen
import com.sarangem.zenwell.service.blockingscreen.WaitScreen
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ScheduleInfo(
    private val context: Context,
    var schedule: Schedules,
    val appSet: Set<String>,
) {

    private val onTimerEnd: () -> Unit = {

        // close the window
        val sr = AppBlockerService
        sr.instance?.closeWindow(schedule.id)

        // add to opened apps
        var previousApp = sr.instance?.previousApp
        if (previousApp != null) {
            sr.instance?.openedApps?.add(previousApp)
        }

        CoroutineScope(Dispatchers.IO).launch {

            // wait till open time
            delay(schedule.openTimeInMinutes * 60 * 1000L)
            sr.instance?.openedApps?.remove(previousApp)

            // re trigger opening window
            if (sr.instance?.previousApp == previousApp) {
                // the user is still on blocked app
                withContext(Dispatchers.Main) {
                    sr.instance?.recheckApp()
                }
            }

        }
    }

    val blockingWindow = BlockingWindow(
        context = context,
        content = { height, width ->
            ZenwellTheme {
                when (schedule.blockType) {

                    BlockType.FullBlock -> FullBlockScreen(
                        message = schedule.message,
                        modifier = Modifier.fillMaxSize(),
                        height = height,
                        width = width
                    )

                    BlockType.Wait -> WaitScreen(
                        modifier = Modifier.fillMaxSize(),
                        onTimerEnd = onTimerEnd,
                        waitTimeInSeconds = schedule.waitTimeInSeconds,
                        message = schedule.message,
                        showOpenDialog = schedule.waitEnterButton,
                        height = height,
                        width = width,
                    )

                    BlockType.Breathing -> BreathingScreen(
                        modifier = Modifier.fillMaxSize(),
                        onTimerEnd = onTimerEnd,
                        breathingCycleDuration = schedule.breathingCycleDuration,
                        breathingCycleNumber = schedule.breathingCycleNumber,
                        showOpenDialog = schedule.waitEnterButton,
                        message = schedule.message,
                        height = height,
                        width = width
                    )

                    else -> {}
                }
            }
        }
    )
}
