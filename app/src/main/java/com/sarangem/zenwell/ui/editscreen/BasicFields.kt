package com.sarangem.zenwell.ui.editscreen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
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
        ChooseNotificationTime(notificationTime = 2)
    }
}