package com.sarangem.zenwell.ui.editscreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.BlockType
import com.sarangem.zenwell.data.tables.Schedules
import com.sarangem.zenwell.ui.AppUiState
import com.sarangem.zenwell.ui.theme.ZenwellTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EditScreen(
    modifier: Modifier = Modifier,
    uiState: AppUiState,
    updateUiState: (AppUiState) -> Unit = {},
    setAppNamesInUiState: () -> Unit = {},
    isSaving: Boolean = false,
    goBack: () -> Unit = {}
) {
    BackHandler { goBack() }

    Box(modifier = modifier) {

        Column(
            modifier = Modifier
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

            val blockType = uiState.schedule.blockType

            if (blockType == BlockType.Wait){

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
                }
            )

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
                breathingCycleDuration = 3,
                breathingCycleNumber = 2
            ),
            appNames = listOf()
        )
    )
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
fun EditScreenPreviewLightMode() {
    ZenwellTheme(darkTheme = false) {
        EditScreenPreview()
    }
}

@Preview(heightDp = 1000)
@Composable
fun EditScreenPreviewDarkMode() {
    ZenwellTheme(darkTheme = true) {
        EditScreenPreview()
    }
}