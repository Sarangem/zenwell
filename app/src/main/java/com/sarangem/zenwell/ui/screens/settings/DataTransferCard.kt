package com.sarangem.zenwell.ui.screens.settings

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ZenwellApplication
import com.sarangem.zenwell.model.BackupData
import com.sarangem.zenwell.ui.screens.common.ShowConfirmDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
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
    val tag = "Settings/Backup"


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

    var showDialog by remember { mutableStateOf(false) }
    val isConfirmed = remember { MutableStateFlow(false) }
    if (showDialog) {
        ShowConfirmDialog(
            icon = Icons.Outlined.Restore,
            title = stringResource(R.string.restore_data),
            description = stringResource(R.string.restore_data_dialog_description),
            onConfirm = {
                isConfirmed.value = true
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
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
                    val restoredData = json.decodeFromString<BackupData>(jsonString)
                    showDialog = true
                    isConfirmed.first { it }
                    app.container.restoreAllData(restoredData)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, R.string.data_restore_completed, Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            } catch (e: Exception) {

                Log.e(tag, "Cannot restore backup from $uri")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, R.string.data_restore_failed, Toast.LENGTH_SHORT).show()
                }

            }

        }
    }

    DataTransferCardBody(
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
fun DataTransferCardBody(
    modifier: Modifier = Modifier,
    createBackup: () -> Unit = {},
    restoreBackup: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding_small))
            .clip(MaterialTheme.shapes.medium),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
    ) {
        DataTransferActionCard(
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Outlined.Backup,
            title = stringResource(R.string.export_data),
            description = stringResource(R.string.export_data_description),
            onClick = createBackup
        )
        DataTransferActionCard(
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Outlined.Restore,
            title = stringResource(R.string.restore_data),
            description = stringResource(R.string.restore_data_description),
            onClick = restoreBackup
        )
    }
}

@Composable
fun DataTransferActionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(dimensionResource(R.dimen.image_size))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.fillMaxSize(0.6f)
                )

            }
            Spacer(Modifier.size(dimensionResource(R.dimen.padding_large)))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}