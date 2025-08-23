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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.database.tables.Schedules
import com.sarangem.zenwell.service.PomodoroWindow
import com.sarangem.zenwell.ui.theme.Orbitron
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import com.sarangem.zenwell.utils.getPomodoroWindow
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusScreen(
    modifier: Modifier = Modifier,
    schedule: Schedules,
    pomodoroWindow: PomodoroWindow? = getPomodoroWindow(schedule.id),
    goBack: () -> Unit = {}
) {
    BackHandler { goBack() }

    val window = LocalActivity.current?.window
    val context = LocalContext.current
    var isFullScreen by rememberSaveable { mutableStateOf(false) }
    DisposableEffect(isFullScreen) {
        if (isFullScreen) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            Toast.makeText(context, R.string.screen_would_remain_on, Toast.LENGTH_SHORT).show()
        }
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            if (!isFullScreen) {
                 Toast.makeText(context, R.string.screen_can_now_turn_off_automatically, Toast.LENGTH_SHORT).show()
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

    LaunchedEffect(Unit) {
        if (pomodoroWindow != null) {
            while (pomodoroWindow.isActive) {
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
                toggleFullScreen = {
                    isFullScreen = it
                    if (it) {
                        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        Toast.makeText(context, R.string.screen_would_remain_on, Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        Toast.makeText(
                            context,
                            R.string.screen_can_now_turn_off_automatically,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                elapsedTime = elapsedTime,
                isWorkTime = isWorkTime,
                isCompleted = isCompleted,
                onStop = {
                    pomodoroWindow.onPomodoroEnd()
                    goBack()
                },
                totalTime = if(isWorkTime) schedule.pomodoroWorkTimeInMinutes else schedule.pomodoroRestTimeInMinutes,
                onPause = { TODO("Do not implement it yet.") },
                onSkip = { TODO("Do not implement it yet.") },
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
    onPause: () -> Unit,
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
                    isWorkTime = isWorkTime,
                    onStop = onStop,
                    onPause = onPause,
                    onSkip = onSkip
                )
            }
        }
    }
}

@Composable
fun PomodoroTimerDisplay(
    elapsedTime: Long,
    isWorkTime: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        val minutes = elapsedTime / 60
        val seconds = elapsedTime % 60
        Icon(
            imageVector = if (isWorkTime) Icons.Filled.Work else Icons.Filled.LocalCafe,
            contentDescription = stringResource(if (isWorkTime) R.string.work_time else R.string.rest_time),
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_large))
                .size(dimensionResource(R.dimen.image_size))
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = if (minutes < 10) "0$minutes" else minutes.toString(),
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_small))
                .graphicsLayer(scaleY = 1.5f),
            fontWeight = FontWeight.Bold,
            fontFamily = Orbitron,
            maxLines = 1,
            autoSize = TextAutoSize.StepBased()
        )
        Text(
            text = if (seconds < 10) "0$seconds" else seconds.toString(),
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_small))
                .graphicsLayer(scaleY = 1.5f),
            fontWeight = FontWeight.Bold,
            fontFamily = Orbitron,
            maxLines = 1,
            autoSize = TextAutoSize.StepBased()
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PomodoroControls(
    modifier: Modifier = Modifier,
    isWorkTime: Boolean,
    onStop: () -> Unit,
    onPause: () -> Unit,
    onSkip: () -> Unit,
) {
    var isStopChecked by remember { mutableStateOf(false) }
    var isPauseChecked by remember { mutableStateOf(false) }
    var isSkipChecked by remember { mutableStateOf(false) }

    if (isStopChecked) {
        ShowConfirmDialog(
            icon = Icons.Filled.Stop,
            headingText = stringResource(R.string.end),
            bodyText = stringResource(R.string.do_you_want_to_end_this_session),
            onConfirm = onStop,
            onDismiss = { isStopChecked = false }
        )
    }

    val buttonColors = ToggleButtonDefaults.toggleButtonColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        checkedContainerColor = MaterialTheme.colorScheme.primaryContainer,
        checkedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ToggleButton(
            checked = isStopChecked,
            onCheckedChange = { isStopChecked = it },
            colors = buttonColors,
            shapes = ButtonGroupDefaults.connectedLeadingButtonShapes(),
            modifier = Modifier
                .fillMaxHeight(0.6f)
                .weight(1f),
        ) {
            Icon(
                imageVector = Icons.Filled.Stop,
                contentDescription = stringResource(R.string.end),
                modifier = Modifier.fillMaxSize(0.5f)
            )
        }

        ToggleButton(
            checked = isPauseChecked,
            onCheckedChange = {
                isPauseChecked = it
                onPause()
            },
            shapes = ButtonGroupDefaults.connectedMiddleButtonShapes(),
            colors = buttonColors,
            modifier = Modifier
                .fillMaxHeight(0.6f)
                .weight(1f),
        ) {
            Icon(
                imageVector = Icons.Filled.Pause,
                contentDescription = stringResource(R.string.pause),
                modifier = Modifier.fillMaxSize(0.5f)
            )
        }

        ToggleButton(
            checked = isSkipChecked,
            onCheckedChange = {
                isSkipChecked = it
                onSkip()
            },
            shapes = ButtonGroupDefaults.connectedTrailingButtonShapes(),
            modifier = Modifier
                .fillMaxHeight(0.6f)
                .weight(1f),
            colors = buttonColors
        ) {
            Icon(
                imageVector = Icons.Filled.FastForward,
                contentDescription = stringResource(R.string.skip_to_next_session),
                tint = if (isWorkTime) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.fillMaxSize(0.5f)
            )
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