package com.sarangem.zenwell

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sarangem.zenwell.ui.ZenwellAppScreen
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import com.sarangem.zenwell.utils.createNotificationChannel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(this)
        enableEdgeToEdge()
        setContent {
            ZenwellTheme {
                ZenwellAppScreen(
                )
            }
        }
    }
}
