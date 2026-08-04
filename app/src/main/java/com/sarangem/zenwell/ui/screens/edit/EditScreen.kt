/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.sarangem.zenwell.database.tables.Schedules
import androidx.compose.ui.tooling.preview.Preview
import com.sarangem.zenwell.model.UnlockMethod
import com.sarangem.zenwell.ui.overlay.common.APP_BLOCKED
import com.sarangem.zenwell.ui.sequenceshowcase.LocalSequenceShowcaseState
import com.sarangem.zenwell.ui.theme.ZenwellTheme

@Composable
fun EditScreen(
    modifier: Modifier = Modifier,
    viewModel: EditViewModel,
    showTopAppBar: Boolean = true,
    setAsExistingUser: () -> Unit = {},
    firstEntry: Boolean = false,
    goBack: () -> Unit = {}
) {
    val showcaseState = LocalSequenceShowcaseState.current
    LaunchedEffect(firstEntry) {
        if (firstEntry) showcaseState.start(2)
    }
    val uiState by viewModel.uiState.collectAsState()
    EditScreenContents(
        modifier = modifier,
        uiState = uiState,
        updateSchedule = viewModel::updateSchedule,
        updateAppNames = viewModel::updateAppNames,
        showTopAppBar = showTopAppBar,
        saveToDatabase = viewModel::saveToDatabase,
        deleteSchedule = viewModel::deleteSchedule,
        firstEntry = firstEntry,
        setAsExistingUser = setAsExistingUser,
        userScrollEnabled = !showcaseState.showCaseVisible,
        goBack = goBack
    )
}


@Preview(showBackground = true)
@Composable
fun EditScreenPreview() {
    ZenwellTheme(darkTheme = false) {
        EditScreenContents(
            uiState = EditUiState(
                schedule = Schedules(
                    title = "Schedule 1",
                    message = APP_BLOCKED,
                    unlockMethod = UnlockMethod.MathProblem,
                    startTimeInMinutes = 179,
                    endTimeInMinutes = 1079,
                ),
                validationErrors = setOf(
                    ValidationError.ActiveTime,
                    ValidationError.NotificationTime
                )
            )
        )
    }
}