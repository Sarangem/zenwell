package com.sarangem.zenwell.ui.screens.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.sarangem.zenwell.R

enum class SchedulesFilter(@StringRes val title: Int) {
    All(R.string.all),
    Regular(R.string.regular),
    Pomodoro(R.string.pomodoro)
}

@Composable
fun SchedulesFilterChips(
    modifier: Modifier,
    filter: SchedulesFilter,
    updateFilter: (SchedulesFilter) -> Unit = {}
){
    Row(
        modifier = modifier.padding(dimensionResource(R.dimen.padding_small)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
    ){
        SchedulesFilter.entries.forEach { entry ->
            FilterChip(
                selected = entry == filter,
                onClick = {
                    updateFilter(entry)
                },
                label = { Text(stringResource(entry.title)) },
            )
        }
    }
}