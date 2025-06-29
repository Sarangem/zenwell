package com.sarangem.zenwell.ui.homescreen

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sarangem.zenwell.R
import com.sarangem.zenwell.checkAccessibilityServicePermission
import com.sarangem.zenwell.data.tables.Schedules
import com.sarangem.zenwell.getAmPm
import com.sarangem.zenwell.minutesToString
import com.sarangem.zenwell.ui.theme.ZenwellTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    schedulesList: List<Schedules>,
    scheduleClicked: Int = 0,
    startPermissionActivity: (Intent) -> Unit = {},
    accessibilityPermission: () -> Boolean = { checkAccessibilityServicePermission() },
    addNewSchedule: suspend () -> Schedules = suspend { Schedules() },
    openEditScreen: (Schedules) -> Unit = {},
){
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
            )
        },
        floatingActionButton = {
            NewScheduleFAB(
                addNewSchedule = addNewSchedule,
                openEditScreen = openEditScreen,
            )
        }
    ) { innerPadding ->

        HomeScreenBody(
            modifier = Modifier.padding(innerPadding),
            schedulesList = schedulesList,
            scheduleClicked = scheduleClicked,
            openEditScreen = openEditScreen,
            startPermissionActivity = startPermissionActivity,
            accessibilityPermission = accessibilityPermission
        )

    }
}

@Composable
fun HomeScreenBody(
    modifier: Modifier = Modifier,
    schedulesList: List<Schedules>,
    scheduleClicked: Int = 0,
    openEditScreen: (Schedules) -> Unit = {},
    startPermissionActivity: (Intent) -> Unit = {},
    accessibilityPermission: () -> Boolean = { checkAccessibilityServicePermission() }
) {
    var hasAccessibilityPermission by remember { mutableStateOf(accessibilityPermission()) }

    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        item {
            AskPermissions(
                startPermissionActivity = { intent ->
                    startPermissionActivity(intent)
                    hasAccessibilityPermission = accessibilityPermission()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_small)),
                hasAccessibilityServicePermission = hasAccessibilityPermission,
            )
        }

        item {
            if (schedulesList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillParentMaxHeight()
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_schedules),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                    )
                }
            }
        }

        items(schedulesList) { schedule ->
            SchedulesCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_small)),
                schedule = schedule,
                isClicked = schedule.id == scheduleClicked,
                openEditScreen = openEditScreen
            )
        }
    }
}


@Composable
fun SchedulesCard(
    modifier: Modifier = Modifier,
    schedule: Schedules,
    isClicked: Boolean = false,
    openEditScreen: (Schedules) -> Unit = {}
){
    val tint = if (schedule.isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
    val cardColor = if (isClicked) {
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    } else {
        CardDefaults.cardColors()
    }

    Card(
        colors = cardColor,
        modifier = modifier
    ) {
        Row {
            Column(modifier = Modifier.weight(5f)) {
                Text(
                    text = schedule.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = tint,
                    fontWeight = FontWeight.Bold,
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
                    color = tint,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { openEditScreen(schedule) }) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = stringResource(R.string.edit_this_schedule),
                    tint = tint,
                    modifier = Modifier.size(28.dp)
                )
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
                id = 1,
                title = "Schedule 1",
                startTimeInMinutes = 13 * 60 + 14,
                endTimeInMinutes = 19 * 60 + 17
            ),
            Schedules(
                id = 2,
                title = "A reallyyyyyyyyyyyyyyyyyyyyyyyyy long name",
                startTimeInMinutes = 0,
                endTimeInMinutes = 1 * 60 + 1
            ),
            Schedules(
                id = 3,
                title = "A very long schedules title with spaces in between",
                startTimeInMinutes = 17 * 60 + 34,
                endTimeInMinutes = 12 * 60 + 12,
                isEnabled = false
            )
        ),
        scheduleClicked = 1,
        accessibilityPermission = { return@HomeScreen false}
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreviewLightMode() {
    ZenwellTheme(darkTheme = false) {
        HomeScreenPreview()
    }
}

@Preview
@Composable
fun HomeScreenPreviewDarkMode() {
    ZenwellTheme(darkTheme = true) {
        HomeScreenPreview()
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenEmptyPreview(){
    ZenwellTheme {
        HomeScreen(
            schedulesList = listOf(),
            accessibilityPermission = {return@HomeScreen false}
        )
    }
}
