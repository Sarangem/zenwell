package com.sarangem.zenwell.ui.editscreen.details

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.sarangem.zenwell.R


@Composable
fun DetailsCard(
    modifier: Modifier = Modifier,
    isStacked: Boolean = false,
    content: @Composable RowScope.() -> Unit = {}
) {
    Card(
        modifier = modifier,
        shape = if (isStacked) RoundedCornerShape(0.dp) else MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()
        }
    }
}