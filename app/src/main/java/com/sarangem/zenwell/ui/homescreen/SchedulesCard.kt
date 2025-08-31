package com.sarangem.zenwell.ui.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.database.tables.Schedules
import com.sarangem.zenwell.service.AppBlockerService
import com.sarangem.zenwell.service.PomodoroWindow
import com.sarangem.zenwell.ui.commonui.ShowConfirmDialog
import com.sarangem.zenwell.ui.theme.Orbitron
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import com.sarangem.zenwell.utils.getAmPm
import com.sarangem.zenwell.utils.is24Hour
import com.sarangem.zenwell.utils.minutesToString
import com.sarangem.zenwell.utils.secondsToString
import kotlinx.coroutines.delay

@Composable
fun SchedulesCard(
    modifier: Modifier = Modifier,
    schedule: Schedules,
    isClicked: Boolean = false,
    pomodoroWindow: PomodoroWindow? = AppBlockerService.instance?.getPomodoroWindow(schedule.id),
    openEditScreen: (Schedules) -> Unit = {},
    openFocusScreen: (Schedules) -> Unit = {},
) {
    val tint =
        if (schedule.isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
    val cardColor = if (isClicked) {
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    } else {
        CardDefaults.cardColors()
    }

    var isPomodoroActive by rememberSaveable {
        mutableStateOf(
            pomodoroWindow?.isActive ?: false
        )
    }

    Card(
        colors = cardColor,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))) {
            Row(
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = schedule.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = tint,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.size(dimensionResource(R.dimen.padding_tiny)))
                    PomodoroStartButton(
                        schedule = schedule,
                        tint = tint,
                        pomodoroWindow = pomodoroWindow,
                        isPomodoroActive = isPomodoroActive,
                        updatePomodoroActivity = { isPomodoroActive = it }
                    )
                }
                IconButton(
                    onClick = { openEditScreen(schedule) },
                    colors = IconButtonDefaults.iconButtonColors(MaterialTheme.colorScheme.surfaceDim),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = stringResource(R.string.edit_this_schedule),
                        tint = tint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            if (schedule.isEnabled && schedule.isPomodoro && isPomodoroActive && pomodoroWindow != null) {
                Spacer(modifier = Modifier.size(dimensionResource(R.dimen.padding_small)))
                PomodoroTimerControls(
                    pomodoroWindow = pomodoroWindow,
                    updatePomodoroActivity = { isPomodoroActive = it },
                    openFocusScreen = { openFocusScreen(schedule) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PomodoroStartButton(
    modifier: Modifier = Modifier,
    schedule: Schedules,
    tint: Color,
    isPomodoroActive: Boolean,
    pomodoroWindow: PomodoroWindow? = null,
    updatePomodoroActivity: (Boolean) -> Unit = {},
) {
    val context = LocalContext.current
    if (schedule.isEnabled && schedule.isPomodoro && pomodoroWindow != null && !isPomodoroActive) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(dimensionResource(R.dimen.padding_small)))
                .background(MaterialTheme.colorScheme.primary)
                .clickable(onClick = {
                    pomodoroWindow.onPomodoroStart()
                    updatePomodoroActivity(true)
                })
        ) {
            Row(Modifier.padding(dimensionResource(R.dimen.padding_tiny))) {
                Icon(
                    imageVector = Icons.Filled.Navigation,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.padding_medium))
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = stringResource(R.string.start),
                    style = MaterialTheme.typography.labelLargeEmphasized,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Spacer(Modifier.width(dimensionResource(R.dimen.padding_small)))
                Text(
                    text = "(" + schedule.pomodoroWorkTimeInMinutes.toString() + " + " + schedule.pomodoroRestTimeInMinutes.toString() + " " + stringResource(
                        R.string.minutes
                    ) + ")",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    } else if (schedule.isPomodoro) {
        Text(
            text = schedule.pomodoroWorkTimeInMinutes.toString() + " + " + schedule.pomodoroRestTimeInMinutes.toString() + " " + stringResource(
                R.string.minutes
            ),
            style = MaterialTheme.typography.labelLarge,
            color = tint,
            fontWeight = FontWeight.Bold,
        )
    } else {
        Text(
            modifier = modifier,
            text = if (schedule.startTimeInMinutes == null) {
                stringResource(R.string.all_day)
            } else if (is24Hour(context)) {
                minutesToString(
                    schedule.startTimeInMinutes,
                    context
                ) + " " + stringResource(R.string.to) + " " + minutesToString(
                    schedule.endTimeInMinutes,
                    context
                )
            } else {
                minutesToString(schedule.startTimeInMinutes, context) + " " + getAmPm(
                    schedule.startTimeInMinutes
                ) + " " + stringResource(R.string.to) + " " + minutesToString(
                    schedule.endTimeInMinutes,
                    context
                ) + " " + getAmPm(schedule.endTimeInMinutes)
            },
            style = MaterialTheme.typography.labelLarge,
            color = tint,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun PomodoroTimerControls(
    modifier: Modifier = Modifier,
    pomodoroWindow: PomodoroWindow,
    updatePomodoroActivity: (Boolean) -> Unit = {},
    openFocusScreen: () -> Unit = {},
) {
    var elapsedTime by rememberSaveable { mutableIntStateOf(pomodoroWindow.getElapsedTimeInSeconds()) }
    var isWorkTime by rememberSaveable { mutableStateOf(pomodoroWindow.isWorkTime) }
    LaunchedEffect(Unit) {
        while (pomodoroWindow.isActive) {
            delay(1000L)
            isWorkTime = pomodoroWindow.isWorkTime
            elapsedTime = pomodoroWindow.getElapsedTimeInSeconds()
        }
        updatePomodoroActivity(false)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceDim),
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier
                .weight(0.75f)
                .fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = dimensionResource(R.dimen.padding_medium),
                        vertical = dimensionResource(R.dimen.padding_small)
                    )
            ) {
                Icon(
                    imageVector = if (isWorkTime) Icons.Filled.Work else Icons.Filled.LocalCafe,
                    contentDescription = stringResource(if (isWorkTime) R.string.work_time else R.string.rest_time),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.padding_large))
                        .align(Alignment.TopEnd)
                )
                Text(
                    text = secondsToString(elapsedTime),
                    style = MaterialTheme.typography.displayMedium,
                    fontFamily = Orbitron,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .graphicsLayer(scaleY = 1.5f)
                        .align(Alignment.Center)
                )
            }
        }
        Spacer(modifier = Modifier.size(dimensionResource(R.dimen.padding_tiny)))
        Column(
            Modifier
                .weight(0.25f)
                .fillMaxHeight()
        ) {
            var isStopClicked by remember { mutableStateOf(false) }
            if(isStopClicked){
                ShowConfirmDialog(
                    icon = Icons.Filled.Stop,
                    headingText = stringResource(R.string.end),
                    bodyText = stringResource(R.string.do_you_want_to_end_this_session),
                    onConfirm = {
                        pomodoroWindow.onPomodoroEnd()
                        updatePomodoroActivity(false)
                    },
                    onDismiss = { isStopClicked = false }
                )
            }

            Button(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_tiny)),
                onClick = {
                    isStopClicked = true
                },
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(dimensionResource(R.dimen.padding_medium))
            ) {
                Icon(
                    imageVector = Icons.Filled.Stop,
                    contentDescription = stringResource(R.string.end),
                    tint = MaterialTheme.colorScheme.onError,
                )
            }
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_tiny)))
            Button(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_tiny)),
                onClick = openFocusScreen,
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.inversePrimary),
                shape = RoundedCornerShape(dimensionResource(R.dimen.padding_medium))
            ) {
                Icon(
                    imageVector = Icons.Outlined.SelfImprovement,
                    contentDescription = stringResource(R.string.focus_mode),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}


// PREVIEW

@Preview(showBackground = true)
@Composable
fun RegularSchedulesCardPreview() {
    ZenwellTheme {
        SchedulesCard(
            schedule = Schedules(
                id = 1,
                title = "Morning Focus",
                startTimeInMinutes = 9 * 60,
                endTimeInMinutes = 10 * 60,
                isPomodoro = false,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PomodoroScheduleCardPreview() {
    ZenwellTheme {
        val schedule = Schedules(
            id = 2,
            title = "Study Session",
            isPomodoro = true,
            pomodoroWorkTimeInMinutes = 1,
            pomodoroRestTimeInMinutes = 5
        )
        SchedulesCard(
            schedule = schedule,
            pomodoroWindow = PomodoroWindow(schedule, context = LocalContext.current),
        )
    }
}
