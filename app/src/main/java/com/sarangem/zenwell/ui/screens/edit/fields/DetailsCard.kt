/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.edit.fields

import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.sarangem.zenwell.R

@Composable
fun DetailsCard(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit = {}
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()
        }
    }
}

@Composable
fun DetailsCardColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    Column(
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding_small))
            .clip(MaterialTheme.shapes.medium)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
    ) {
        content()
    }
}

@Composable
fun DetailsCardWithTextField(
    @StringRes mainText: Int,
    textFieldValue: String = "",
    @StringRes suffixText: Int? = null,
    isError: Boolean = false,
    @StringRes errorMessage: Int? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit = {},
) {
    DetailsCard {
        Text(
            text = stringResource(mainText),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            modifier = Modifier.weight(2f),
            value = textFieldValue,
            shape = MaterialTheme.shapes.large,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = keyboardType
            ),
            suffix = {
                Text(
                    suffixText?.let { stringResource(it) } ?: ""
                )
            },
            isError = isError,
            label = {
                if (isError) {
                    errorMessage?.let { stringResource(it) } ?: ""
                }
            }
        )
    }
}

@Composable
fun DetailsCardWithNumberField(
    @StringRes mainText: Int,
    textFieldValue: Int,
    @StringRes suffixText: Int? = null,
    isError: Boolean = false,
    @StringRes errorMessage: Int? = null,
    updateSchedule: (Int) -> Unit = {},
) {
    DetailsCardWithTextField(
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

@Composable
fun DetailsCardWithRangeNumberField(
    @StringRes mainText: Int,
    firstFieldValue: Int,
    lastFieldValue: Int,
    updateFirstValue: (Int) -> Unit = {},
    updateLastValue: (Int) -> Unit = {},
    suffixText: String = "",
    isError: Boolean = false,
    errorMessage: String = ""
) {
    DetailsCard {
        Text(
            text = stringResource(mainText),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Row(Modifier.weight(2f)) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = firstFieldValue.toString(),
                shape = MaterialTheme.shapes.large,
                onValueChange = { num ->
                    if (num.isDigitsOnly()) {
                        val num = num.toIntOrNull()
                        if (num == null) {
                            updateFirstValue(0)
                        } else {
                            updateFirstValue(num)
                        }
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
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
            Text(
                text = stringResource(R.string.to),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_small))
                    .align(Alignment.CenterVertically)
            )
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = lastFieldValue.toString(),
                shape = MaterialTheme.shapes.large,
                onValueChange = { num ->
                    if (num.isDigitsOnly()) {
                        val num = num.toIntOrNull()
                        if (num == null) {
                            updateLastValue(0)
                        } else {
                            updateLastValue(num)
                        }
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
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
}