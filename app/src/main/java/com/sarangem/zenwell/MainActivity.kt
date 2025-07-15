package com.sarangem.zenwell

import android.os.Build
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

        // Add the notification channel
        createNotificationChannel(this)

        // show content
        enableEdgeToEdge()
        setContent {
            ZenwellTheme {
                ZenwellAppScreen(
                    startPermissionActivity = { intent ->
                        startActivity(intent)
                    },
                    shouldShowRequestPermissionRationale = { permission ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            shouldShowRequestPermissionRationale(permission)
                        }
                    }
                )
            }
        }

    }
}
