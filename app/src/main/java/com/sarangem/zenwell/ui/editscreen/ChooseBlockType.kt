package com.sarangem.zenwell.ui.editscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.BlockType
import com.sarangem.zenwell.ui.theme.ZenwellTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseBlockType(
    modifier: Modifier = Modifier,
    blockType: BlockType,
    updateUiState: (BlockType) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    EditScreenCard(modifier = modifier) {
        Text(
            text = stringResource(R.string.choose_block_type),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(0.5f)
        )
        ExposedDropdownMenuBox(
            modifier = Modifier.weight(0.8f),
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                readOnly = true,
                value = TextFieldValue(stringResource(blockType.title)),
                onValueChange = {},
                shape = MaterialTheme.shapes.large,
                trailingIcon = @Composable {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = !expanded },
            ) {
                BlockType.entries.forEach {
                    DropdownMenuItem(
                        text = { Text(stringResource(it.title)) },
                        onClick = {
                            updateUiState(it)
                            expanded = !expanded
                        }
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ChooseBlockTypePreview() {
    ZenwellTheme {
        var blockType by remember { mutableStateOf(BlockType.FullBlock) }
        Column {
            Spacer(Modifier.height(10.dp))
            ChooseBlockType(
                blockType = blockType,
                updateUiState = { blockType = it }
            )
            Spacer(Modifier.height(250.dp))
        }
    }
}