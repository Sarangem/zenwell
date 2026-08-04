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
import com.sarangem.zenwell.ui.screens.edit.fields.showcase2Modifier
import com.sarangem.zenwell.ui.screens.edit.fields.showcase3Modifier
import com.sarangem.zenwell.ui.screens.edit.fields.showcase4Modifier
import com.sarangem.zenwell.ui.sequenceshowcase.SequenceShowcaseScope
import com.sarangem.zenwell.ui.theme.ZenwellTheme

@Composable
fun SequenceShowcaseScope.EditScreen(
    modifier: Modifier = Modifier,
    viewModel: EditViewModel,
    showTopAppBar: Boolean = true,
    setAsExistingUser: () -> Unit = {},
    firstEntry: Boolean = false,
    goBack: () -> Unit = {}
) {
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
        showcase2Modifier = showcase2Modifier(setAsExistingUser),
        showcase2onClick = { showcaseState.dismiss() },
        showcase2onDismiss = { if (firstEntry) showcaseState.start(3) },
        showcase3Modifier = showcase3Modifier(setAsExistingUser),
        showcase3onClick = { showcaseState.dismiss() },
        showcase3onDismiss = { if (firstEntry) showcaseState.start(4) },
        showcase4Modifier = showcase4Modifier(setAsExistingUser),
        showcase4onClick = {
            showcaseState.dismiss()
            if (firstEntry) setAsExistingUser()
        },
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