package com.sarangem.zenwell.ui.screens.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.ZenwellTheme

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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DetailsCardWithSwitch(
    modifier: Modifier = Modifier,
    isStacked: Boolean = false,
    mainText: String,
    checked: Boolean,
    motionScheme: MotionScheme = MotionScheme.standard(),
    onCheckedChange: (Boolean) -> Unit = {}
) {
    DetailsCard(
        modifier = modifier,
        isStacked = isStacked
    ) {
        Text(
            text = mainText,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.weight(1f))
        ZenwellTheme(motionScheme = motionScheme) {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_small))
            )
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
            .clip(MaterialTheme.shapes.medium),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
    ) {
        content()
    }
}

@Composable
fun DetailsCardWithNumberField(
    modifier: Modifier = Modifier,
    isStacked: Boolean = false,
    mainText: String = "",
    textFieldValue: Int,
    updateSchedule: (Int) -> Unit = {},
    suffixText: String = "",
    isError: Boolean = false,
    errorMessage: String = ""
) {
    DetailsCardWithTextField(
        modifier = modifier,
        isStacked = isStacked,
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

@Composable
fun DetailsCardWithTextField(
    modifier: Modifier = Modifier,
    isStacked: Boolean = false,
    mainText: String = "",
    textFieldValue: String = "",
    onValueChange: (String) -> Unit = {},
    keyboardType: KeyboardType = KeyboardType.Text,
    suffixText: String = "",
    isError: Boolean = false,
    errorMessage: String = ""
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
        OutlinedTextField(
            modifier = Modifier.weight(0.8f),
            value = textFieldValue,
            shape = MaterialTheme.shapes.large,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = keyboardType
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
                        Text(                            text = label.title,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                )
            }
        }
    }
}