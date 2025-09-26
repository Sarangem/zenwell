package com.sarangem.zenwell.ui.editscreen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.sarangem.zenwell.R

@Composable
fun ChooseEnable(
    modifier: Modifier = Modifier,
    checked: Boolean,
    updateUiState: (Boolean) -> Unit = {}
) {
    DetailsCard(
        modifier = modifier.padding(dimensionResource(R.dimen.padding_small))
    ) {
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
fun ChooseWaitEnterButton(
    modifier: Modifier = Modifier,
    checked: Boolean,
    updateUiState: (Boolean) -> Unit = {}
) {
    DetailsCard(
        modifier = modifier,
        isStacked = true
    ) {
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