package com.sarangem.zenwell.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.sarangem.zenwell.R
import com.sarangem.zenwell.lock.AppPasswordLock
import com.sarangem.zenwell.ui.theme.sizing

@Composable
fun PasswordSettingsSection(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var hasPassword by remember { mutableStateOf(AppPasswordLock.hasPassword(context)) }
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier) {
        Text(
            stringResource(R.string.app_password_section),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = MaterialTheme.sizing.extraSmall)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
        ) {
            SettingsActionCard(
                modifier = Modifier.fillMaxWidth(),
                icon = R.drawable.filled_lock_protection,
                title = stringResource(R.string.app_password_title),
                description = stringResource(
                    if (hasPassword) R.string.app_password_on else R.string.app_password_off
                ),
                onClick = { showDialog = true }
            )
        }
    }

    if (showDialog) {
        PasswordDialog(
            hasPassword = hasPassword,
            onDismiss = { showDialog = false },
            onChanged = {
                hasPassword = AppPasswordLock.hasPassword(context)
                showDialog = false
            }
        )
    }
}

@Composable
private fun PasswordDialog(
    hasPassword: Boolean,
    onDismiss: () -> Unit,
    onChanged: () -> Unit
) {
    val context = LocalContext.current
    var current by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var errorRes by remember { mutableStateOf<Int?>(null) }

    fun save() {
        errorRes = when {
            hasPassword && !AppPasswordLock.verify(context, current) -> R.string.app_password_wrong
            newPassword.length < AppPasswordLock.MIN_LENGTH -> R.string.app_password_too_short
            newPassword != confirm -> R.string.app_password_mismatch
            else -> null
        }
        if (errorRes == null) {
            AppPasswordLock.setPassword(context, newPassword)
            onChanged()
        }
    }

    fun remove() {
        if (AppPasswordLock.verify(context, current)) {
            AppPasswordLock.removePassword(context)
            onChanged()
        } else {
            errorRes = R.string.app_password_wrong
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(
                    if (hasPassword) R.string.app_password_change else R.string.app_password_set
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (hasPassword) {
                    PasswordField(current, { current = it; errorRes = null },
                        stringResource(R.string.app_password_current))
                }
                PasswordField(newPassword, { newPassword = it; errorRes = null },
                    stringResource(R.string.app_password_new))
                PasswordField(confirm, { confirm = it; errorRes = null },
                    stringResource(R.string.app_password_confirm))
                errorRes?.let {
                    Text(stringResource(it), color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { save() }) { Text(stringResource(R.string.app_password_save)) }
        },
        dismissButton = {
            Row {
                if (hasPassword) {
                    TextButton(onClick = { remove() }) {
                        Text(stringResource(R.string.app_password_remove))
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.app_password_cancel))
                }
            }
        }
    )
}

@Composable
private fun PasswordField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
}
