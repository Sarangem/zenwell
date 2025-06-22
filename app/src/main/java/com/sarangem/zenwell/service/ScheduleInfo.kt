package com.sarangem.zenwell.service

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.sarangem.zenwell.data.BlockType
import com.sarangem.zenwell.data.tables.Schedules
import com.sarangem.zenwell.getCurrentTimeInMinutes
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

    private val TAG = AppBlockerService.instance?.TAG
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val onTimerEnd: () -> Unit = fn@{

        // close the window
        var instance: AppBlockerService = AppBlockerService.instance ?: return@fn
        instance.closeWindow(schedule.id)

        // add to opened apps
        var previousApp = instance.previousApp
        if (previousApp != null) {
            instance.openedApps.add(previousApp)
        }
        Log.d(TAG, "Adding to openedApps: ${instance.openedApps}")

        coroutineScope.launch {

            // wait till open time
            val delayTime = if(getCurrentTimeInMinutes() + schedule.openTimeInMinutes > schedule.endTimeInMinutes){
                schedule.endTimeInMinutes - getCurrentTimeInMinutes()
            } else {
                schedule.openTimeInMinutes
            }
            if (delayTime < 0) return@launch
            delay(delayTime * 60 * 1000L)
            Log.d(TAG, "Rechecking the app.")

            // reinitialize instance
            instance = AppBlockerService.instance ?: return@launch

            instance.openedApps.remove(previousApp)

            // re trigger opening window
            if (instance.previousApp == previousApp) {
                // the user is still on blocked app
                withContext(Dispatchers.Main) {
                    instance.recheckApp()
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
