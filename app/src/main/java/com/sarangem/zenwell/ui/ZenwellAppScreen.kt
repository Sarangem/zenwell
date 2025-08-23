package com.sarangem.zenwell.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.sarangem.zenwell.data.ZenwellNavigationPage
import com.sarangem.zenwell.ui.editscreen.EditScreen
import com.sarangem.zenwell.ui.focusscreen.FocusScreen
import com.sarangem.zenwell.ui.homescreen.HomeScreen
import com.sarangem.zenwell.utils.isExpandedWidth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZenwellAppScreen() {

    val viewModel: ZenwellAppViewModel = viewModel(factory = ZenwellAppViewModel.factory)
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}
    val activity = LocalActivity.current
    val startPermissionActivity: (Intent) -> Unit = { intent ->
        activity?.startActivity(intent)
    }

    val schedulesList by viewModel.getAllSchedules().collectAsState(emptyList())
    val homeScreen: @Composable (Modifier) -> Unit = { modifier ->
        HomeScreen(
            modifier = modifier,
            schedulesList = schedulesList,
            scheduleClicked = uiState.schedule.id,
            startPermissionActivity = startPermissionActivity,
            requestNotification = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            },
            addNewSchedule = { viewModel.addNewSchedule(context) },
            openEditScreen = {
                viewModel.updateUiState(
                    uiState.copy(
                        navigationPage = ZenwellNavigationPage.Edit,
                        schedule = it,
                    )
                )
            },
            openFocusScreen = {
                viewModel.updateUiState(
                    uiState.copy(
                        navigationPage = ZenwellNavigationPage.Focus,
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

    when (uiState.navigationPage){
        ZenwellNavigationPage.Home -> homeScreen(Modifier.fillMaxSize())
        ZenwellNavigationPage.Edit -> {
            if(isExpandedWidth(width)) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    homeScreen(Modifier.weight(1f))
                    editScreen(Modifier.weight(1f), false)
                }
            } else {
                editScreen(Modifier.fillMaxSize(), true)
            }
        }
        ZenwellNavigationPage.Focus -> FocusScreen(
            schedule = uiState.schedule,
            goBack = {
                viewModel.updateUiState(AppUiState())
            }
        )
        ZenwellNavigationPage.Settings -> {}
    }
}