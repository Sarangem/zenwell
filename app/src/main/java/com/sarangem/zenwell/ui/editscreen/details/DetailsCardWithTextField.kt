package com.sarangem.zenwell.ui.editscreen.details

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun DetailsCardWithTextField(
    modifier: Modifier = Modifier,
    isStacked: Boolean = false,
    mainText: String = "",
    textFieldValue: String = "",
    onValueChange: (String) -> Unit = {},
    keyboardType: KeyboardType = KeyboardType.Text,
    suffixText: String = "",
    isError: Boolean = false,
    errorMessage: String = ""
) {
    DetailsCard(
        modifier = modifier,
        isStacked = isStacked
    ) {
        Text(
            text = mainText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(0.5f)
        )
        OutlinedTextField(
            modifier = Modifier.weight(0.8f),
            value = textFieldValue,
            shape = MaterialTheme.shapes.large,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = keyboardType
            ),
            suffix = {
                Text(suffixText)
            },
            isError = isError,
            label = {
                if (isError) {
                    Text(errorMessage)
                }
            }
        )
    }
}

