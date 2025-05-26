package com.sarangem.zenwell.ui.homescreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.tables.Schedules
import com.sarangem.zenwell.getAmPm
import com.sarangem.zenwell.minutesToString
import com.sarangem.zenwell.ui.ZenwellAppViewModelProvider
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeScreenViewModel: HomeScreenViewModel = viewModel(factory = ZenwellAppViewModelProvider.Factory),
    openEditScreen: (Int) -> Unit = {},
    openSettingsScreen: (Int) -> Unit = {},
) {
    val schedulesList by homeScreenViewModel.getAllSchedules().collectAsState(emptyList())

    HomeScreenBody(
        modifier = modifier,
        openEditScreen = openEditScreen,
        openSettingsScreen = openSettingsScreen,
        schedulesList = schedulesList,
        addNewSchedule = {
            homeScreenViewModel.addNewSchedule(it)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreenBody(
    modifier: Modifier = Modifier,
    openEditScreen: (Int) -> Unit = {},
    openSettingsScreen: (Int) -> Unit = {},
    schedulesList: List<Schedules>,
    addNewSchedule: suspend (List<Schedules>) -> Int = { _ -> 0 },
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        val scheduleId = addNewSchedule(schedulesList)
                        withContext(Dispatchers.Main) {
                            openEditScreen(scheduleId)
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.new_schedule)
                    )
                },
                modifier = Modifier
                    .padding(
                        end = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateEndPadding(LocalLayoutDirection.current)
                    )
            )
        },
    ) { innerPadding ->
        SchedulesList(
            schedulesList = schedulesList,
            openEditScreen = openEditScreen,
            modifier = Modifier.padding(innerPadding)
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
    HomeScreenBody(
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