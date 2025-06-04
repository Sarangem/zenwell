package com.sarangem.zenwell.ui.homescreen

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.tables.Schedules
import com.sarangem.zenwell.getAmPm
import com.sarangem.zenwell.minutesToString
import com.sarangem.zenwell.ui.theme.ZenwellTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    schedulesList: List<Schedules>,
    openEditScreen: (Int) -> Unit = {},
    startPermissionActivity: (Intent) -> Unit = {}
) {

    Column(modifier = modifier) {

        AskPermissions(
            startPermissionActivity = startPermissionActivity
        )

        Spacer(Modifier.padding(dimensionResource(R.dimen.padding_small)))

        SchedulesList(
            schedulesList = schedulesList,
            openEditScreen = openEditScreen,
        )

    }
}

@Composable
fun SchedulesList(
    schedulesList: List<Schedules>,
    modifier: Modifier = Modifier,
    openEditScreen: (Int) -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(schedulesList) { schedule ->
            Card(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = dimensionResource(R.dimen.card_elevation)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_small))
            ) {
                Row {
                    Column(modifier = Modifier.weight(5f)) {
                        Text(
                            text = schedule.title,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                        )
                        Text(
                            text = minutesToString(schedule.startTimeInMinutes) + " " + getAmPm(
                                schedule.startTimeInMinutes
                            )
                                    + stringResource(R.string.to)
                                    + minutesToString(schedule.endTimeInMinutes) + " " + getAmPm(
                                schedule.endTimeInMinutes
                            ),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { openEditScreen(schedule.id) }) {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = stringResource(R.string.edit_this_schedule)
                        )
                    }
                }
            }
        }
    }
}


// -- Preview -- //
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        schedulesList = listOf(
            Schedules(
                title = "Schedule 1",
                startTimeInMinutes = 13 * 60 + 14,
                endTimeInMinutes = 19 * 60 + 17
            ),
            Schedules(
                title = "A reallyyyyyyyyyyyyyyyyyyyyyyyyy long name",
                startTimeInMinutes = 0,
                endTimeInMinutes = 1 * 60 + 1
            ),
            Schedules(
                title = "A very long schedules title with spaces in between",
                startTimeInMinutes = 17 * 60 + 34,
                endTimeInMinutes = 12 * 60 + 12
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreviewLightMode() {
    ZenwellTheme(darkTheme = false) {
        HomeScreenPreview()
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreviewDarkMode() {
    ZenwellTheme(darkTheme = true) {
        HomeScreenPreview()
    }
}