/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.sarangem.zenwell.lock.AppPasswordLock
import com.sarangem.zenwell.ui.screens.ZenwellAppScreen
import com.sarangem.zenwell.ui.screens.lock.AppLockScreen
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import com.sarangem.zenwell.service.notifications.createNotificationChannel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(this)
        enableEdgeToEdge()
        AppPasswordLock.syncOnLaunch(this)
        setContent {
            ZenwellTheme {
                Box(Modifier.fillMaxSize()) {
                    ZenwellAppScreen()
                    if (AppPasswordLock.isLocked) AppLockScreen()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (!isChangingConfigurations) AppPasswordLock.lockIfEnabled(this)
    }
}
