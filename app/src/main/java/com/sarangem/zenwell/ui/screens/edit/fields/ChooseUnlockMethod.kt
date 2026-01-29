package com.sarangem.zenwell.ui.screens.edit.fields

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import com.sarangem.zenwell.R
import com.sarangem.zenwell.model.UnlockMethod

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseUnlockMethod(
    unlockMethod: UnlockMethod,
    updateValue: (UnlockMethod) -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }
    DetailsCard {
        Column(Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.unlock_method),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        ExposedDropdownMenuBox(
            modifier = Modifier.weight(2f),
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                readOnly = true,
                value = TextFieldValue(stringResource(unlockMethod.title)),
                onValueChange = {},
                shape = MaterialTheme.shapes.large,
                trailingIcon = @Composable {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = !expanded },
                shape = MaterialTheme.shapes.large
            ) {
                UnlockMethod.entries.forEach {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(it.title),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        onClick = {
                            updateValue(it)
                            expanded = !expanded
                        }
                    )
                }
            }
        }
    }
}