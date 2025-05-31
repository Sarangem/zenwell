package com.sarangem.zenwell

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sarangem.zenwell.ui.ZenwellApp
import com.sarangem.zenwell.ui.theme.ZenwellTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZenwellTheme {
                ZenwellApp(
                    startPermissionActivity = { intent ->
                        startActivity(intent)
                    }
                )
            }
        }
    }
}