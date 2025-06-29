package com.sarangem.zenwell.ui.homescreen

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.tables.Schedules
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun NewScheduleFAB(
    addNewSchedule: suspend () -> Schedules = suspend { Schedules() },
    openEditScreen: (Schedules) -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()

    ExtendedFloatingActionButton(
        onClick = {
            coroutineScope.launch(Dispatchers.IO) {

                val newSchedule = addNewSchedule()
                withContext(Dispatchers.Main) {
                    openEditScreen(newSchedule)
                }

            }
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
        },
        text = {
            Text(
                text = stringResource(R.string.new_schedule)
            )
        },
        modifier = Modifier
            .padding(
                end = WindowInsets.safeDrawing.asPaddingValues()
                    .calculateEndPadding(LocalLayoutDirection.current)
            )
    )
}
