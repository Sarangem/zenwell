package com.sarangem.zenwell.ui.editscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.ZenwellTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SaveAndDeleteButton(
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    onSave: () -> Unit = {},
    onDelete: () -> Unit = {},
) {
    var checkedSave by remember { mutableStateOf(false) }
    var checkedDelete by remember { mutableStateOf(false) }

    HorizontalFloatingToolbar(
        expanded = true,
        modifier = modifier
    ) {

        // SAVE BUTTON
        ToggleButton(
            checked = checkedSave,
            onCheckedChange = {
                checkedSave = false
                onSave()
                checkedSave = true
            },
            shapes = ToggleButtonDefaults.shapes(
                shape = ButtonGroupDefaults.connectedLeadingButtonShape,
                pressedShape = ButtonGroupDefaults.connectedLeadingButtonPressShape,
                checkedShape = ButtonGroupDefaults.connectedButtonCheckedShape
            ),
            colors = if(isError){
                ToggleButtonDefaults.toggleButtonColors(MaterialTheme.colorScheme.surfaceDim)
            } else {
                ToggleButtonDefaults.toggleButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    checkedContainerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    checkedContentColor = MaterialTheme.colorScheme.onPrimary
                )
            },
            modifier = Modifier.weight(1.5f),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Save,
                    contentDescription = null,
                )
                Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                Text(
                    text = stringResource(R.string.save),
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 18.sp
                )
            }
        }

        // SPACE
        Spacer(Modifier.width(ButtonGroupDefaults.ConnectedSpaceBetween))

        // DELETE BUTTON
        ToggleButton(
            checked = checkedDelete,
            onCheckedChange = { checkedDelete = true },
            shapes = ToggleButtonDefaults.shapes(
                shape = ButtonGroupDefaults.connectedTrailingButtonShape,
                pressedShape = ButtonGroupDefaults.connectedTrailingButtonPressShape,
                checkedShape = ButtonGroupDefaults.connectedButtonCheckedShape
            ),
            modifier = Modifier.weight(0.5f),
            colors = if(isError){
                ToggleButtonDefaults.toggleButtonColors(MaterialTheme.colorScheme.surfaceDim)
            } else {
                ToggleButtonDefaults.toggleButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    checkedContainerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    checkedContentColor = MaterialTheme.colorScheme.onError
                )
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = stringResource(R.string.delete),
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.padding(4.dp)
            )
        }
        if (checkedDelete) {
            ShowDeleteDialog(
                onDismiss = { checkedDelete = false },
                onDeleteConfirm = {
                    checkedDelete = false
                    onDelete()
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDeleteDialog(
    onDismiss: () -> Unit = {},
    onDeleteConfirm: () -> Unit = {}
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = dimensionResource(R.dimen.padding_small),
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_large))
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .padding(
                            top = dimensionResource(R.dimen.padding_large),
                            bottom = dimensionResource(R.dimen.padding_small)
                        )
                )
                Text(
                    modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_large)),
                    text = stringResource(R.string.delete),
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = stringResource(R.string.delete_confirmation),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(Modifier.size(dimensionResource(R.dimen.padding_large)))
                Row {
                    TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDeleteConfirm) { Text(stringResource(R.string.delete)) }
                }
            }
        }
    }


}


@Preview(showBackground = true)
@Composable
fun SaveButtonPreview() {
    ZenwellTheme {
        SaveAndDeleteButton()
    }
}
