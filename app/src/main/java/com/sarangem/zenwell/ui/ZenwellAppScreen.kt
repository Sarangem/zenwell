package com.sarangem.zenwell.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.window.core.layout.WindowSizeClass
import com.sarangem.zenwell.data.FocusPage
import com.sarangem.zenwell.data.HomePage
import com.sarangem.zenwell.data.database.tables.Schedules
import com.sarangem.zenwell.ui.editscreen.EditScreen
import com.sarangem.zenwell.ui.editscreen.EditScreenPlaceholder
import com.sarangem.zenwell.ui.focusscreen.FocusScreen
import com.sarangem.zenwell.ui.homescreen.HomeScreen
import kotlinx.coroutines.launch

@Composable
fun ZenwellAppScreen() {

    val backStack = rememberNavBackStack(HomePage)
    NavDisplay(
        modifier = Modifier.fillMaxSize(),
        backStack = backStack,
        transitionSpec = {
            slideInHorizontally(initialOffsetX = { it }, animationSpec = tween()) togetherWith
                    slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween())
        },
        popTransitionSpec = {
            slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween()) togetherWith
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween())
        },
        predictivePopTransitionSpec = {
            slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween()) togetherWith
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween())
        },
        entryProvider = { key ->
            when (key) {

                is HomePage -> NavEntry(key) {
                    ListDetailScreen(
                        modifier = Modifier.fillMaxSize(),
                        openFocusScreen = {
                            backStack.add(FocusPage(it))
                        }
                    )
                }

                is FocusPage -> NavEntry(
                    key = key,
                    metadata = NavDisplay.transitionSpec {
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(1000)
                        ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                    } + NavDisplay.popTransitionSpec {
                        EnterTransition.None togetherWith
                                slideOutHorizontally(
                                    targetOffsetX = { it },
                                    animationSpec = tween()
                                )
                    } + NavDisplay.predictivePopTransitionSpec {
                        EnterTransition.None togetherWith
                                slideOutHorizontally(
                                    targetOffsetX = { it },
                                    animationSpec = tween()
                                )
                    }
                ) {
                    FocusScreen(
                        modifier = Modifier.fillMaxSize(),
                        schedule = key.schedules,
                        goBack = {
                            backStack.removeLastOrNull()
                        }
                    )
                }

                else -> {
                    error("Unknown route: $key")
                }

            }

        }
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ListDetailScreen(
    modifier: Modifier = Modifier,
    openFocusScreen: (Schedules) -> Unit = {}
) {
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

    val navigator = rememberListDetailPaneScaffoldNavigator<Schedules>()
    val coroutineScope = rememberCoroutineScope()
    val paneExpansionState = rememberPaneExpansionState()
    paneExpansionState.setFirstPaneProportion(0.5f)

    NavigableListDetailPaneScaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        navigator = navigator,
        paneExpansionState = paneExpansionState,
        listPane = {
            AnimatedPane {
                HomeScreen(
                    modifier = Modifier.fillMaxSize(),
                    scheduleClicked = navigator.currentDestination?.contentKey?.id ?: 0,
                    startPermissionActivity = startPermissionActivity,
                    requestNotification = requestNotification,
                    openEditScreen = {
                        coroutineScope.launch {
                            navigator.navigateTo(
                                ListDetailPaneScaffoldRole.Detail,
                                it
                            )
                        }
                    },
                    openFocusScreen = openFocusScreen
                )
            }
        },
        detailPane = {
            AnimatedPane {
                navigator.currentDestination?.contentKey?.let {
                    EditScreen(
                        modifier = Modifier.fillMaxSize(),
                        schedule = it,
                        showTopAppBar = !isExpandedWidth,
                        goBack = {
                            coroutineScope.launch {
                                navigator.navigateBack()
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