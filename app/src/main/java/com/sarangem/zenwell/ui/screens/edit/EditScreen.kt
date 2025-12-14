package com.sarangem.zenwell.ui.screens.edit

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sarangem.zenwell.R
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.ui.screens.AppViewModelProvider
import com.sarangem.zenwell.ui.screens.edit.fields.SaveAndDeleteButton
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
        launch(Dispatchers.IO) {

            val appNames = viewModel.getAppNames(schedule.id)
            withContext(Dispatchers.Main) {
                viewModel.updateUiState(EditUiState(schedule, appNames))
            }
            viewModel.pastAppList = appNames

        }
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
                            style = MaterialTheme.typography.headlineSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                )
            }
        },
        floatingActionButton = {
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
                isError = uiState.validationErrors.isNotEmpty()
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
            isSaving = isSaving
        )

    }
}