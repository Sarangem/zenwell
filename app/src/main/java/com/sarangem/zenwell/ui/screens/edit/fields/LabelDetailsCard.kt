/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.edit.fields

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sarangem.zenwell.R

data class LabelState(
    @field:StringRes val title: Int,
    val isSelected: Boolean,
    val isVisible: Boolean = true,
    val onSelectChange: (Boolean) -> Unit = {}
)
@Composable
fun LabelDetailsCard(
    @StringRes mainText: Int,
    labelList: List<LabelState> = listOf(),
    isError: Boolean = false,
    errorMessage: String = ""
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(0.dp)
    ) {
        Text(
            text = stringResource(mainText),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
        )
        if (isError) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(
                    start = dimensionResource(R.dimen.padding_small),
                    end = dimensionResource(R.dimen.padding_small)
                )
            )
        }
        FlowRow(
            modifier = Modifier.padding(
                start = dimensionResource(R.dimen.padding_small),
                end = dimensionResource(R.dimen.padding_small),
                bottom = dimensionResource(R.dimen.padding_small)
            ).animateContentSize(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        ) {
            labelList.forEach { label ->
                AnimatedVisibility(
                    visible = label.isVisible,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    FilterChip(
                        selected = label.isSelected,
                        onClick = { label.onSelectChange(!label.isSelected) },
                        label = {
                            Text(
                                text = stringResource(label.title),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        border = BorderStroke(
                            width = 0.5.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}