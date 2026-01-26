package com.sarangem.zenwell.ui.screens.edit.fields

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sarangem.zenwell.utils.convertToTimePickerState
import com.sarangem.zenwell.utils.getAmPm
import com.sarangem.zenwell.utils.is24Hour
import com.sarangem.zenwell.utils.minutesToString
import com.sarangem.zenwell.utils.toMinutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClockButton(
    modifier: Modifier = Modifier,
    time: Int,
    timePickerTitle: String,
    updateUiState: (Int) -> Unit
) {
    val context = LocalContext.current
    var showPopup by remember { mutableStateOf(false) }
    Button(
        modifier = modifier,
        onClick = { showPopup = true },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        shape = MaterialTheme.shapes.extraLarge,
        border = BorderStroke(width = 0.5.dp, color = MaterialTheme.colorScheme.secondary)
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            Row {
                Spacer(Modifier.weight(0.5f))
                Text(
                    text = minutesToString(time, context),
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    autoSize = TextAutoSize.StepBased(maxFontSize = MaterialTheme.typography.headlineMedium.fontSize),
                )
                Spacer(Modifier.weight(0.5f))
            }
            if (!is24Hour(context)) {
                Row {
                    Spacer(Modifier.weight(0.5f))
                    Text(
                        text = getAmPm(time),
                        style = MaterialTheme.typography.labelMedium,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        autoSize = TextAutoSize.StepBased(maxFontSize = MaterialTheme.typography.labelMedium.fontSize),
                    )
                    Spacer(Modifier.weight(0.5f))
                }
            }
        }
    }
    if (showPopup) {
        AdvancedTimePickerDialog(
            title = timePickerTitle,
            onDismiss = { showPopup = false },
            onConfirm = {
                updateUiState(it.toMinutes())
                showPopup = false
            },
            timePickerState = convertToTimePickerState(time, context)
        )
    }
}