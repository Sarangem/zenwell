package com.sarangem.zenwell.ui.focusscreen

import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.database.tables.Schedules
import com.sarangem.zenwell.service.AppBlockerService
import com.sarangem.zenwell.service.PomodoroWindow
import com.sarangem.zenwell.ui.commonui.TimerBox
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusScreen(
    modifier: Modifier = Modifier,
    schedule: Schedules,
    pomodoroWindow: PomodoroWindow? = AppBlockerService.instance?.getPomodoroWindow(schedule.id),
    goBack: () -> Unit = {}
) {
    BackHandler { goBack() }

    val window = LocalActivity.current?.window
    val context = LocalContext.current
    var isFullScreen by rememberSaveable { mutableStateOf(false) }
    DisposableEffect(isFullScreen) {
        var screenWasKeptOn = false
        if (isFullScreen) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            Toast.makeText(context, R.string.screen_would_remain_on, Toast.LENGTH_SHORT).show()
            screenWasKeptOn = true
        }
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            if (screenWasKeptOn) {
                Toast.makeText(
                    context,
                    R.string.screen_can_now_turn_off_automatically,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    var elapsedTime by rememberSaveable {
        mutableLongStateOf(
            pomodoroWindow?.getElapsedTimeInSeconds() ?: 0
        )
    }
    var isWorkTime by rememberSaveable { mutableStateOf(pomodoroWindow?.isWorkTime ?: true) }
    var isCompleted by rememberSaveable { mutableStateOf(false) }
    var isPaused by rememberSaveable { mutableStateOf(pomodoroWindow?.isPaused ?: false) }

    if (pomodoroWindow != null) {
        LaunchedEffect(Unit) {
            while (pomodoroWindow.isActive || pomodoroWindow.isPaused) {
                delay(1000L)
                elapsedTime = pomodoroWindow.getElapsedTimeInSeconds()
                if (elapsedTime <= 0) {
                    isWorkTime = !isWorkTime
                }
            }
            isCompleted = true
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            AnimatedVisibility(
                visible = !isFullScreen,
                enter = fadeIn(animationSpec = tween(durationMillis = 500, easing = LinearEasing)),
                exit = fadeOut(animationSpec = tween(durationMillis = 500, easing = LinearEasing))
            ) {
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
                            text = schedule.title + " (" + stringResource(R.string.focus_mode) + ")",
                            style = MaterialTheme.typography.headlineSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                )
            }
        }
    ) { innerPadding ->
        if (pomodoroWindow == null) {
            Text(
                text = stringResource(R.string.accessibility_service_not_started),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            )
        } else {
            FocusScreenBody(
                modifier = Modifier.padding(innerPadding),
                isFullScreen = isFullScreen,
                toggleFullScreen = { isFullScreen = it },
                elapsedTime = elapsedTime,
                isWorkTime = isWorkTime,
                isCompleted = isCompleted,
                onStop = {
                    pomodoroWindow.onPomodoroEnd()
                    goBack()
                },
                totalTime = if (isWorkTime) schedule.pomodoroWorkTimeInMinutes * 60 else schedule.pomodoroRestTimeInMinutes * 60,
                isPaused = isPaused,
                onPauseOrResume = {
                    if (isPaused) {
                        pomodoroWindow.onPomodoroStart()
                    } else {
                        pomodoroWindow.onPomodoroPause()
                    }
                    isPaused = !isPaused
                },
                onSkip = {
                    pomodoroWindow.onPomodoroSkip()
                    isWorkTime = !isWorkTime
                    isPaused = false
                },
            )
        }
    }
}

@Composable
fun FocusScreenBody(
    modifier: Modifier = Modifier,
    isFullScreen: Boolean,
    toggleFullScreen: (Boolean) -> Unit,
    elapsedTime: Long,
    totalTime: Int,
    isWorkTime: Boolean,
    isCompleted: Boolean,
    onStop: () -> Unit,
    isPaused: Boolean,
    onPauseOrResume: () -> Unit,
    onSkip: () -> Unit
) {
    if (isFullScreen) {
        BackHandler { toggleFullScreen(false) }
    }
    val animatedProgress by animateFloatAsState(
        targetValue = (totalTime - elapsedTime).toFloat() / totalTime,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
    )

    val animationSpec: AnimationSpec<Dp> = tween(durationMillis = 500, easing = LinearEasing)
    val animatedPadding by animateDpAsState(
        targetValue = if (isFullScreen) 0.dp else dimensionResource(R.dimen.padding_large),
        animationSpec = animationSpec,
    )
    val animatedCornerRadius by animateDpAsState(
        targetValue = if (isFullScreen) 0.dp else dimensionResource(R.dimen.padding_large),
        animationSpec = animationSpec,
    )
    val animatedStrokeWidth by animateDpAsState(
        targetValue = if (isFullScreen) dimensionResource(R.dimen.image_size) else dimensionResource(
            R.dimen.padding_large
        ),
        animationSpec = animationSpec,
    )

    Column(modifier = modifier.fillMaxSize()) {
        if (isCompleted) {
            Text(
                text = stringResource(R.string.pomodoro_session_completed),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            TimerBox(
                modifier = Modifier
                    .weight(0.8f)
                    .fillMaxSize()
                    .padding(animatedPadding)
                    .clickable(onClick = { toggleFullScreen(!isFullScreen) }),
                progress = animatedProgress,
                backgroundColor = if (isWorkTime) {
                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                } else {
                    MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                },
                trackColor = if (isWorkTime) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                cornerRadiusInDp = animatedCornerRadius,
                strokeWidthInDp = animatedStrokeWidth
            ) {
                PomodoroTimerDisplay(elapsedTime = elapsedTime, isWorkTime = isWorkTime)
            }

            AnimatedVisibility(
                visible = !isFullScreen,
                modifier = Modifier.weight(0.2f),
                enter = fadeIn(animationSpec = tween(durationMillis = 500, easing = LinearEasing)),
                exit = fadeOut(animationSpec = tween(durationMillis = 500, easing = LinearEasing))
            ) {
                PomodoroControls(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.padding_small)),
                    onStop = onStop,
                    isPaused = isPaused,
                    onPauseOrResume = onPauseOrResume,
                    onSkip = onSkip
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FocusScreenPreview() {
    ZenwellTheme {
        val schedule = Schedules(
            id = 1,
            title = "Study Time",
            isPomodoro = false,
            pomodoroWorkTimeInMinutes = 1,
            pomodoroRestTimeInMinutes = 1
        )
        val pomodoroWindow = PomodoroWindow(schedule)
        pomodoroWindow.onPomodoroStart()
        FocusScreen(
            schedule = schedule,
            pomodoroWindow = pomodoroWindow,
        )
    }
}

@Preview
@Composable
fun FocusScreenDarkModePreview() {
    ZenwellTheme(darkTheme = true) {
        val schedule = Schedules(
            id = 1,
            title = "Study Time",
            isPomodoro = false,
            pomodoroWorkTimeInMinutes = 1,
            pomodoroRestTimeInMinutes = 1
        )
        val pomodoroWindow = PomodoroWindow(schedule)
        pomodoroWindow.onPomodoroStart()
        FocusScreen(
            schedule = schedule,
            pomodoroWindow = pomodoroWindow,
        )
    }
}