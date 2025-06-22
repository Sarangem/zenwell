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