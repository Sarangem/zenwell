/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.edit.fields

import android.content.Context
import android.text.format.DateFormat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.sizing
import com.sarangem.zenwell.utils.minutesToString

@Composable
fun ChooseActiveTime(
    startTimeInMinutes: Int,
    updateStartTime: (Int) -> Unit = {},
    endTimeInMinutes: Int,
    updateEndTime: (Int) -> Unit = {},
    isError: Boolean
) {
    val isExpandedWidth = currentWindowAdaptiveInfoV2().windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)
    DetailsCard {
        Text(
            text = stringResource(R.string.choose_active_time),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Column(Modifier.weight(2f)) {
            if (isError) {
                Text(
                    text = stringResource(R.string.active_time_is_invalid),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(MaterialTheme.sizing.small)
                )
            }
            if(isExpandedWidth){
                Row(Modifier.fillMaxSize()){
                    ClockRange(Modifier.weight(1f), Modifier.align(Alignment.CenterVertically), startTimeInMinutes, updateStartTime, endTimeInMinutes, updateEndTime)
                }
            } else {
                Column(Modifier.fillMaxSize()) {
                    ClockRange(Modifier.fillMaxWidth(), Modifier.align(Alignment.CenterHorizontally), startTimeInMinutes, updateStartTime, endTimeInMinutes, updateEndTime)
                }
            }
        }
    }
}

@Composable
fun ClockRange(
    clockModifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    startTimeInMinutes: Int,
    updateStartTime: (Int) -> Unit = {},
    endTimeInMinutes: Int,
    updateEndTime: (Int) -> Unit = {},
){
    ClockButton(
        modifier = clockModifier,
        time = startTimeInMinutes,
        timePickerTitle = stringResource(R.string.choose_start_time),
        updateUiState = updateStartTime
    )
    Text(
        text = stringResource(R.string.to),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = textModifier.padding(vertical = MaterialTheme.sizing.small)
    )
    ClockButton(
        modifier = clockModifier,
        time = endTimeInMinutes,
        timePickerTitle = stringResource(R.string.choose_end_time),
        updateUiState = updateEndTime
    )
}

@Composable
fun ClockButton(
    modifier: Modifier = Modifier,
    time: Int,
    timePickerTitle: String,
    updateUiState: (Int) -> Unit
) {
    val context = LocalContext.current
    var showPopup by remember { mutableStateOf(false) }
    Surface(
        onClick = { showPopup = true },
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        color = Color.Transparent,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(MaterialTheme.sizing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = minutesToString(time, context),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(R.drawable.outlined_schedule),
                contentDescription = null,
                modifier = Modifier.padding(start = MaterialTheme.sizing.small)
            )
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
@Preview(showBackground = true)
@Composable
fun AdvancedTimePickerDialog(
    title: String = "Choose Time",
    onDismiss: () -> Unit = {},
    onConfirm: (TimePickerState) -> Unit = {},
    timePickerState: TimePickerState = TimePickerState(0,0,false)
) {
    var showDial by remember { mutableStateOf(true) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = MaterialTheme.sizing.small,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(MaterialTheme.sizing.large),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = MaterialTheme.sizing.medium),
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (showDial) TimePicker(timePickerState) else TimeInput(timePickerState)
                Row(Modifier.fillMaxWidth()) {
                    IconButton(onClick = { showDial = !showDial }) {
                        Icon(
                            painterResource(if (showDial) R.drawable.outlined_keyboard else R.drawable.outlined_schedule),
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

@OptIn(ExperimentalMaterial3Api::class)
fun convertToTimePickerState(timeInMinutes: Int, context: Context): TimePickerState {
    return TimePickerState(
        initialHour = (timeInMinutes / 60),
        initialMinute = (timeInMinutes % 60),
        is24Hour = DateFormat.is24HourFormat(context)
    )
}
fun TimePickerState.toMinutes(): Int = (this.hour * 60) + (this.minute)