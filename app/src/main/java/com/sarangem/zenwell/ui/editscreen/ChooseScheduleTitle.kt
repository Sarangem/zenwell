package com.sarangem.zenwell.ui.editscreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.ZenwellTheme

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


@Preview(showBackground = true)
@Composable
fun ChooseScheduleTitlePreview() {
    ZenwellTheme {
        ChooseScheduleTitle(title = "Schedule 1")
    }
}
