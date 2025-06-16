package com.sarangem.zenwell.ui.editscreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.text.isDigitsOnly
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.ZenwellTheme

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