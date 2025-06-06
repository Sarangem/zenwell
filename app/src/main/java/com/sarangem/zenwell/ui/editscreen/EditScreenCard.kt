package com.sarangem.zenwell.ui.editscreen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.sarangem.zenwell.R

@Composable
fun EditScreenCard(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit = {}
) {
    Card(
        modifier = modifier.padding(dimensionResource(R.dimen.padding_small)),
        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.card_elevation))
    ) {
        Row(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}