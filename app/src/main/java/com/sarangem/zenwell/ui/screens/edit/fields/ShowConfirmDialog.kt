/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.edit.fields

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.sarangem.zenwell.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowConfirmDialog(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    description: String,
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    BasicAlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = dimensionResource(R.dimen.padding_small),
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_large))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .padding(
                            top = dimensionResource(R.dimen.padding_large),
                            bottom = dimensionResource(R.dimen.padding_small)
                        )
                )
                Text(
                    modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_large)),
                    text = title,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(Modifier.size(dimensionResource(R.dimen.padding_large)))
                Row {
                    TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onConfirm) { Text(stringResource(R.string.confirm)) }
                }
            }
        }
    }
}