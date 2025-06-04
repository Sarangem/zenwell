package com.sarangem.zenwell

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sarangem.zenwell.service.alarmer.ManageExactAlarms
import com.sarangem.zenwell.ui.ZenwellAppScreen
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZenwellTheme {
                ZenwellAppScreen(
                    startPermissionActivity = { intent ->
                        startActivity(intent)
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        CoroutineScope(Dispatchers.IO).launch{
            val alarmClass = ManageExactAlarms(
                context = applicationContext,
                schedulesList = (application as ZenwellApplication).container.getAllSchedules().first()
            )
            alarmClass.cancelAllExactAlarms()
            alarmClass.setExactAlarms()
        }
    }
}
