package com.sarangem.zenwell.ui.editscreen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.text.isDigitsOnly
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.ZenwellTheme

@Composable
fun ChooseEnable(
    modifier: Modifier = Modifier,
    checked: Boolean,
    updateUiState: (Boolean) -> Unit = {}
) {
    EditScreenCard(modifier = modifier) {
        Text(
            text = stringResource(R.string.enable),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = { updateUiState(it) },
            modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_small))
        )
    }
}

@Composable
fun ChooseScheduleTitle(
    modifier: Modifier = Modifier,
    title: String,
    updateUiState: (String) -> Unit = {}
) {
    EditScreenOutlinedField(
        mainText = stringResource(R.string.schedule_title),
        textFieldValue = title,
        onValueChange = { updateUiState(it) },
        modifier = modifier
    )
}

@Composable
fun ChooseMessage(
    modifier: Modifier = Modifier,
    message: String,
    updateUiState: (String) -> Unit = {}
) {
    EditScreenOutlinedField(
        mainText = stringResource(R.string.message),
        textFieldValue = message,
        onValueChange = { updateUiState(it) },
        modifier = modifier
    )
}

@Composable
fun ChooseNotificationTime(
    modifier: Modifier = Modifier,
    notificationTime: Int,
    isNotificationTimeInvalid: Boolean,
    updateUiState: (Int) -> Unit = {}
) {
    EditScreenOutlinedField(
        mainText = stringResource(R.string.send_notification_before_closing),
        textFieldValue = notificationTime.toString(),
        onValueChange = {
            if (it.isDigitsOnly()) {
                val num = it.toIntOrNull()
                if (num == null) {
                    updateUiState(0)
                } else {
                    updateUiState(num)
                }
            }
        },
        keyboardType = KeyboardType.Number,
        suffixText = stringResource(R.string.minutes),
        modifier = modifier,
        isError = isNotificationTimeInvalid,
        errorMessage = stringResource(R.string.notification_time_invalid)
    )
}

@Composable
fun ChooseWaitTime(
    modifier: Modifier = Modifier,
    waitTimeInSeconds: Int,
    updateUiState: (Int) -> Unit = {}
) {
    EditScreenOutlinedField(
        mainText = stringResource(R.string.wait_time),
        textFieldValue = waitTimeInSeconds.toString(),
        onValueChange = {
            if (it.isDigitsOnly()) {
                val num = it.toIntOrNull()
                if (num == null) {
                    updateUiState(0)
                } else {
                    updateUiState(num)
                }
            }
        },
        keyboardType = KeyboardType.Number,
        suffixText = stringResource(R.string.seconds),
        modifier = modifier
    )
}

@Composable
fun ChooseOpenTime(
    modifier: Modifier = Modifier,
    openTimeInMinutes: Int,
    updateUiState: (Int) -> Unit = {}
) {
    EditScreenOutlinedField(
        mainText = stringResource(R.string.open_time),
        textFieldValue = openTimeInMinutes.toString(),
        onValueChange = {
            if (it.isDigitsOnly()) {
                val num = it.toIntOrNull()
                if (num == null) {
                    updateUiState(0)
                } else {
                    updateUiState(num)
                }
            }
        },
        keyboardType = KeyboardType.Number,
        suffixText = stringResource(R.string.minutes),
        modifier = modifier
    )
}

@Composable
fun ChooseWaitEnterButton(
    modifier: Modifier = Modifier,
    checked: Boolean,
    updateUiState: (Boolean) -> Unit = {}
) {
    EditScreenCard(modifier = modifier) {
        Text(
            text = stringResource(R.string.choose_wait_enter_button),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.weight(1f))
        Checkbox(
            checked = checked,
            onCheckedChange = { updateUiState(it) },
            modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_small))
        )
    }
}

@Composable
fun ChooseBreathingCycleDuration(
    modifier: Modifier = Modifier,
    breathingCycleDuration: Int,
    updateUiState: (Int) -> Unit = {}
) {
    EditScreenOutlinedField(
        mainText = stringResource(R.string.breathing_cycle_duration),
        textFieldValue = breathingCycleDuration.toString(),
        onValueChange = {
            if (it.isDigitsOnly()) {
                val num = it.toIntOrNull()
                if (num == null) {
                    updateUiState(0)
                } else {
                    updateUiState(num)
                }
            }
        },
        keyboardType = KeyboardType.Number,
        suffixText = stringResource(R.string.seconds),
        modifier = modifier
    )
}

@Composable
fun ChooseBreathingCycleNumber(
    modifier: Modifier = Modifier,
    breathingCycleNumber: Int,
    updateUiState: (Int) -> Unit = {}
) {
    EditScreenOutlinedField(
        mainText = stringResource(R.string.number_of_breathing_cycles),
        textFieldValue = breathingCycleNumber.toString(),
        onValueChange = {
            if (it.isDigitsOnly()) {
                val num = it.toIntOrNull()
                if (num == null) {
                    updateUiState(0)
                } else {
                    updateUiState(num)
                }
            }
        },
        keyboardType = KeyboardType.Number,
        modifier = modifier
    )
}

@Composable
fun ChoosePomodoroWorkTime(
    modifier: Modifier = Modifier,
    workTimeInMinutes: Int,
    updateUiState: (Int) -> Unit = {}
) {
    EditScreenOutlinedField(
        mainText = stringResource(R.string.work_time),
        textFieldValue = workTimeInMinutes.toString(),
        onValueChange = {
            if (it.isDigitsOnly()) {
                val num = it.toIntOrNull()
                if (num == null) {
                    updateUiState(0)
                } else {
                    updateUiState(num)
                }
            }
        },
        keyboardType = KeyboardType.Number,
        suffixText = stringResource(R.string.minutes),
        modifier = modifier
    )
}

@Composable
fun ChoosePomodoroRestTime(
    modifier: Modifier = Modifier,
    restTimeInMinutes: Int,
    updateUiState: (Int) -> Unit = {}
) {
    EditScreenOutlinedField(
        mainText = stringResource(R.string.rest_time),
        textFieldValue = restTimeInMinutes.toString(),
        onValueChange = {
            if (it.isDigitsOnly()) {
                val num = it.toIntOrNull()
                if (num == null) {
                    updateUiState(0)
                } else {
                    updateUiState(num)
                }
            }
        },
        keyboardType = KeyboardType.Number,
        suffixText = stringResource(R.string.minutes),
        modifier = modifier
    )
}

@Composable
fun ChoosePomodoroSessionNumber(
    modifier: Modifier = Modifier,
    pomodoroSessionNumber: Int,
    updateUiState: (Int) -> Unit = {},
    isPomodoroSessionNumberInvalid: Boolean = false
) {
    EditScreenOutlinedField(
        mainText = stringResource(R.string.number_of_pomodoro_sessions),
        textFieldValue = pomodoroSessionNumber.toString(),
        onValueChange = {
            if (it.isDigitsOnly()) {
                val num = it.toIntOrNull()
                if (num == null) {
                    updateUiState(0)
                } else {
                    updateUiState(num)
                }
            }
        },
        keyboardType = KeyboardType.Number,
        isError = isPomodoroSessionNumberInvalid,
        errorMessage = stringResource(R.string.pomodoro_session_number_invalid),
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun ChooseEnablePreview() {
    ZenwellTheme {
        var isChecked by remember { mutableStateOf(false) }
        ChooseEnable(
            checked = isChecked,
            updateUiState = { isChecked = it }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChooseScheduleTitlePreview() {
    ZenwellTheme {
        ChooseScheduleTitle(title = "Schedule 1")
    }
}

@Preview(showBackground = true)
@Composable
fun ChooseMessagePreview() {
    ZenwellTheme {
        ChooseMessage(message = "This app is blocked.")
    }
}

@Preview(showBackground = true)
@Composable
fun ChooseNotificationTimePreview() {
    ZenwellTheme {
        ChooseNotificationTime(notificationTime = 2, isNotificationTimeInvalid = false)
    }
}

@Preview(showBackground = true)
@Composable
fun ChooseNotificationTimePreview2() {
    ZenwellTheme {
        ChooseNotificationTime(notificationTime = 2, isNotificationTimeInvalid = true)
    }
}

@Preview(showBackground = true)
@Composable
fun ChooseWaitTimePreview() {
    ZenwellTheme {
        ChooseWaitTime(
            waitTimeInSeconds = 10,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChooseOpenTimePreview() {
    ZenwellTheme {
        ChooseOpenTime(
            openTimeInMinutes = 999999999,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChooseWaitEnterPreview() {
    ZenwellTheme {
        var isChecked by remember { mutableStateOf(false) }
        ChooseWaitEnterButton(
            checked = isChecked,
            updateUiState = { isChecked = it }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChooseBreathingCycleDurationPreview() {
    ZenwellTheme {
        ChooseBreathingCycleDuration(
            breathingCycleDuration = 10,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChooseBreathingCycleNumberPreview() {
    ZenwellTheme {
        ChooseBreathingCycleNumber(
            breathingCycleNumber = 999999999,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChoosePomodoroWorkTimePreview() {
    ZenwellTheme {
        ChoosePomodoroWorkTime(
            workTimeInMinutes = 25,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChoosePomodoroRestTimePreview() {
    ZenwellTheme {
        ChoosePomodoroRestTime(
            restTimeInMinutes = 5
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChoosePomodoroSessionNumberPreview() {
    ZenwellTheme {
        ChoosePomodoroSessionNumber(
            pomodoroSessionNumber = 5
        )
    }
}