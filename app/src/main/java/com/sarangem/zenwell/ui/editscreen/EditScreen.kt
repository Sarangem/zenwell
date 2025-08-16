package com.sarangem.zenwell.ui.editscreen

import androidx.activity.compose.BackHandler
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
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.BlockType
import com.sarangem.zenwell.data.tables.Schedules
import com.sarangem.zenwell.ui.AppUiState
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    modifier: Modifier = Modifier,
    uiState: AppUiState,
    showTopAppBar: Boolean = true,
    updateUiState: (AppUiState) -> Unit = {},
    setAppNamesInUiState: () -> Unit = {},
    onSave: suspend () -> Unit = {},
    onDelete: suspend () -> Unit = {},
    goBack: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    var isSaving by rememberSaveable { mutableStateOf(false) }

    BackHandler { goBack() }

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
            val isError = uiState.isRunningTimeInvalid || uiState.isNotificationTimeInvalid
            SaveAndDeleteButton(
                modifier = Modifier.padding(
                    start = dimensionResource(R.dimen.padding_small),
                    end = dimensionResource(R.dimen.padding_small)
                ),
                onSave = {
                    if(!isError){
                        coroutineScope.launch(Dispatchers.IO) {
                            isSaving = true
                            onSave()
                            isSaving = false
                            withContext(Dispatchers.Main) {
                                goBack()
                            }
                        }
                    }
                },
                onDelete = {
                    if(!isError){
                        coroutineScope.launch(Dispatchers.IO) {
                            isSaving = true
                            onDelete()
                            isSaving = false
                            withContext(Dispatchers.Main) {
                                goBack()
                            }
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
            updateUiState = updateUiState,
            setAppNamesInUiState = setAppNamesInUiState,
            isSaving = isSaving
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EditScreenBody(
    modifier: Modifier = Modifier,
    uiState: AppUiState,
    updateUiState: (AppUiState) -> Unit = {},
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

            if(!uiState.schedule.isPomodoro){
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

            if (blockType == BlockType.Breathing) {

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

            if (blockType == BlockType.Wait ||
                blockType == BlockType.Breathing
            ) {

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

            if(!uiState.schedule.isPomodoro){
                ChooseNotificationTime(
                    notificationTime = uiState.schedule.notificationTimeInMinutes,
                    updateUiState = {
                        updateUiState(
                            uiState.copy(
                                schedule = uiState.schedule.copy(
                                    notificationTimeInMinutes = it
                                )
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

            if(uiState.schedule.isPomodoro){

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

            } else {
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


// -- Preview -- //
@Composable
fun EditScreenPreview() {
    EditScreen(
        uiState = AppUiState(
            schedule = Schedules(
                title = "Schedule 1",
                message = "This app is completely blocked.",
                blockType = BlockType.Breathing,
                startTimeInMinutes = 179,
                endTimeInMinutes = 1079,
                openTimeInMinutes = 10000,
                notificationTimeInMinutes = 10,
                breathingCycleDuration = 3,
                breathingCycleNumber = 2
            ),
        )
    )
}

@Preview(showBackground = true)
@Composable
fun EditScreenPreviewLightMode() {
    ZenwellTheme(darkTheme = false) {
        EditScreenPreview()
    }
}

@Preview
@Composable
fun EditScreenPreviewDarkMode() {
    ZenwellTheme(darkTheme = true) {
        EditScreenPreview()
    }
}