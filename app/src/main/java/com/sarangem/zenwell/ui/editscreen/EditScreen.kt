package com.sarangem.zenwell.ui.editscreen

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.BlockType
import com.sarangem.zenwell.data.tables.Schedules
import com.sarangem.zenwell.ui.AppUiState
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EditScreen(
    modifier: Modifier = Modifier,
    uiState: AppUiState,
    updateUiState: (AppUiState) -> Unit = {},
    saveToDatabase: suspend (Context) -> Unit = {},
    deleteSchedule: suspend (Context) -> Unit = {},
    goBack: () -> Unit = {}
) {
    BackHandler { goBack() }

    Column(modifier = modifier.fillMaxSize()) {

        ChooseEnable(
            checked = uiState.scheduleInfo.isEnabled,
            updateUiState = {
                updateUiState(
                    uiState.copy(
                        scheduleInfo = uiState.scheduleInfo.copy(
                            isEnabled = it
                        )
                    )
                )
            }
        )

        ChooseScheduleTitle(
            title = uiState.scheduleInfo.title,
            updateUiState = {
                updateUiState(
                    uiState.copy(
                        scheduleInfo = uiState.scheduleInfo.copy(
                            title = it
                        )
                    )
                )
            }
        )

        ChooseBlockType(
            blockType = uiState.scheduleInfo.blockType,
            updateUiState = {
                updateUiState(
                    uiState.copy(
                        scheduleInfo = uiState.scheduleInfo.copy(
                            blockType = it
                        )
                    )
                )
            }
        )

        ChooseAppList(
            checkedAppList = uiState.appNames,
            updateAppList = {
                updateUiState(
                    uiState.copy(
                        appNames = it
                    )
                )
            }
        )

        ChooseRunningTime(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)),
            startTimeInMinutes = uiState.scheduleInfo.startTimeInMinutes,
            updateStartTime = {
                updateUiState(
                    uiState.copy(
                        scheduleInfo = uiState.scheduleInfo.copy(
                            startTimeInMinutes = it
                        )
                    )
                )
            },
            endTimeInMinutes = uiState.scheduleInfo.endTimeInMinutes,
            updateEndTime = {
                updateUiState(
                    uiState.copy(
                        scheduleInfo = uiState.scheduleInfo.copy(
                            endTimeInMinutes = it
                        )
                    )
                )
            }
        )

        Spacer(Modifier.weight(1f))

        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        SaveAndDeleteButton(
            onSave = {
                coroutineScope.launch(Dispatchers.IO) {
                    saveToDatabase(context)
                    withContext(Dispatchers.Main){
                        goBack()
                    }
                }
            },
            onDelete = {
                coroutineScope.launch(Dispatchers.IO) {
                    deleteSchedule(context)
                    withContext(Dispatchers.Main){
                        goBack()
                    }
                }
            },
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_small))
                .fillMaxWidth()
        )
    }
}


// -- Preview -- //
@Composable
fun EditScreenPreview() {
    EditScreen(
        uiState = AppUiState(
            scheduleInfo = Schedules(
                title = "Schedule 1",
                blockType = BlockType.FullBlock,
                startTimeInMinutes = 179,
                endTimeInMinutes = 1079,
            ),
            appNames = listOf()
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