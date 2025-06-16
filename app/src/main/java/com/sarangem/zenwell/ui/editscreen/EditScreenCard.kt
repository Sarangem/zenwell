package com.sarangem.zenwell.ui.editscreen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()
        }
    }
}

@Composable
fun EditScreenOutlinedField(
    modifier: Modifier = Modifier,
    mainText: String = "",
    textFieldValue: String = "",
    onValueChange: (String) -> Unit = {},
    keyboardType: KeyboardType = KeyboardType.Text,
    suffixText: String = ""
){
    EditScreenCard(modifier = modifier) {
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
            }
        )
    }
}