package com.sarangem.zenwell.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.window.core.layout.WindowSizeClass
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sarangem.zenwell.data.ZenwellNavigationPage
import com.sarangem.zenwell.ui.editscreen.EditScreen
import com.sarangem.zenwell.ui.focusscreen.FocusScreen
import com.sarangem.zenwell.ui.homescreen.HomeScreen

@Composable
fun ZenwellAppScreen() {

    val viewModel: ZenwellAppViewModel = viewModel(factory = ZenwellAppViewModel.factory)
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}
    val requestNotification: () -> Unit = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    val activity = LocalActivity.current
    val isExpandedWidth = currentWindowAdaptiveInfo().windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
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
            requestNotification = requestNotification,
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
                viewModel.updateUiState(
                    AppUiState()
                )
            }
        )
    }

    AnimatedContent(
        targetState = uiState.navigationPage,
        transitionSpec = {
            if (targetState.ordinal > initialState.ordinal) {
                slideInHorizontally(initialOffsetX = { it }, animationSpec = tween()) togetherWith
                        slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween())
            } else {
                slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween()) togetherWith
                        slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween())
            }
        }
    ) { targetPage ->
        when (targetPage) {
            ZenwellNavigationPage.Home -> homeScreen(Modifier.fillMaxSize())
            ZenwellNavigationPage.Edit -> {
                if (isExpandedWidth) {
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
}
