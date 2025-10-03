package com.sarangem.zenwell.ui.editscreen.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsCardWithSlider(
    modifier: Modifier = Modifier,
    isStacked: Boolean = false,
    mainText: String,
    minValue: Int,
    maxValue: Int,
    updateValue: (ClosedFloatingPointRange<Float>) -> Unit = {},
) {
    DetailsCard(
        modifier = modifier,
        isStacked = isStacked
    ) {
        Text(
            text = mainText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(0.5f)
        )
        Spacer(Modifier.weight(0.1f))
        Column(Modifier.weight(0.8f)) {
            Row {
                Text("1")
                Spacer(Modifier.weight(1f))
                Text("10")
            }
            RangeSlider(
                value = minValue.toFloat()..maxValue.toFloat(),
                onValueChange = {
                    updateValue(it)
                },
                valueRange = 1f..10f,
                steps = 8,
            )
        }
    }
}