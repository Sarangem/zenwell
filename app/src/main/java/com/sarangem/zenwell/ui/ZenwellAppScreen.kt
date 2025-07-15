package com.sarangem.zenwell.ui

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sarangem.zenwell.ui.editscreen.EditScreen
import com.sarangem.zenwell.ui.homescreen.HomeScreen
import com.sarangem.zenwell.utils.isExpandedWidth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZenwellAppScreen(
    startPermissionActivity: (Intent) -> Unit = {},
    shouldShowRequestPermissionRationale: (String) -> Unit = {}
) {
    val viewModel: ZenwellAppViewModel = viewModel(factory = ZenwellAppViewModel.factory)
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val schedulesList by viewModel.getAllSchedules().collectAsState(emptyList())
    val homeScreen: @Composable (Modifier) -> Unit = { modifier ->
        HomeScreen(
            modifier = modifier,
            schedulesList = schedulesList,
            scheduleClicked = uiState.schedule.id,
            startPermissionActivity = startPermissionActivity,
            shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale,
            addNewSchedule = { viewModel.addNewSchedule(context) },
            openEditScreen = {

                // go to Edit screen
                viewModel.updateUiState(
                    uiState.copy(
                        isShowingHomePage = false,
                        schedule = it,
                    )
                )

            }
        )
    }

    val editScreen: @Composable (Modifier, Boolean) -> Unit = { modifier, showTopAppBar ->
        EditScreen(
            modifier = modifier,
            uiState = uiState,
            showTopAppBar = showTopAppBar,
            updateUiState = { viewModel.updateUiState(it) },
            setAppNamesInUiState = { viewModel.setAppNamesInUiState() },
            onSave = { viewModel.saveToDatabase() },
            onDelete = { viewModel.deleteSchedule() },
            goBack = {

                // go to HomeScreen
                viewModel.updateUiState(
                    AppUiState()
                )

            }
        )
    }

    val width = with(LocalDensity.current) {
        LocalWindowInfo.current.containerSize.width.toDp().value
    }

    if (isExpandedWidth(width)) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            homeScreen(Modifier.weight(1f))
            if (!uiState.isShowingHomePage) {
                editScreen(Modifier.weight(1f), false)
            }
        }
    } else {
        if (uiState.isShowingHomePage) {
            homeScreen(Modifier.fillMaxSize())
        } else {
            editScreen(Modifier.fillMaxSize(), true)
        }
    }
}