package com.sarangem.zenwell.ui.editscreen

import androidx.compose.animation.AnimatedVisibility
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
            updateUiState = {
                viewModel.updateUiState(it)
            },
            setAppNamesInUiState = { viewModel.setAppNamesInUiState() },
            isSaving = isSaving
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EditScreenBody(
    modifier: Modifier = Modifier,
    uiState: EditUiState,
    updateUiState: (EditUiState) -> Unit = {},
    setAppNamesInUiState: () -> Unit = {},
    isSaving: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        ChooseEnable(
            checked = uiState.schedule.isEnabled,
            updateUiState = {
                updateUiState(
                    uiState.copy(
                        schedule = uiState.schedule.copy(
                            isEnabled = it
                        )
                    )
                )
            },
        )

        ChooseScheduleTitle(
            title = uiState.schedule.title,
            updateUiState = {
                updateUiState(
                    uiState.copy(
                        schedule = uiState.schedule.copy(
                            title = it
                        )
                    )
                )
            }
        )
        ChooseMessage(
            message = uiState.schedule.message,
            updateUiState = {
                updateUiState(
                    uiState.copy(
                        schedule = uiState.schedule.copy(
                            message = it
                        )
                    )
                )
            }
        )

        if (!uiState.schedule.isPomodoro) {
            ChooseBlockType(
                blockType = uiState.schedule.blockType,
                updateUiState = {
                    updateUiState(
                        uiState.copy(
                            schedule = uiState.schedule.copy(
                                blockType = it
                            )
                        )
                    )
                }
            )
        }

        val blockType = uiState.schedule.blockType

        if (blockType == BlockType.Wait) {

            ChooseWaitTime(
                waitTimeInSeconds = uiState.schedule.waitTimeInSeconds,
                updateUiState = {
                    updateUiState(
                        uiState.copy(
                            schedule = uiState.schedule.copy(
                                waitTimeInSeconds = it
                            )
                        )
                    )
                }
            )

        }

        AnimatedVisibility(blockType == BlockType.Breathing) {
            Column {

                ChooseBreathingCycleDuration(
                    breathingCycleDuration = uiState.schedule.breathingCycleDuration,
                    updateUiState = {
                        updateUiState(
                            uiState.copy(
                                schedule = uiState.schedule.copy(
                                    breathingCycleDuration = it
                                )
                            )
                        )
                    }
                )

                ChooseBreathingCycleNumber(
                    breathingCycleNumber = uiState.schedule.breathingCycleNumber,
                    updateUiState = {
                        updateUiState(
                            uiState.copy(
                                schedule = uiState.schedule.copy(
                                    breathingCycleNumber = it
                                )
                            )
                        )
                    }
                )

            }
        }

        AnimatedVisibility(
            blockType == BlockType.Wait ||
                    blockType == BlockType.Breathing
        ) {
            Column {

                ChooseOpenTime(
                    openTimeInMinutes = uiState.schedule.openTimeInMinutes,
                    updateUiState = {
                        updateUiState(
                            uiState.copy(
                                schedule = uiState.schedule.copy(
                                    openTimeInMinutes = it
                                )
                            )
                        )
                    }
                )

                ChooseWaitEnterButton(
                    checked = uiState.schedule.waitEnterButton,
                    updateUiState = {
                        updateUiState(
                            uiState.copy(
                                schedule = uiState.schedule.copy(
                                    waitEnterButton = it
                                )
                            )
                        )
                    }
                )


            }
        }

        AnimatedVisibility(!uiState.schedule.isPomodoro && blockType != BlockType.FullBlock) {
            ChooseNotificationTime(
                notificationTime = uiState.schedule.notificationTimeInMinutes,
                updateUiState = {
                    updateUiState(
                        uiState.copy(
                            schedule = uiState.schedule.copy(
                                notificationTimeInMinutes = it
                            ),
                        )
                    )
                },
                isNotificationTimeInvalid = uiState.isNotificationTimeInvalid
            )
        }

        ChooseAppList(
            checkedAppList = uiState.appNames,
            updateAppList = {
                updateUiState(
                    uiState.copy(
                        appNames = it
                    )
                )
            },
            setAppNamesInUiState = setAppNamesInUiState
        )

        if (uiState.schedule.isPomodoro) {

            ChoosePomodoroWorkTime(
                workTimeInMinutes = uiState.schedule.pomodoroWorkTimeInMinutes,
                updateUiState = {
                    updateUiState(
                        uiState.copy(
                            schedule = uiState.schedule.copy(
                                pomodoroWorkTimeInMinutes = it
                            )
                        )
                    )
                }
            )

            ChoosePomodoroRestTime(
                restTimeInMinutes = uiState.schedule.pomodoroRestTimeInMinutes,
                updateUiState = {
                    updateUiState(
                        uiState.copy(
                            schedule = uiState.schedule.copy(
                                pomodoroRestTimeInMinutes = it
                            )
                        )
                    )
                }
            )

            ChoosePomodoroSessionNumber(
                pomodoroSessionNumber = uiState.schedule.pomodoroSessionNumber,
                updateUiState = {
                    updateUiState(
                        uiState.copy(
                            schedule = uiState.schedule.copy(
                                pomodoroSessionNumber = it
                            ),
                        )
                    )
                },
                isPomodoroSessionNumberInvalid = uiState.isPomodoroSessionNumberInvalid
            )

        }

        if (!uiState.schedule.isPomodoro) {
            ChooseRunningTime(
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)),
                startTimeInMinutes = uiState.schedule.startTimeInMinutes,
                updateStartTime = {
                    updateUiState(
                        uiState.copy(
                            schedule = uiState.schedule.copy(
                                startTimeInMinutes = it
                            )
                        )
                    )
                },
                endTimeInMinutes = uiState.schedule.endTimeInMinutes,
                updateEndTime = {
                    updateUiState(
                        uiState.copy(
                            schedule = uiState.schedule.copy(
                                endTimeInMinutes = it
                            )
                        )
                    )
                },
                weekDays = uiState.schedule.weekDays,
                updateWeekDays = {
                    updateUiState(
                        uiState.copy(
                            schedule = uiState.schedule.copy(
                                weekDays = it
                            )
                        )
                    )
                },
                isRunningTimeInvalid = uiState.isRunningTimeInvalid
            )
        }

        if(uiState.schedule.isPomodoro){
            ChoosePomodoroActionsToShow(
                showPauseInWorkTime = uiState.schedule.showPauseInWorkTime,
                updateShowPauseInWorkTime = {
                    updateUiState(
                        uiState.copy(
                            schedule = uiState.schedule.copy(
                                showPauseInWorkTime = it
                            )
                        )
                    )
                },
                showSkipInWorkTime = uiState.schedule.showSkipInWorkTime,
                updateShowSkipInWorkTime = {
                    updateUiState(
                        uiState.copy(
                            schedule = uiState.schedule.copy(
                                showSkipInWorkTime = it
                            )
                        )
                    )
                },
                showPauseInRestTime = uiState.schedule.showPauseInRestTime,
                updateShowPauseInRestTime = {
                    updateUiState(
                        uiState.copy(
                            schedule = uiState.schedule.copy(
                                showPauseInRestTime = it
                            )
                        )
                    )
                },
                showSkipInRestTime = uiState.schedule.showSkipInRestTime,
                updateShowSkipInRestTime = {
                    updateUiState(
                        uiState.copy(
                            schedule = uiState.schedule.copy(
                                showSkipInWorkTime = it
                            )
                        )
                    )
                }
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