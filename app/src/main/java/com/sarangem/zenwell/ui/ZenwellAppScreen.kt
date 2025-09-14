package com.sarangem.zenwell.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.core.layout.WindowSizeClass
import com.sarangem.zenwell.data.ZenwellNavigationPage
import com.sarangem.zenwell.ui.editscreen.EditScreen
import com.sarangem.zenwell.ui.editscreen.EditScreenPlaceholder
import com.sarangem.zenwell.ui.focusscreen.FocusScreen
import com.sarangem.zenwell.ui.homescreen.HomeScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
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
    val isExpandedWidth =
        currentWindowAdaptiveInfo().windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    val startPermissionActivity: (Intent) -> Unit = { intent ->
        activity?.startActivity(intent)
    }

    val schedulesList by viewModel.getAllSchedules().collectAsState(emptyList())

    val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator<Int>()
    val coroutineScope = rememberCoroutineScope()
    val paneExpansionState = rememberPaneExpansionState()
    paneExpansionState.setFirstPaneProportion(0.5f)

    NavigableListDetailPaneScaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        navigator = scaffoldNavigator,
        paneExpansionState = paneExpansionState,
        listPane = {
            AnimatedPane{
                if(uiState.navigationPage == ZenwellNavigationPage.Home) {
                    val schedulesList by viewModel.getAllSchedules().collectAsState(emptyList())

                    HomeScreen(
                        modifier = Modifier.fillMaxSize(),
                        schedulesList = schedulesList,
                        scheduleClicked = uiState.schedule.id,
                        startPermissionActivity = startPermissionActivity,
                        requestNotification = requestNotification,
                        addNewSchedule = { viewModel.addNewSchedule(context) },
                        openEditScreen = {
                            viewModel.updateUiState(
                                uiState.copy(
                                    schedule = it,
                                )
                            )
                            coroutineScope.launch {
                                scaffoldNavigator.navigateTo(
                                    ListDetailPaneScaffoldRole.Detail,
                                    it.id
                                )
                            }
                        },
                        openFocusScreen = {
                            viewModel.updateUiState(
                                uiState.copy(
                                    navigationPage = ZenwellNavigationPage.Focus,
                                    schedule = it,
                                )
                            )
                            coroutineScope.launch {
                                paneExpansionState.setFirstPaneProportion(1f)
                                scaffoldNavigator.navigateTo(ListDetailPaneScaffoldRole.List)
                            }
                        }
                    )
                } else if (uiState.navigationPage == ZenwellNavigationPage.Focus){
                    FocusScreen(
                        schedule = uiState.schedule,
                        goBack = {
                            viewModel.updateUiState(AppUiState())
                            paneExpansionState.setFirstPaneProportion(0.5f)
                        }
                    )
                }
            }
        },
        detailPane = {
            AnimatedPane {
                scaffoldNavigator.currentDestination?.contentKey?.let {
                    EditScreen(
                        modifier = Modifier.fillMaxSize(),
                        uiState = uiState,
                        showTopAppBar = !isExpandedWidth,
                        updateUiState = { viewModel.updateUiState(it) },
                        setAppNamesInUiState = { viewModel.setAppNamesInUiState() },
                        onSave = { viewModel.saveToDatabase() },
                        onDelete = { viewModel.deleteSchedule() },
                        goBack = {
                            viewModel.updateUiState(
                                AppUiState()
                            )
                            coroutineScope.launch {
                                scaffoldNavigator.navigateBack()
                            }
                        }
                    )

                } ?: run {
                    EditScreenPlaceholder(
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        },
    )
}
