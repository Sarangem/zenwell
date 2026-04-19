/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.edit.fields

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.sarangem.zenwell.R

@Composable
fun ChooseActiveSwitch(
    isActive: Boolean,
    updateValue: (Boolean) -> Unit = {}
) {
    DetailsCard {
        Text(
            text = stringResource(R.string.active),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.weight(1f))
        Switch(
            checked = isActive,
            onCheckedChange = updateValue,
            modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_small))
        )
    }
}