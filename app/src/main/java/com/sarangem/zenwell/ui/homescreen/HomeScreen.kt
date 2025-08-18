package com.sarangem.zenwell.ui.homescreen

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.database.tables.Schedules
import com.sarangem.zenwell.service.AppBlockerService
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import com.sarangem.zenwell.utils.areNotificationsEnabled
import com.sarangem.zenwell.utils.checkAccessibilityServicePermission

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    schedulesList: List<Schedules>,
    scheduleClicked: Int = 0,
    startPermissionActivity: (Intent) -> Unit = {},
    requestNotification: () -> Unit = {},
    accessibilityPermission: () -> Boolean = { checkAccessibilityServicePermission() },
    notificationPermission: (Context) -> Boolean = { areNotificationsEnabled(it) },
    addNewSchedule: suspend () -> Schedules = suspend { Schedules() },
    openEditScreen: (Schedules) -> Unit = {},
    openFocusScreen: (Schedules) -> Unit = {},
) {
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
            openFocusScreen = openFocusScreen,
            startPermissionActivity = startPermissionActivity,
            requestNotification = requestNotification,
            accessibilityPermission = accessibilityPermission,
            notificationPermission = notificationPermission
        )

    }
}

@Composable
fun HomeScreenBody(
    modifier: Modifier = Modifier,
    schedulesList: List<Schedules>,
    scheduleClicked: Int = 0,
    openEditScreen: (Schedules) -> Unit = {},
    openFocusScreen: (Schedules) -> Unit = {},
    startPermissionActivity: (Intent) -> Unit = {},
    requestNotification: () -> Unit = {},
    accessibilityPermission: () -> Boolean,
    notificationPermission: (Context) -> Boolean,
) {
    val context = LocalContext.current
    var filter by rememberSaveable { mutableStateOf(SchedulesFilter.All) }
    var hasAccessibilityPermission by remember { mutableStateOf(accessibilityPermission()) }
    var hasNotificationPermission by remember { mutableStateOf(notificationPermission(context)) }

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
                requestNotification = {
                    requestNotification()
                    hasNotificationPermission = notificationPermission(context)
                },
                modifier = Modifier.fillMaxWidth(),
                hasAccessibilityServicePermission = hasAccessibilityPermission,
                hasNotificationsPermission = hasNotificationPermission
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

        item {
            if (!schedulesList.isEmpty()) {
                SchedulesFilterChips(
                    modifier = Modifier.fillMaxWidth(),
                    filter = filter,
                    updateFilter = {
                        filter = it
                    }
                )
            }
        }

        items(schedulesList) { schedule ->
            val showSchedule = when (filter) {
                SchedulesFilter.All -> true
                SchedulesFilter.Regular -> !schedule.isPomodoro
                SchedulesFilter.Pomodoro -> schedule.isPomodoro
            }

            if (showSchedule) {
                SchedulesCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.padding_small)),
                    schedule = schedule,
                    isClicked = schedule.id == scheduleClicked,
                    openEditScreen = openEditScreen,
                    openFocusScreen = openFocusScreen,
                    pomodoroManager = AppBlockerService.instance?.PomodoroManager(schedule.id),
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
                startTimeInMinutes = 9 * 60 + 14,
                endTimeInMinutes = 10 * 60 + 17
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
                startTimeInMinutes = 7 * 60 + 34,
                endTimeInMinutes = 11 * 60 + 12,
                isEnabled = false
            )
        ),
        scheduleClicked = 1,
        accessibilityPermission = { false },
        notificationPermission = { false }
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
fun HomeScreenEmptyPreview() {
    ZenwellTheme {
        HomeScreen(
            schedulesList = listOf(),
            accessibilityPermission = { false },
            notificationPermission = { false }
        )
    }
}
