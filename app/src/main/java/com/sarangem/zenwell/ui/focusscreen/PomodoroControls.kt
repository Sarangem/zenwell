package com.sarangem.zenwell.ui.focusscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.commonui.ShowConfirmDialog

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PomodoroControls(
    modifier: Modifier = Modifier,
    onStop: () -> Unit,
    isPaused: Boolean,
    onPauseOrResume: () -> Unit,
    onSkip: () -> Unit,
) {
    var isStopChecked by remember { mutableStateOf(false) }
    if (isStopChecked) {
        ShowConfirmDialog(
            icon = Icons.Filled.Stop,
            headingText = stringResource(R.string.end),
            bodyText = stringResource(R.string.do_you_want_to_end_this_session),
            onConfirm = onStop,
            onDismiss = { isStopChecked = false }
        )
    }

    var isSkipChecked by remember { mutableStateOf(false) }
    if (isSkipChecked) {
        ShowConfirmDialog(
            icon = Icons.Filled.FastForward,
            headingText = stringResource(R.string.skip_to_next_session),
            bodyText = stringResource(R.string.skip_to_next_session_description),
            onConfirm = onSkip,
            onDismiss = { isSkipChecked = false }
        )
    }

    val buttonColors = ToggleButtonDefaults.toggleButtonColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        checkedContainerColor = MaterialTheme.colorScheme.primary,
        checkedContentColor = MaterialTheme.colorScheme.onPrimary,
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ToggleButton(
            checked = isStopChecked,
            onCheckedChange = { isStopChecked = it },
            colors = buttonColors,
            shapes = ButtonGroupDefaults.connectedLeadingButtonShapes(),
            modifier = Modifier
                .fillMaxHeight(0.6f)
                .weight(1f),
        ) {
            Icon(
                imageVector = Icons.Filled.Stop,
                contentDescription = stringResource(R.string.end),
                modifier = Modifier.fillMaxSize(0.5f)
            )
        }

        ToggleButton(
            checked = isPaused,
            onCheckedChange = { onPauseOrResume() },
            shapes = ButtonGroupDefaults.connectedMiddleButtonShapes(),
            colors = buttonColors,
            modifier = Modifier
                .fillMaxHeight(0.6f)
                .weight(1f),
        ) {
            Icon(
                imageVector = if (isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                contentDescription = if(isPaused) stringResource(R.string.resume) else stringResource(R.string.pause),
                modifier = Modifier.fillMaxSize(0.5f)
            )
        }

        ToggleButton(
            checked = isSkipChecked,
            onCheckedChange = { isSkipChecked = it },
            shapes = ButtonGroupDefaults.connectedTrailingButtonShapes(),
            modifier = Modifier
                .fillMaxHeight(0.6f)
                .weight(1f),
            colors = buttonColors
        ) {
            Icon(
                imageVector = Icons.Filled.FastForward,
                contentDescription = stringResource(R.string.skip_to_next_session),
                modifier = Modifier.fillMaxSize(0.5f)
            )
        }
    }
}
