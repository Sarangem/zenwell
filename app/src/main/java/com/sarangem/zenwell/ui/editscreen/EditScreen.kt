package com.sarangem.zenwell.ui.editscreen

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.BlockType
import com.sarangem.zenwell.data.tables.Schedules
import com.sarangem.zenwell.ui.ZenwellAppViewModelProvider
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@Composable
fun EditScreen(
    scheduleId: Int,
    modifier: Modifier = Modifier,
    editScreenViewModel: EditScreenViewModel = viewModel(factory = ZenwellAppViewModelProvider.Factory),
    goBack: () -> Unit = {}
) {

    val editScreenUiState by editScreenViewModel.uiState.collectAsState()

    EditScreenBody(
        modifier = modifier,
        editScreenUiState = editScreenUiState,
        updateUiState = {
            editScreenViewModel.updateUiState(it)
        },
        saveToDatabase = {
            editScreenViewModel.saveToDatabase(context = it)
        },
        deleteSchedule = {
            editScreenViewModel.deleteSchedule(context = it)
        },
        goBack = goBack
    )

    LaunchedEffect(scheduleId) {
        initUiState(
            scheduleId = scheduleId,
            coroutineScope = this,
            editScreenViewModel = editScreenViewModel
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EditScreenBody(
    modifier: Modifier = Modifier,
    editScreenUiState: EditScreenUiState,
    updateUiState: (EditScreenUiState) -> Unit = {},
    saveToDatabase: suspend (Context) -> Unit = {},
    deleteSchedule: suspend (Context) -> Unit = {},
    goBack: () -> Unit = {}
) {

    Scaffold(
        modifier = modifier,
        topBar = {
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
                        text = stringResource(R.string.edit) + " " + editScreenUiState.scheduleInfo.title,
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {

            ChooseScheduleTitle(
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)),
                title = editScreenUiState.scheduleInfo.title,
                updateUiState = {
                    updateUiState(
                        editScreenUiState.copy(
                            scheduleInfo = editScreenUiState.scheduleInfo.copy(
                                title = it
                            )
                        )
                    )
                }
            )

            ChooseBlockType(
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)),
                blockType = editScreenUiState.scheduleInfo.blockType,
                updateUiState = {
                    updateUiState(
                        editScreenUiState.copy(
                            scheduleInfo = editScreenUiState.scheduleInfo.copy(
                                blockType = it
                            )
                        )
                    )
                }
            )

            ChooseAppList(
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)),
                checkedAppList = editScreenUiState.appNames,
                updateAppList = {
                    updateUiState(
                        editScreenUiState.copy(
                            appNames = it
                        )
                    )
                }
            )

            ChooseRunningTime(
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)),
                startTimeInMinutes = editScreenUiState.scheduleInfo.startTimeInMinutes,
                updateStartTime = {
                    updateUiState(
                        editScreenUiState.copy(
                            scheduleInfo = editScreenUiState.scheduleInfo.copy(
                                startTimeInMinutes = it
                            )
                        )
                    )
                },
                endTimeInMinutes = editScreenUiState.scheduleInfo.endTimeInMinutes,
                updateEndTime = {
                    updateUiState(
                        editScreenUiState.copy(
                            scheduleInfo = editScreenUiState.scheduleInfo.copy(
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
                    }
                    goBack()
                },
                onDelete = {
                    coroutineScope.launch(Dispatchers.IO) {
                        deleteSchedule(context)
                    }
                    goBack()
                },
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_small))
                    .fillMaxWidth()
            )
        }
    }
}

fun initUiState(
    scheduleId: Int,
    coroutineScope: CoroutineScope,
    editScreenViewModel: EditScreenViewModel
) {
    val scheduleInfo = coroutineScope.async(Dispatchers.IO) {
        editScreenViewModel.getScheduleInfo(scheduleId).firstOrNull()
    }
    val appNames = coroutineScope.async(Dispatchers.IO) {
        editScreenViewModel.getAppNames(scheduleId).first()
    }

    coroutineScope.launch(Dispatchers.IO) {
        editScreenViewModel.updateUiState(
            EditScreenUiState(
                scheduleInfo = scheduleInfo.await() ?: Schedules(),
                appNames = appNames.await()
            )
        )
        editScreenViewModel.isInitialized = true
        editScreenViewModel.pastAppList = appNames.await().toMutableList()
    }
}


// -- Preview -- //
@Composable
fun EditScreenPreview() {
    EditScreenBody(
        editScreenUiState = EditScreenUiState(
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

@Preview
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