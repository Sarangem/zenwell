package com.sarangem.zenwell.ui.editscreen.details

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.text.isDigitsOnly

@Composable
fun DetailsCardWithNumberField(
    modifier: Modifier = Modifier,
    isStacked: Boolean = false,
    mainText: String = "",
    textFieldValue: Int,
    updateSchedule: (Int) -> Unit = {},
    suffixText: String = "",
    isError: Boolean = false,
    errorMessage: String = ""
) {
    DetailsCardWithTextField(
        modifier = modifier,
        isStacked = isStacked,
        mainText = mainText,
        textFieldValue = textFieldValue.toString(),
        onValueChange = { num ->
            if (num.isDigitsOnly()) {
                val num = num.toIntOrNull()
                if (num == null) {
                    updateSchedule(0)
                } else {
                    updateSchedule(num)
                }
            }
        },
        keyboardType = KeyboardType.Number,
        suffixText = suffixText,
        isError = isError,
        errorMessage = errorMessage
    )
}