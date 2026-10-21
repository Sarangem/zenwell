/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.edit

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.sarangem.zenwell.model.Schedules
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sarangem.zenwell.model.UnlockMethod
import com.sarangem.zenwell.ui.overlay.common.APP_BLOCKED
import com.sarangem.zenwell.ui.common.sequenceshowcase.LocalSequenceShowcaseState
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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val showcaseState = LocalSequenceShowcaseState.current
    LaunchedEffect(firstEntry) {
        if (firstEntry) showcaseState.start(2)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    EditScreenContents(
        modifier = modifier,
        uiState = uiState,
        updateSchedule = viewModel::updateSchedule,
        updateAppNames = viewModel::updateSelectedAppsList,
        showTopAppBar = showTopAppBar,
        saveToDatabase = {
            viewModel.saveToDatabase()
            if (uiState.schedule.notificationTimeInMinutes != 0 &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        },
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
                validationErrors = setOf(ValidationError.NotificationTime)
            )
        )
    }
}