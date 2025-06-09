package com.sarangem.zenwell.ui.editscreen

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sarangem.zenwell.R
import com.sarangem.zenwell.convertToTimePickerState
import com.sarangem.zenwell.getAmPm
import com.sarangem.zenwell.minutesToString
import com.sarangem.zenwell.toMinutes
import com.sarangem.zenwell.ui.theme.Orbitron
import com.sarangem.zenwell.ui.theme.ZenwellTheme

@Composable
fun ChooseRunningTime(
    modifier: Modifier = Modifier,
    startTimeInMinutes: Int,
    updateStartTime: (Int) -> Unit = {},
    endTimeInMinutes: Int,
    updateEndTime: (Int) -> Unit = {}
) {
    Card(modifier = modifier) {

        Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))) {
            Text(
                text = stringResource(R.string.choose_running_time),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )
            Row(
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClockButton(
    modifier: Modifier = Modifier,
    time: Int,
    timePickerTitle: String,
    updateUiState: (Int) -> Unit
) {
    var showPopup by remember { mutableStateOf(false) }
    Button(
        modifier = modifier,
        onClick = { showPopup = true },
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surfaceDim),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            Row {
                Spacer(Modifier.weight(0.5f))
                Text(
                    text = minutesToString(time),
                    style = MaterialTheme.typography.headlineSmall,
                    fontFamily = Orbitron,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.graphicsLayer(scaleY = 1.5f)
                )
                Spacer(Modifier.weight(0.5f))
            }
            Row {
                Spacer(Modifier.weight(0.5f))
                Text(
                    text = getAmPm(time),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = Orbitron,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.graphicsLayer(scaleX = 1.5f)
                )
                Spacer(Modifier.weight(0.5f))
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
            timePickerState = convertToTimePickerState(time)
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


@Preview(showBackground = true)
@Composable
fun ChooseRunningTimeLightPreview() {
    ZenwellTheme {
        ChooseRunningTime(
            startTimeInMinutes = 0,
            endTimeInMinutes = 1439
        )
    }
}