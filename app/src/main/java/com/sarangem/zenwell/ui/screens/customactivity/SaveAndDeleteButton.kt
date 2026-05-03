/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.customactivity

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.screens.edit.fields.ShowConfirmDialog
import com.sarangem.zenwell.ui.theme.Green500
import com.sarangem.zenwell.ui.theme.sizing

@Composable
fun SaveAndDeleteButton(
    isSaved: Boolean,
    onSave: () -> Unit,
    onDelete: () -> Unit
){
    var showDeleteDialog by remember { mutableStateOf(false) }
    if (showDeleteDialog) {
        ShowConfirmDialog(
            icon = R.drawable.filled_delete,
            title = stringResource(R.string.delete),
            description = stringResource(R.string.delete_confirmation_custom_view),
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                showDeleteDialog = false
                onDelete()
            }
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { if (!isSaved) onSave() },
            shape = CircleShape,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = if (isSaved) Color.Gray else Green500
            )
        ) {
            Icon(
                painterResource(R.drawable.filled_check),
                contentDescription = stringResource(R.string.save_custom_view),
                tint = Color.White
            )
        }
        Spacer(Modifier.width(MaterialTheme.sizing.small))
        IconButton(
            onClick = { showDeleteDialog = true },
            shape = CircleShape,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = if(isSystemInDarkTheme()){
                    MaterialTheme.colorScheme.errorContainer
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        ) {
            Icon(
                painterResource(R.drawable.filled_delete),
                contentDescription = stringResource(R.string.delete),
                tint = if(isSystemInDarkTheme()){
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onError
                }
            )
        }
    }
}