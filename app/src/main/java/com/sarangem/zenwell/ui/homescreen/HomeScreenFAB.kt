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
import com.sarangem.zenwell.ui.AppUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreenFAB(
    uiState: AppUiState,
    addNewSchedule: suspend () -> Int = suspend { 0 },
    openEditScreen: (Int) -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()

    if (uiState.isShowingHomePage) {
        ExtendedFloatingActionButton(
            onClick = {
                coroutineScope.launch(Dispatchers.IO) {
                    val scheduleId = addNewSchedule()
                    withContext(Dispatchers.Main) {
                        openEditScreen(scheduleId)
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
}