package com.sarangem.zenwell.ui.editscreen.details

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sarangem.zenwell.R

data class LabelState(
    val title: String,
    val isSelected: Boolean,
    val onSelectChange: (Boolean) -> Unit = {}
)

@Composable
fun LabelDetailsCard(
    modifier: Modifier = Modifier,
    mainText: String,
    labelList: List<LabelState> = listOf(),
    isError: Boolean = false,
    errorMessage: String = ""
) {
    Card(
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(0.dp)
    ) {
        Text(
            text = mainText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
        )
        if (isError) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )
        }
        FlowRow(
            modifier = Modifier.padding(
                start = dimensionResource(R.dimen.padding_small),
                end = dimensionResource(R.dimen.padding_small),
                bottom = dimensionResource(R.dimen.padding_small)
            ),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        ) {
            labelList.forEach { label ->
                FilterChip(
                    selected = label.isSelected,
                    onClick = { label.onSelectChange(!label.isSelected) },
                    label = {
                        Text(
                            text = label.title,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                )
            }
        }
    }
}