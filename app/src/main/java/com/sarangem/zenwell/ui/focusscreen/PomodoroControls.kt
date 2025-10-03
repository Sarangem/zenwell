package com.sarangem.zenwell.ui.focusscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonColors
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.common.ShowConfirmDialog

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PomodoroControls(
    modifier: Modifier = Modifier,
    onStop: () -> Unit,
    isPaused: Boolean,
    onPauseOrResume: () -> Unit,
    onSkip: () -> Unit,
    isWorkTime: Boolean,
    showPauseInWorkTime: Boolean,
    showSkipInWorkTime: Boolean,
    showPauseInRestTime: Boolean,
    showSkipInRestTime: Boolean,
) {
    // button shapes
    val leadingButtonShape = RoundedCornerShape(
        topStart = CornerSize(100),
        bottomStart = CornerSize(8.0.dp),
        topEnd = CornerSize(8.0.dp),
        bottomEnd = CornerSize(8.0.dp),
    )
    val trailingButtonShape = RoundedCornerShape(
        topStart = CornerSize(8.0.dp),
        bottomStart = CornerSize(8.0.dp),
        topEnd = CornerSize(100),
        bottomEnd = CornerSize(8.0.dp),
    )
    val middleShape = RoundedCornerShape(
        topStart = CornerSize(100),
        bottomStart = CornerSize(8.0.dp),
        topEnd = CornerSize(100),
        bottomEnd = CornerSize(8.0.dp),
    )
    val showMiddleShape = if (isWorkTime) {
        !showSkipInWorkTime || !showPauseInWorkTime
    } else {
        !showSkipInRestTime || !showPauseInRestTime
    }

    // skip dialog
    var isSkipChecked by remember { mutableStateOf(false) }
    if (isSkipChecked) {
        ShowConfirmDialog(
            icon = Icons.Filled.FastForward,
            title = stringResource(R.string.skip_to_next_session),
            description = stringResource(R.string.skip_to_next_session_description),
            onConfirm = {
                onSkip()
                isSkipChecked = false
            },
            onDismiss = { isSkipChecked = false }
        )
    }

    // stop dialog
    var isStopChecked by remember { mutableStateOf(false) }
    if (isStopChecked) {
        ShowConfirmDialog(
            icon = Icons.Filled.Stop,
            title = stringResource(R.string.end),
            description = stringResource(R.string.do_you_want_to_end_this_session),
            onConfirm = onStop,
            onDismiss = { isStopChecked = false }
        )
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
    ) {
        Spacer(Modifier.weight(0.1f))
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility (
                visible = (isWorkTime && showPauseInWorkTime) || (!isWorkTime && showPauseInRestTime) ,
                modifier = Modifier.weight(1f),
            ) {
                PomodoroControlButton(
                    value = isPaused,
                    onValueChange = onPauseOrResume,
                    icon = if (isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                    text = if (isPaused) stringResource(R.string.resume) else stringResource(R.string.pause),
                    shape = if (showMiddleShape) middleShape else leadingButtonShape
                )
            }
            AnimatedVisibility (
                visible = (isWorkTime && showSkipInWorkTime) || (!isWorkTime && showSkipInRestTime),
                modifier = Modifier.weight(1f)
            ) {
                PomodoroControlButton(
                    value = isSkipChecked,
                    onValueChange = { isSkipChecked = !isSkipChecked },
                    icon = Icons.Filled.FastForward,
                    text = stringResource(R.string.skip),
                    shape = if(showMiddleShape) middleShape else trailingButtonShape
                )
            }
        }
        PomodoroControlButton(
            modifier = Modifier.weight(1f),
            value = isStopChecked,
            onValueChange = { isStopChecked = !isStopChecked },
            icon = Icons.Filled.Stop,
            text = stringResource(R.string.end),
            colors = ToggleButtonDefaults.toggleButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                checkedContainerColor = MaterialTheme.colorScheme.error,
                checkedContentColor = MaterialTheme.colorScheme.onError,
            )
        )
        Spacer(Modifier.weight(0.1f))
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PomodoroControlButton(
    modifier: Modifier = Modifier,
    value: Boolean,
    onValueChange: () -> Unit,
    icon: ImageVector? = null,
    text: String,
    shape: CornerBasedShape = ShapeDefaults.Small,
    colors: ToggleButtonColors = ToggleButtonDefaults.toggleButtonColors(
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        checkedContainerColor = MaterialTheme.colorScheme.tertiary,
        checkedContentColor = MaterialTheme.colorScheme.onTertiary,
    ),
){
    ToggleButton(
        checked = value,
        onCheckedChange = { onValueChange() },
        shapes = ButtonGroupDefaults.connectedLeadingButtonShapes(shape),
        colors = colors,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if(icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                )
            }
            Spacer(Modifier.size(dimensionResource(R.dimen.padding_medium)))
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}