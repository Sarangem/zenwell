package com.sarangem.zenwell.ui.screens.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.ui.screens.AppViewModelProvider
import androidx.compose.ui.tooling.preview.Preview
import com.sarangem.zenwell.model.UnlockMethod
import com.sarangem.zenwell.ui.overlay.common.APP_BLOCKED
import com.sarangem.zenwell.ui.theme.ZenwellTheme

@Composable
fun EditScreen(
    modifier: Modifier = Modifier,
    schedule: Schedules,
    showTopAppBar: Boolean = true,
    goBack: () -> Unit = {}
) {
    val viewModel: EditViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(schedule.id) {
        viewModel.initialize(schedule)
    }

    EditScreenContents(
        modifier = modifier,
        uiState = uiState,
        updateSchedule = viewModel::updateSchedule,
        updateAppNames = viewModel::updateAppNames,
        showTopAppBar = showTopAppBar,
        saveToDatabase = viewModel::saveToDatabase,
        deleteSchedule = viewModel::deleteSchedule,
        goBack = {
            goBack()
            viewModel.emptyUiState()
        }
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