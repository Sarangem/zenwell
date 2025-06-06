package com.sarangem.zenwell.ui.editscreen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.ZenwellTheme

@Composable
fun ChooseScheduleTitle(
    modifier: Modifier = Modifier,
    title: String,
    updateUiState: (String) -> Unit = {}
) {
    EditScreenCard(modifier = modifier) {
        Text(
            text = stringResource(R.string.schedule_title),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.weight(1f))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(0.7f),
            value = title,
            singleLine = true,
            shape = MaterialTheme.shapes.large,
            onValueChange = { updateUiState(it) },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            )
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
