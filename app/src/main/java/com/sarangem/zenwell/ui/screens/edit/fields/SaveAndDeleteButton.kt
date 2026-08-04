/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.edit.fields

import android.os.Build
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.screens.home.SkipGuideButton
import com.sarangem.zenwell.ui.sequenceshowcase.LocalSequenceShowcaseState
import com.sarangem.zenwell.ui.sequenceshowcase.sequenceShowcaseTarget
import com.sarangem.zenwell.ui.theme.sizing

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SaveAndDeleteButton(
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    onSave: () -> Unit = {},
    onDelete: () -> Unit = {},
    goBack: () -> Unit = {},
    firstEntry: Boolean = false,
    setAsExistingUser: () -> Unit = {}
) {
    val showcaseState = LocalSequenceShowcaseState.current
    val context = LocalContext.current
    val colorScheme = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            dynamicDarkColorScheme(context)
        } else {
            darkColorScheme()
        }
    }
    var checkedSave by remember { mutableStateOf(false) }
    var checkedDelete by remember { mutableStateOf(false) }

    HorizontalFloatingToolbar(
        expanded = true,
        modifier = Modifier.padding(horizontal = MaterialTheme.sizing.small)
    ) {

        // SAVE BUTTON
        ToggleButton(
            checked = checkedSave,
            onCheckedChange = {
                if(!isError){
                    checkedSave = false
                    showcaseState.dismiss()
                    if (firstEntry) setAsExistingUser()
                    onSave()
                    goBack()
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
                    containerColor = colorScheme.primaryContainer,
                    checkedContainerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimaryContainer,
                    checkedContentColor = colorScheme.onPrimary
                )
            },
            modifier = modifier
                .weight(1.5f)
                .then(showcase4Modifier(setAsExistingUser)),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(4.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.filled_save),
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
                containerColor = colorScheme.errorContainer,
                checkedContainerColor = colorScheme.error,
                contentColor = colorScheme.onErrorContainer,
                checkedContentColor = colorScheme.onError
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.filled_delete),
                contentDescription = stringResource(R.string.delete),
                modifier = Modifier.padding(4.dp)
            )
        }
    }

    if (checkedDelete) {
        ShowConfirmDialog(
            icon = R.drawable.filled_delete,
            title = stringResource(R.string.delete),
            description = stringResource(R.string.delete_confirmation),
            onDismiss = { checkedDelete = false },
            onConfirm = {
                checkedDelete = false
                onDelete()
                goBack()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
val showcase4Modifier: @Composable (skipGuide: () -> Unit) -> Modifier = { skip ->
    Modifier.sequenceShowcaseTarget(
        index = 4,
        shape = ButtonGroupDefaults.connectedLeadingButtonShapes().shape,
        shapeMargin = 0.dp,
        backgroundAlpha = 0.9f,
        fixedContent = { SkipGuideButton(skip) }
    ) {
        Text(
            text = stringResource(R.string.showcase_4),
            style = MaterialTheme.typography.headlineMedium,
            color = darkColorScheme().onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}