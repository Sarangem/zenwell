/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

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
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.sarangem.zenwell.R
import com.sarangem.zenwell.model.UnlockMethod
import com.sarangem.zenwell.ui.screens.home.SkipGuideButton
import com.sarangem.zenwell.ui.sequenceshowcase.SequenceShowcaseScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseUnlockMethod(
    modifier: Modifier = Modifier,
    unlockMethod: UnlockMethod,
    updateValue: (UnlockMethod) -> Unit = {},
    onShowcaseClick: () -> Unit = {},
    onShowcaseDismiss: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    DetailsCard(modifier) {
        Column(Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.unlock_method),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        ExposedDropdownMenuBox(
            modifier = Modifier.weight(2f),
            expanded = expanded,
            onExpandedChange = {
                onShowcaseClick()
                expanded = !expanded
            },
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
                onDismissRequest = {
                    onShowcaseDismiss()
                    expanded = !expanded
                },
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
                            onShowcaseDismiss()
                            expanded = !expanded
                        }
                    )
                }
            }
        }
    }
}

val showcase3Modifier: @Composable SequenceShowcaseScope.(skipGuide: () -> Unit) -> Modifier = { skip ->
    Modifier.sequenceShowcaseTarget(
        index = 3,
        shape = MaterialTheme.shapes.medium,
        shapeMargin = 0.dp,
        backgroundAlpha = 0.9f,
        fixedContent = { SkipGuideButton(skip) }
    ) {
        Text(
            text = stringResource(R.string.showcase_3),
            style = MaterialTheme.typography.headlineMedium,
            color = darkColorScheme().onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}