package com.sarangem.zenwell.ui.editscreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.BlockType
import com.sarangem.zenwell.data.database.tables.Schedules
import com.sarangem.zenwell.service.overlay.common.APP_BLOCKED
import com.sarangem.zenwell.ui.editscreen.details.DetailsCardColumn
import com.sarangem.zenwell.ui.editscreen.details.DetailsCardWithNumberField
import com.sarangem.zenwell.ui.editscreen.details.DetailsCardWithSwitch
import com.sarangem.zenwell.ui.editscreen.details.DetailsCardWithTextField
import com.sarangem.zenwell.ui.editscreen.fields.ChooseAppList
import com.sarangem.zenwell.ui.editscreen.fields.ChooseBlockType
import com.sarangem.zenwell.ui.editscreen.sections.BreathingDetailsSection
import com.sarangem.zenwell.ui.editscreen.sections.ChooseRunningTime
import com.sarangem.zenwell.ui.editscreen.sections.MathEquationDetailsSection
import com.sarangem.zenwell.ui.editscreen.sections.PomodoroDetailsSection
import com.sarangem.zenwell.ui.editscreen.sections.WaitDetailsSection
import com.sarangem.zenwell.ui.theme.ZenwellTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EditScreenBody(
    modifier: Modifier = Modifier,
    uiState: EditUiState,
    updateSchedule: (Schedules) -> Unit = {},
    updateAppList: (List<String>) -> Unit = {},
    isSaving: Boolean = false,
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        // enable switch
        DetailsCardWithSwitch(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)),
            mainText = stringResource(R.string.enable),
            checked = uiState.schedule.isEnabled,
            onCheckedChange = {
                updateSchedule(
                    uiState.schedule.copy(
                        isEnabled = it
                    )
                )
            },
            motionScheme = MotionScheme.expressive()
        )

        DetailsCardColumn {

            // title
            DetailsCardWithTextField(
                mainText = stringResource(R.string.schedule_title),
                isStacked = true,
                textFieldValue = uiState.schedule.title,
                onValueChange = {
                    updateSchedule(
                        uiState.schedule.copy(
                            title = it
                        )
                    )
                },
            )

            // message
            DetailsCardWithTextField(
                mainText = stringResource(R.string.message),
                isStacked = true,
                textFieldValue = uiState.schedule.message,
                onValueChange = {
                    updateSchedule(
                        uiState.schedule.copy(
                            message = it
                        )
                    )
                },
            )
        }

        ChooseAppList(
            checkedAppList = uiState.appNames,
            updateAppList = updateAppList
        )

        if (!uiState.schedule.isPomodoro) {
            DetailsCardColumn {

                // block type
                ChooseBlockType(
                    blockType = uiState.schedule.blockType,
                    updateUiState = {
                        updateSchedule(
                            uiState.schedule.copy(
                                blockType = it
                            )
                        )
                    }
                )

                AnimatedContent(
                    targetState = uiState.schedule.blockType
                ) { blockType ->
                    Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))) {

                        if (blockType == BlockType.Wait) {
                            // wait time
                            DetailsCardWithNumberField(
                                mainText = stringResource(R.string.wait_time),
                                textFieldValue = uiState.schedule.waitTimeInSeconds,
                                updateSchedule = {
                                    updateSchedule(
                                        uiState.schedule.copy(
                                            waitTimeInSeconds = it
                                        )
                                    )
                                },
                                suffixText = stringResource(R.string.seconds),
                                isStacked = true,
                            )
                        }

                        if (blockType == BlockType.Breathing) {
                            BreathingDetailsSection(
                                schedule = uiState.schedule,
                                updateSchedule = updateSchedule
                            )
                        }

                        if (blockType == BlockType.MathEquation) {
                            MathEquationDetailsSection(
                                schedule = uiState.schedule,
                                validationErrors = uiState.validationErrors,
                                updateSchedule = updateSchedule
                            )
                        }

                        if (blockType != BlockType.FullBlock) {
                            WaitDetailsSection(
                                schedule = uiState.schedule,
                                validationErrors = uiState.validationErrors,
                                updateSchedule = updateSchedule
                            )
                        }

                    }
                }
            }
        }

        if (uiState.schedule.isPomodoro) {
            PomodoroDetailsSection(
                schedule = uiState.schedule,
                validationErrors = uiState.validationErrors,
                updateSchedule = updateSchedule
            )
        }

        if (!uiState.schedule.isPomodoro) {
            ChooseRunningTime(
                schedule = uiState.schedule,
                validationError = uiState.validationErrors,
                updateSchedule = updateSchedule
            )
        }

        Spacer(Modifier.height(dimensionResource(R.dimen.floating_action_button_height)))

    }

    if (isSaving) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LoadingIndicator(Modifier.fillMaxWidth())
        }
    }


}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
fun EditScreenPreviewLightMode() {
    ZenwellTheme(darkTheme = false) {
        EditScreenBody(
            uiState = EditUiState(
                schedule = Schedules(
                    title = "Schedule 1",
                    message = APP_BLOCKED,
                    blockType = BlockType.MathEquation,
                    startTimeInMinutes = 179,
                    endTimeInMinutes = 1079,
                ),
                validationErrors = setOf(
                    ValidationError.RunningTime,
                    ValidationError.MathOperators,
                    ValidationError.NotificationTime
                )
            )
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun EditScreenPreviewDarkMode() {
    ZenwellTheme(darkTheme = true) {
        EditScreenBody(
            uiState = EditUiState(
                schedule = Schedules(
                    title = "Pomodoro Mode",
                    message = APP_BLOCKED,
                    isPomodoro = true
                ),
            )
        )
    }
}