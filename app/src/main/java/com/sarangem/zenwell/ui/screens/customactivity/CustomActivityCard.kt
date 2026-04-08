package com.sarangem.zenwell.ui.screens.customactivity

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.screens.edit.fields.ShowConfirmDialog
import com.sarangem.zenwell.ui.theme.Green500
import com.sarangem.zenwell.utils.PackageInfo

@Composable
fun CustomActivityCard(
    modifier: Modifier,
    appName: CustomActivityUiState,
    installedAppsList: List<PackageInfo>,
    updateAppNames: (CustomActivityUiState) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    if (showDeleteDialog) {
        ShowConfirmDialog(
            icon = Icons.Filled.Delete,
            title = stringResource(R.string.delete),
            description = stringResource(R.string.delete_confirmation_custom_view),
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                showDeleteDialog = false
                onDelete()
            }
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_small))
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
            ) {
                AppListDropdownMenu(
                    modifier = Modifier.fillMaxWidth(),
                    currentApp = appName.packageName,
                    onCurrentAppChange = {
                        updateAppNames(appName.copy(packageName = it))
                    },
                    installedAppsList = installedAppsList
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (!appName.isSaved) onSave() },
                        shape = CircleShape,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (appName.isSaved) Color.Gray else Green500
                        )
                    ) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = stringResource(R.string.save_custom_view),
                            tint = Color.White
                        )
                    }
                    Spacer(Modifier.width(dimensionResource(R.dimen.padding_small)))
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
                            Icons.Filled.Delete,
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

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
            ) {
                OutlinedTextField(
                    value = appName.viewTitle,
                    onValueChange = { updateAppNames(appName.copy(viewTitle = it)) },
                    placeholder = { Text(stringResource(R.string.view_title)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = appName.viewId,
                    onValueChange = { updateAppNames(appName.copy(viewId = it)) },
                    placeholder = { Text(stringResource(R.string.view_id)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListDropdownMenu(
    modifier: Modifier,
    currentApp: String,
    onCurrentAppChange: (String) -> Unit,
    installedAppsList: List<PackageInfo>,
) {
    var expanded by remember { mutableStateOf(false) }
    val currentApp = installedAppsList.firstOrNull { it.packageName == currentApp }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            modifier = modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            readOnly = true,
            value = TextFieldValue(currentApp?.appName ?: stringResource(R.string.select_app)),
            onValueChange = {},
            leadingIcon = {
                Image(
                    painter = rememberDrawablePainter(currentApp?.icon),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_small))
                        .clip(RoundedCornerShape(dimensionResource(R.dimen.padding_small)))
                        .size(dimensionResource(R.dimen.image_size))
                )
            },
            trailingIcon = @Composable {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = !expanded },
            shape = MaterialTheme.shapes.large
        ) {
            installedAppsList.forEach {
                DropdownMenuItem(
                    text = {
                        Text(
                            it.appName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    onClick = {
                        onCurrentAppChange(it.packageName)
                        expanded = !expanded
                    },
                    leadingIcon = {
                        Image(
                            painter = rememberDrawablePainter(it.icon),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(dimensionResource(R.dimen.padding_small))
                                .clip(RoundedCornerShape(dimensionResource(R.dimen.padding_small)))
                                .size(dimensionResource(R.dimen.image_size))
                        )
                    },
                )
            }
        }
    }

}