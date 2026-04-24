/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.focus

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.screens.edit.fields.ShowConfirmDialog

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PomodoroControls(
    modifier: Modifier = Modifier,
    onEnd: () -> Unit,
    isPaused: Boolean,
    onPauseOrResume: () -> Unit,
    onSkip: () -> Unit,
    isWorkTime: Boolean,
    showPauseInWorkTime: Boolean,
    showSkipInWorkTime: Boolean,
    showPauseInRestTime: Boolean,
    showSkipInRestTime: Boolean,
) {
    // skip dialog
    var isSkipChecked by remember { mutableStateOf(false) }
    if (isSkipChecked) {
        ShowConfirmDialog(
            icon = R.drawable.filled_fast_forward,
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
            icon = R.drawable.filled_close,
            title = stringResource(R.string.end),
            description = stringResource(R.string.do_you_want_to_end_this_session),
            onConfirm = {
                onEnd()
                isStopChecked = false
            },
            onDismiss = { isStopChecked = false }
        )
    }

    val showPause = (isWorkTime && showPauseInWorkTime) || (!isWorkTime && showPauseInRestTime)
    val showSkip = (isWorkTime && showSkipInWorkTime) || (!isWorkTime && showSkipInRestTime)

    HorizontalFloatingToolbar(
        expanded = true,
        modifier = modifier
    ) {

        // 1. pause/resume button (Leading)
        if (showPause) {
            ToggleButton(
                checked = isPaused,
                onCheckedChange = { onPauseOrResume() },
                colors = ToggleButtonDefaults.toggleButtonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    checkedContainerColor = MaterialTheme.colorScheme.tertiary,
                    checkedContentColor = MaterialTheme.colorScheme.onTertiary,
                ),
                shapes = ButtonGroupDefaults.connectedLeadingButtonShapes()
            ) {
                AnimatedContent(isPaused, label = "PauseResumeIcon") {
                    Icon(
                        painterResource(if (it) R.drawable.filled_play_arrow else R.drawable.filled_pause),
                        contentDescription = if (it) stringResource(R.string.resume) else stringResource(R.string.pause),
                        modifier = Modifier.size(dimensionResource(R.dimen.image_size))
                    )
                }
            }
        }
        Spacer(Modifier.width(ButtonGroupDefaults.ConnectedSpaceBetween))

        // 2. end button (Middle / Dynamic)
        ToggleButton(
            checked = isStopChecked,
            onCheckedChange = { isStopChecked = !isStopChecked },
            shapes = when {
                showPause && showSkip -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                showPause && !showSkip -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                !showPause && showSkip -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                else -> ButtonGroupDefaults.connectedMiddleButtonShapes(MaterialTheme.shapes.extraLargeIncreased)
            },
            colors = ToggleButtonDefaults.toggleButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                checkedContainerColor = MaterialTheme.colorScheme.error,
                checkedContentColor = MaterialTheme.colorScheme.onError,
            )
        ) {
            Icon(
                painterResource(R.drawable.filled_close),
                contentDescription = stringResource(R.string.end),
                modifier = Modifier.size(dimensionResource(R.dimen.image_size))
            )
        }
        Spacer(Modifier.width(ButtonGroupDefaults.ConnectedSpaceBetween))


        // 3. skip button (Trailing)
        if (showSkip) {
            ToggleButton(
                checked = isSkipChecked,
                onCheckedChange = { isSkipChecked = !isSkipChecked },
                colors = ToggleButtonDefaults.toggleButtonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    checkedContainerColor = MaterialTheme.colorScheme.tertiary,
                    checkedContentColor = MaterialTheme.colorScheme.onTertiary,
                ),
                shapes = ButtonGroupDefaults.connectedTrailingButtonShapes()
            ) {
                Icon(
                    painterResource(R.drawable.filled_fast_forward),
                    contentDescription = stringResource(R.string.skip),
                    modifier = Modifier.size(dimensionResource(R.dimen.image_size))
                )
            }
        }
    }
}