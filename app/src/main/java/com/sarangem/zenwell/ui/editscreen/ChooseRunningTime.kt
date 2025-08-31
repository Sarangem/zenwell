package com.sarangem.zenwell.ui.editscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.Orbitron
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import com.sarangem.zenwell.utils.checkIfScheduleEnabled
import com.sarangem.zenwell.utils.convertToTimePickerState
import com.sarangem.zenwell.utils.getAmPm
import com.sarangem.zenwell.utils.getWeekDays
import com.sarangem.zenwell.utils.is24Hour
import com.sarangem.zenwell.utils.minutesToString
import com.sarangem.zenwell.utils.toMinutes
import kotlin.math.pow

@Composable
fun ChooseRunningTime(
    modifier: Modifier = Modifier,
    startTimeInMinutes: Int?,
    updateStartTime: (Int?) -> Unit = {},
    endTimeInMinutes: Int,
    updateEndTime: (Int) -> Unit = {},
    weekDays: Int,
    updateWeekDays: (Int) -> Unit = {},
    isRunningTimeInvalid: Boolean
) {
    Card(modifier = modifier) {
        RunAllDay(
            startTimeInMinutes = startTimeInMinutes,
            updateStartTime = updateStartTime,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
        )

        AnimatedVisibility(
            visible = startTimeInMinutes != null
        ) {
            if(startTimeInMinutes != null){
                Row(
                    modifier = Modifier.padding(
                        start = dimensionResource(R.dimen.padding_small),
                        end = dimensionResource(R.dimen.padding_small),
                        bottom = dimensionResource(R.dimen.padding_medium)
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ClockButton(
                        time = startTimeInMinutes,
                        updateUiState = updateStartTime,
                        timePickerTitle = "Choose Start Time",
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = stringResource(R.string.to),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    ClockButton(
                        time = endTimeInMinutes,
                        updateUiState = updateEndTime,
                        timePickerTitle = "Choose End Time",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Text(
            text = stringResource(R.string.choose_week_days),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(
                start = dimensionResource(R.dimen.padding_small),
                end = dimensionResource(R.dimen.padding_small)
            )
        )
        SelectWeekDays(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(R.dimen.padding_small),
                    end = dimensionResource(R.dimen.padding_small),
                    bottom = dimensionResource(R.dimen.padding_small)
                ),
            weekDays = weekDays,
            updateWeekDays = updateWeekDays
        )

        AnimatedVisibility(visible = isRunningTimeInvalid) {
            Text(
                text = stringResource(R.string.running_time_is_invalid),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RunAllDay(
    modifier: Modifier = Modifier,
    startTimeInMinutes: Int?,
    updateStartTime: (Int?) -> Unit = {},
){
    MaterialExpressiveTheme(motionScheme = MotionScheme.standard()) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = stringResource(R.string.run_all_day),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.weight(1f))
            Switch(
                checked = startTimeInMinutes == null,
                onCheckedChange = {
                    if (it) {
                        updateStartTime(null)
                    } else {
                        updateStartTime(0)
                    }
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClockButton(
    modifier: Modifier = Modifier,
    time: Int,
    timePickerTitle: String,
    updateUiState: (Int) -> Unit
) {
    val context = LocalContext.current
    var showPopup by remember { mutableStateOf(false) }
    Button(
        modifier = modifier,
        onClick = { showPopup = true },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        shape = MaterialTheme.shapes.extraLarge,
        border = BorderStroke(width = 0.5.dp, color = MaterialTheme.colorScheme.secondary)
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            Row {
                Spacer(Modifier.weight(0.5f))
                Text(
                    text = minutesToString(time, context),
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = Orbitron,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    autoSize = TextAutoSize.StepBased(maxFontSize = MaterialTheme.typography.headlineMedium.fontSize),
                    modifier = Modifier.graphicsLayer(scaleY = 1.5f)
                )
                Spacer(Modifier.weight(0.5f))
            }
            if (!is24Hour(context)) {
                Row {
                    Spacer(Modifier.weight(0.5f))
                    Text(
                        text = getAmPm(time),
                        style = MaterialTheme.typography.labelMedium,
                        fontFamily = Orbitron,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        autoSize = TextAutoSize.StepBased(maxFontSize = MaterialTheme.typography.labelMedium.fontSize),
                        modifier = Modifier.graphicsLayer(scaleX = 1.5f)
                    )
                    Spacer(Modifier.weight(0.5f))
                }
            }
        }
    }
    if (showPopup) {
        AdvancedTimePickerDialog(
            title = timePickerTitle,
            onDismiss = { showPopup = false },
            onConfirm = {
                updateUiState(it.toMinutes())
                showPopup = false
            },
            timePickerState = convertToTimePickerState(time, context)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedTimePickerDialog(
    title: String,
    onDismiss: () -> Unit = {},
    onConfirm: (TimePickerState) -> Unit = {},
    timePickerState: TimePickerState
) {
    var showDial by remember { mutableStateOf(true) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = dimensionResource(R.dimen.padding_small),
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_large)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.bodyMedium
                )
                if (showDial) {
                    TimePicker(
                        state = timePickerState,
                    )
                } else {
                    TimeInput(
                        state = timePickerState,
                    )
                }
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    IconButton(onClick = { showDial = !showDial }) {
                        Icon(
                            imageVector = if (showDial) {
                                Icons.Filled.Keyboard
                            } else {
                                Icons.Filled.Schedule
                            },
                            contentDescription = stringResource(R.string.time_picker_type_toggle),
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
                    TextButton(onClick = { onConfirm(timePickerState) }) { Text(stringResource(R.string.ok)) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SelectWeekDays(
    modifier: Modifier = Modifier,
    weekDays: Int,
    updateWeekDays: (Int) -> Unit = {}
) {
    val daysList = getWeekDays()

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        daysList.forEachIndexed { index, (day, abbr) ->

            val shape = when (index) {
                0 -> ButtonGroupDefaults.connectedLeadingButtonShape
                daysList.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShape
                else -> ButtonGroupDefaults.connectedMiddleButtonPressShape
            }

            val isChecked = checkIfScheduleEnabled(weekDays, day)
            ToggleButton(
                checked = isChecked,
                onCheckedChange = { checked ->
                    if (checked) {
                        updateWeekDays(weekDays + 10.0.pow(day).toInt())
                    } else {
                        updateWeekDays(weekDays - 10.0.pow(day).toInt())
                    }
                },
                shapes = ToggleButtonDefaults.shapes(
                    shape = shape,
                    checkedShape = shape,
                    pressedShape = shape
                ),
                colors = ToggleButtonDefaults.toggleButtonColors(
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceDim,
                    disabledContentColor = MaterialTheme.colorScheme.onSurface,
                    checkedContainerColor = MaterialTheme.colorScheme.primary,
                    checkedContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.card_elevation))
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(abbr),
                    style = MaterialTheme.typography.labelLarge,
                )
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun ChooseRunningTimeLightPreview() {
    ZenwellTheme {
        var weekDays by remember { mutableIntStateOf(11111110) }
        var startTime: Int? by remember { mutableStateOf(719) }
        ChooseRunningTime(
            startTimeInMinutes = startTime,
            endTimeInMinutes = 0,
            weekDays = weekDays,
            updateWeekDays = { weekDays = it },
            updateStartTime = { startTime = it} ,
            isRunningTimeInvalid = true
        )
    }
}