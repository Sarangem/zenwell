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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.BlockType
import com.sarangem.zenwell.data.database.tables.Schedules
import com.sarangem.zenwell.service.ui.APP_BLOCKED
import com.sarangem.zenwell.ui.AppViewModelProvider
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    modifier: Modifier = Modifier,
    schedule: Schedules,
    showTopAppBar: Boolean = true,
    goBack: () -> Unit = {}
) {
    val viewModel: EditViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(schedule) {
        viewModel.updateUiState(EditUiState(schedule = schedule))
    }

    val coroutineScope = rememberCoroutineScope()
    var isSaving by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            if (showTopAppBar) {
                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = goBack,
                            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.go_back)
                            )
                        }
                    },
                    title = {
                        Text(
                            text = stringResource(R.string.edit) + " " + uiState.schedule.title,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                )
            }
        },
        floatingActionButton = {
            val isError =
                uiState.isRunningTimeInvalid || uiState.isNotificationTimeInvalid || uiState.isPomodoroSessionNumberInvalid
            SaveAndDeleteButton(
                modifier = Modifier.padding(
                    start = dimensionResource(R.dimen.padding_small),
                    end = dimensionResource(R.dimen.padding_small)
                ),
                onSave = {
                    coroutineScope.launch(Dispatchers.IO) {
                        isSaving = true
                        viewModel.saveToDatabase()
                        isSaving = false
                        withContext(Dispatchers.Main) {
                            goBack()
                        }
                    }
                },
                onDelete = {
                    coroutineScope.launch(Dispatchers.IO) {
                        isSaving = true
                        viewModel.deleteSchedule()
                        isSaving = false
                        withContext(Dispatchers.Main) {
                            goBack()
                        }
                    }
                },
                isError = isError
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->

        EditScreenBody(
            modifier = Modifier.padding(innerPadding),
            uiState = uiState,
            updateSchedule = {
                viewModel.updateUiState(
                    uiState.copy(
                        schedule = it
                    )
                )
            },
            updateAppList = {
                viewModel.updateUiState(
                    uiState.copy(
                        appNames = it
                    )
                )
            },
            setAppNamesInUiState = { viewModel.setAppNamesInUiState() },
            isSaving = isSaving
        )

    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EditScreenBody(
    modifier: Modifier = Modifier,
    uiState: EditUiState,
    updateSchedule: (Schedules) -> Unit = {},
    updateAppList: (List<String>) -> Unit = {},
    setAppNamesInUiState: () -> Unit = {},
    isSaving: Boolean = false,
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        // enable switch
        ChooseEnable(
            checked = uiState.schedule.isEnabled,
            updateUiState = {
                updateSchedule(
                    uiState.schedule.copy(
                        isEnabled = it
                    )
                )
            },
        )

        StackedDetailsCard {

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
            updateAppList = updateAppList,
            setAppNamesInUiState = setAppNamesInUiState
        )

        StackedDetailsCard {

            if (!uiState.schedule.isPomodoro) {
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
            }

            AnimatedContent(
                targetState = uiState.schedule.blockType
            ) { blockType ->
                Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.card_elevation))) {

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

                        // breathing cycle duration
                        DetailsCardWithNumberField(
                            mainText = stringResource(R.string.breathing_cycle_duration),
                            textFieldValue = uiState.schedule.breathingCycleDuration,
                            updateSchedule = {
                                updateSchedule(
                                    uiState.schedule.copy(
                                        breathingCycleDuration = it
                                    )
                                )
                            },
                            suffixText = stringResource(R.string.seconds),
                            isStacked = true,
                        )

                        // breathing cycles number
                        DetailsCardWithNumberField(
                            mainText = stringResource(R.string.number_of_breathing_cycles),
                            textFieldValue = uiState.schedule.breathingCycleDuration,
                            updateSchedule = {
                                updateSchedule(
                                    uiState.schedule.copy(
                                        breathingCycleNumber = it
                                    )
                                )
                            },
                            isStacked = true,
                        )
                    }

                    if (blockType != BlockType.FullBlock) {

                        // open time
                        DetailsCardWithNumberField(
                            mainText = stringResource(R.string.open_time),
                            textFieldValue = uiState.schedule.openTimeInMinutes,
                            updateSchedule = {
                                updateSchedule(
                                    uiState.schedule.copy(
                                        openTimeInMinutes = it
                                    )
                                )
                            },
                            suffixText = stringResource(R.string.minutes),
                            isStacked = true,
                        )

                        // ask before opening
                        ChooseWaitEnterButton(
                            checked = uiState.schedule.waitEnterButton,
                            updateUiState = {
                                updateSchedule(
                                    uiState.schedule.copy(
                                        waitEnterButton = it
                                    )
                                )
                            }
                        )

                        // notification time
                        DetailsCardWithNumberField(
                            mainText = stringResource(R.string.send_notification_before_closing),
                            textFieldValue = uiState.schedule.notificationTimeInMinutes,
                            updateSchedule = {
                                updateSchedule(
                                    uiState.schedule.copy(
                                        notificationTimeInMinutes = it
                                    )
                                )
                            },
                            suffixText = stringResource(R.string.minutes),
                            isStacked = true,
                            isError = uiState.isNotificationTimeInvalid,
                            errorMessage = stringResource(R.string.notification_time_invalid)
                        )
                    }

                }
            }
        }

        if (uiState.schedule.isPomodoro) {

            StackedDetailsCard {

                // pomodoro work time
                DetailsCardWithNumberField(
                    mainText = stringResource(R.string.work_time),
                    textFieldValue = uiState.schedule.pomodoroWorkTimeInMinutes,
                    updateSchedule = {
                        updateSchedule(
                            uiState.schedule.copy(
                                pomodoroWorkTimeInMinutes = it
                            )
                        )
                    },
                    suffixText = stringResource(R.string.minutes),
                    isStacked = true
                )

                // pomodoro rest time
                DetailsCardWithNumberField(
                    mainText = stringResource(R.string.rest_time),
                    textFieldValue = uiState.schedule.pomodoroRestTimeInMinutes,
                    updateSchedule = {
                        updateSchedule(
                            uiState.schedule.copy(
                                pomodoroRestTimeInMinutes = it
                            )
                        )
                    },
                    suffixText = stringResource(R.string.minutes),
                    isStacked = true
                )

                // pomodoro sessions count
                DetailsCardWithNumberField(
                    mainText = stringResource(R.string.number_of_pomodoro_sessions),
                    textFieldValue = uiState.schedule.pomodoroSessionNumber,
                    updateSchedule = {
                        updateSchedule(
                            uiState.schedule.copy(
                                pomodoroSessionNumber = it
                            )
                        )
                    },
                    isError = uiState.isPomodoroSessionNumberInvalid,
                    errorMessage = stringResource(R.string.pomodoro_session_number_invalid),
                    isStacked = true
                )

                LabelDetailsCard(
                    mainText = stringResource(R.string.actions_to_show_in_work_time),
                    labelList = listOf(
                        LabelState(
                            title = stringResource(R.string.pause_resume),
                            isSelected = uiState.schedule.showPauseInWorkTime,
                            onSelectChange = {
                                updateSchedule(
                                    uiState.schedule.copy(
                                        showPauseInWorkTime = it
                                    )
                                )
                            }
                        ),
                        LabelState(
                            title = stringResource(R.string.skip),
                            isSelected = uiState.schedule.showSkipInWorkTime,
                            onSelectChange = {
                                updateSchedule(
                                    uiState.schedule.copy(
                                        showSkipInWorkTime = it
                                    )
                                )
                            }
                        )
                    )
                )
                LabelDetailsCard(
                    mainText = stringResource(R.string.actions_to_show_in_rest_time),
                    labelList = listOf(
                        LabelState(
                            title = stringResource(R.string.pause_resume),
                            isSelected = uiState.schedule.showPauseInRestTime,
                            onSelectChange = {
                                updateSchedule(
                                    uiState.schedule.copy(
                                        showPauseInRestTime = it
                                    )
                                )
                            }
                        ),
                        LabelState(
                            title = stringResource(R.string.skip),
                            isSelected = uiState.schedule.showSkipInRestTime,
                            onSelectChange = {
                                updateSchedule(
                                    uiState.schedule.copy(
                                        showSkipInRestTime = it
                                    )
                                )
                            }
                        )
                    )
                )
            }
        }

        if (!uiState.schedule.isPomodoro) {
            ChooseRunningTime(
                startTimeInMinutes = uiState.schedule.startTimeInMinutes,
                updateStartTime = {
                    updateSchedule(
                        uiState.schedule.copy(
                            startTimeInMinutes = it
                        )
                    )
                },
                endTimeInMinutes = uiState.schedule.endTimeInMinutes,
                updateEndTime = {
                    updateSchedule(
                        uiState.schedule.copy(
                            endTimeInMinutes = it
                        )
                    )
                },
                weekDays = uiState.schedule.weekDays,
                updateWeekDays = {
                    updateSchedule(
                        uiState.schedule.copy(
                            weekDays = it
                        )
                    )
                },
                isRunningTimeInvalid = uiState.isRunningTimeInvalid
            )
        }

        Spacer(Modifier.height(84.dp))

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

@Preview(showBackground = true)
@Composable
fun EditScreenPreviewLightMode() {
    ZenwellTheme(darkTheme = false) {
        EditScreenBody(
            uiState = EditUiState(
                schedule = Schedules(
                    title = "Schedule 1",
                    message = APP_BLOCKED,
                    blockType = BlockType.Breathing,
                    startTimeInMinutes = 179,
                    endTimeInMinutes = 1079,
                ),
            )
        )
    }
}

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