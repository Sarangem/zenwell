/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.settings

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ZenwellApplication
import com.sarangem.zenwell.model.BackupData
import com.sarangem.zenwell.ui.theme.sizing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

@Composable
fun DataTransferCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = CoroutineScope(Dispatchers.IO)
    val app = context.applicationContext as ZenwellApplication
    val json = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }


    val createBackup = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult

        coroutineScope.launch {
            try {

                val jsonString = json.encodeToString(app.container.getAllData())
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    BufferedWriter(OutputStreamWriter(outputStream)).use { writer ->
                        writer.write(jsonString)
                    }
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, R.string.backup_completed, Toast.LENGTH_SHORT).show()
                }

            } catch (e: IOException) {

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, R.string.backup_failed, Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    val restoreBackup = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        coroutineScope.launch {

            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val jsonString = BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        reader.readText()
                    }
                    val restoredData = json.decodeFromString<List<BackupData>>(jsonString)
                    app.container.restoreAllData(restoredData)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, R.string.data_restore_completed, Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, R.string.data_restore_failed, Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    DataTransferCard(
        modifier = modifier,
        createBackup = {
            createBackup.launch("zenwell_backup.json")
        },
        restoreBackup = {
            restoreBackup.launch(arrayOf("application/json"))
        }
    )
}

@Composable
fun DataTransferCard(
    modifier: Modifier = Modifier,
    createBackup: () -> Unit = {},
    restoreBackup: () -> Unit = {}
) {
    Column {
        Text(
            stringResource(R.string.data_backup),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = MaterialTheme.sizing.extraSmall)
        )
        Column(
            modifier = modifier.clip(MaterialTheme.shapes.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.sizing.tiny)
        ) {
            SettingsActionCard(
                modifier = Modifier.fillMaxWidth(),
                icon = R.drawable.outlined_backup,
                title = stringResource(R.string.export_data),
                description = stringResource(R.string.export_data_description),
                onClick = createBackup
            )
            SettingsActionCard(
                modifier = Modifier.fillMaxWidth(),
                icon = R.drawable.filled_settings_backup_restore,
                title = stringResource(R.string.restore_data),
                description = stringResource(R.string.restore_data_description),
                onClick = restoreBackup
            )
        }
    }
}