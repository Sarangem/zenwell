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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.tables.Schedules
import com.sarangem.zenwell.ui.editscreen.EditScreen
import com.sarangem.zenwell.ui.homescreen.HomeScreen
import com.sarangem.zenwell.ui.homescreen.HomeScreenFAB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZenwellAppScreen(
    startPermissionActivity: (Intent) -> Unit = {}
) {
    val viewModel: ZenwellAppViewModel = viewModel(factory = ZenwellAppViewModel.factory)
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier
            .nestedScroll(TopAppBarDefaults.enterAlwaysScrollBehavior().nestedScrollConnection)
            .fillMaxSize(),
        topBar = {
            ZenwellTopBar(
                uiState = uiState,
                goBack = { goToHome(viewModel) }
            )
        },
        floatingActionButton = {
            if (uiState.isShowingHomePage) {
                HomeScreenFAB(
                    uiState = uiState,
                    addNewSchedule = {
                        viewModel.addNewSchedule()
                    },
                    openEditScreen = { goToEdit(viewModel, uiState, it) },
                )
            }
        }
    ) { innerPadding ->

        if (uiState.isShowingHomePage) {

            val schedulesList by viewModel.getAllSchedules().collectAsState(emptyList())
            HomeScreen(
                modifier = Modifier.padding(innerPadding),
                schedulesList = schedulesList,
                openEditScreen = { goToEdit(viewModel, uiState, it) },
                startPermissionActivity = startPermissionActivity,
            )

        } else {

            EditScreen(
                modifier = Modifier.padding(innerPadding),
                uiState = uiState,
                updateUiState = { viewModel.updateUiState(it) },
                saveToDatabase = { viewModel.saveToDatabase(context = it) },
                deleteSchedule = { viewModel.deleteSchedule(context = it) },
                goBack = { goToHome(viewModel) }
            )

            LaunchedEffect(Unit) {
                launch(Dispatchers.IO) {
                    initUiState(
                        scheduleId = uiState.scheduleId,
                        coroutineScope = this,
                        viewModel = viewModel,
                        uiState = uiState
                    )
                }
            }

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
                    stringResource(R.string.edit) + " " + uiState.scheduleInfo.title
                },
                style = MaterialTheme.typography.headlineSmall
            )
        },
    )
}


// UI STATE INITIALIZATION //

suspend fun initUiState(
    scheduleId: Int,
    coroutineScope: CoroutineScope,
    viewModel: ZenwellAppViewModel,
    uiState: AppUiState
) {
    val scheduleInfo = coroutineScope.async {
        viewModel.getScheduleInfo(scheduleId).firstOrNull()
    }
    val appNames = coroutineScope.async {
        viewModel.getAppNames(scheduleId).first()
    }

    viewModel.updateUiState(
        uiState.copy(
            scheduleInfo = scheduleInfo.await() ?: Schedules(),
            appNames = appNames.await()
        )
    )
    viewModel.isEditScreenInitialized = true
    viewModel.pastAppList = appNames.await().toMutableList()
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
    scheduleId: Int
) {
    viewModel.updateUiState(
        uiState.copy(
            isShowingHomePage = false,
            scheduleId = scheduleId
        )
    )
}