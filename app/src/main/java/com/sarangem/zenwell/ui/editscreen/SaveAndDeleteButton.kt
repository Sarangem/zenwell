package com.sarangem.zenwell.ui.editscreen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.focusscreen.ShowConfirmDialog
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
                if(!isError){
                    checkedSave = false
                    onSave()
                    checkedSave = true
                }
            },
            shapes = ButtonGroupDefaults.connectedLeadingButtonShapes(),
            colors = if(isError){
                ToggleButtonDefaults.toggleButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceDim,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
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
            shapes = ButtonGroupDefaults.connectedTrailingButtonShapes(),
            modifier = Modifier.weight(0.5f),
            colors = ToggleButtonDefaults.toggleButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                checkedContainerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                checkedContentColor = MaterialTheme.colorScheme.onError
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = stringResource(R.string.delete),
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.padding(4.dp)
            )
        }
        if (checkedDelete) {
            ShowConfirmDialog(
                icon = Icons.Filled.Delete,
                headingText = stringResource(R.string.delete),
                bodyText = stringResource(R.string.delete_confirmation),
                onDismiss = { checkedDelete = false },
                onConfirm = {
                    checkedDelete = false
                    onDelete()
                }
            )
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
