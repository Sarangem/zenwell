package com.sarangem.zenwell.ui.editscreen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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

@Preview(showBackground = true)
@Composable
fun ChooseEnablePreview() {
    ZenwellTheme {
        var isChecked by remember { mutableStateOf(false) }
        ChooseEnable(
            checked = isChecked,
            updateUiState = { isChecked = it}
        )
    }
}