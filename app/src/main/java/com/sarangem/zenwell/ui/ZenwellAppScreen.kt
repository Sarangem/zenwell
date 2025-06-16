package com.sarangem.zenwell.ui

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.tables.Schedules
import com.sarangem.zenwell.ui.editscreen.EditScreen
import com.sarangem.zenwell.ui.editscreen.SaveAndDeleteButton
import com.sarangem.zenwell.ui.homescreen.HomeScreen
import com.sarangem.zenwell.ui.homescreen.NewScheduleFAB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZenwellAppScreen(
    startPermissionActivity: (Intent) -> Unit = {}
) {
    val viewModel: ZenwellAppViewModel = viewModel(factory = ZenwellAppViewModel.factory)
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isSaving by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ZenwellTopBar(
                uiState = uiState,
                goBack = { goToHome(viewModel) }
            )
        },
        bottomBar = {
            if (!uiState.isShowingHomePage) {
                SaveAndDeleteButton(
                    onSave = {
                        coroutineScope.launch(Dispatchers.IO) {
                            isSaving = true
                            viewModel.saveToDatabase()
                            isSaving = false
                            withContext(Dispatchers.Main) {
                                goToHome(viewModel)
                            }
                        }
                    },
                    onDelete = {
                        coroutineScope.launch(Dispatchers.IO) {
                            isSaving = true
                            viewModel.deleteSchedule()
                            isSaving = false
                            withContext(Dispatchers.Main) {
                                goToHome(viewModel)
                            }
                        }
                    },
                )
            }
        },
        floatingActionButton = {
            if (uiState.isShowingHomePage) {
                NewScheduleFAB(
                    addNewSchedule = {
                        viewModel.addNewSchedule(context)
                    },
                    openEditScreen = { goToEdit(viewModel, uiState, it) },
                    coroutineScope = coroutineScope
                )
            }
        }
    ) { innerPadding ->

        if (uiState.isShowingHomePage) {

            val schedulesList by viewModel.getAllSchedules().collectAsState(emptyList())
            HomeScreen(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                schedulesList = schedulesList,
                openEditScreen = { goToEdit(viewModel, uiState, it) },
                startPermissionActivity = startPermissionActivity,
            )

        } else {

            LaunchedEffect(Unit) {
                viewModel.initUiState()
            }
            
            EditScreen(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                uiState = uiState,
                updateUiState = { viewModel.updateUiState(it) },
                goBack = { goToHome(viewModel) },
                isSaving = isSaving
            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZenwellTopBar(
    uiState: AppUiState,
    goBack: () -> Unit = {},
    openSettingsScreen: (Int) -> Unit = {},
) {
    TopAppBar(
        navigationIcon = {
            if (!uiState.isShowingHomePage) {
                IconButton(
                    onClick = goBack,
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.go_back)
                    )
                }
            }
        },
        title = {
            Text(
                text = if (uiState.isShowingHomePage) {
                    stringResource(id = R.string.app_name)
                } else {
                    stringResource(R.string.edit) + " " + uiState.schedule.title
                },
                style = MaterialTheme.typography.headlineSmall
            )
        },
    )
}


// UI SCREEN TRANSITION //

fun goToHome(
    viewModel: ZenwellAppViewModel
) {
    viewModel.updateUiState(
        AppUiState()
    )
}

fun goToEdit(
    viewModel: ZenwellAppViewModel,
    uiState: AppUiState,
    schedule: Schedules
) {
    viewModel.updateUiState(
        uiState.copy(
            isShowingHomePage = false,
            schedule = schedule,
        )
    )
}