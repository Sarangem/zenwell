package com.sarangem.zenwell.ui.editscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.sarangem.zenwell.R

@Composable
fun ChoosePomodoroActionsToShow(
    modifier: Modifier = Modifier,
    showPauseInWorkTime: Boolean,
    updateShowPauseInWorkTime: (Boolean) -> Unit,
    showSkipInWorkTime: Boolean,
    updateShowSkipInWorkTime: (Boolean) -> Unit,
    showPauseInRestTime: Boolean,
    updateShowPauseInRestTime: (Boolean) -> Unit,
    showSkipInRestTime: Boolean,
    updateShowSkipInRestTime: (Boolean) -> Unit,
){
    val pauseLabel: @Composable () -> Unit = {
        Text(
            text = stringResource(R.string.pause_resume),
            style = MaterialTheme.typography.labelLarge
        )
    }
    val skipLabel: @Composable () -> Unit = {
        Text(
            text = stringResource(R.string.skip),
            style = MaterialTheme.typography.labelLarge
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_small)),
    ){

        Text(
            text = stringResource(R.string.actions_to_show_in_work_time),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
        )
        Row(
            modifier = Modifier.padding(
                start = dimensionResource(R.dimen.padding_small),
                end = dimensionResource(R.dimen.padding_small),
                bottom =  dimensionResource(R.dimen.padding_small),
            ),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ){
            FilterChip(
                selected = showPauseInWorkTime,
                onClick = {
                    updateShowPauseInWorkTime(!showPauseInWorkTime)
                },
                label = pauseLabel,
            )
            FilterChip(
                selected = showSkipInWorkTime,
                onClick = {
                    updateShowSkipInWorkTime(!showSkipInWorkTime)
                },
                label = skipLabel,
            )
        }

        Text(
            text = stringResource(R.string.actions_to_show_in_rest_time),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
        )
        Row(
            modifier = Modifier.padding(
                start = dimensionResource(R.dimen.padding_small),
                end = dimensionResource(R.dimen.padding_small),
                bottom =  dimensionResource(R.dimen.padding_small),
            ),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ){
            FilterChip(
                selected = showPauseInRestTime,
                onClick = {
                    updateShowPauseInRestTime(!showPauseInRestTime)
                },
                label = pauseLabel,
            )
            FilterChip(
                selected = showSkipInRestTime,
                onClick = {
                    updateShowSkipInRestTime(!showSkipInRestTime)
                },
                label = skipLabel,
            )
        }
    }
}