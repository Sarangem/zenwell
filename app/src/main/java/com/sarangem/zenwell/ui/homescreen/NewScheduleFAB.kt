package com.sarangem.zenwell.ui.homescreen

import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.tables.Schedules
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NewScheduleFAB(
    modifier: Modifier = Modifier,
    addNewSchedule: suspend () -> Schedules = suspend { Schedules() },
    openEditScreen: (Schedules) -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var fabMenuExpanded by remember { mutableStateOf(false) }

    BackHandler(fabMenuExpanded) { fabMenuExpanded = false }

    FloatingActionButtonMenu(
        modifier = modifier,
        expanded = fabMenuExpanded,
        button = {
            ToggleFloatingActionButton(
                modifier =
                    Modifier
                        .semantics {
                            traversalIndex = -1f
                            stateDescription =
                                context.getString(if (fabMenuExpanded) R.string.expanded else R.string.collapsed)
                            contentDescription = context.getString(R.string.new_schedule)
                        }
                        .animateFloatingActionButton(
                            visible = true,
                            alignment = Alignment.BottomEnd,
                        ),
                checked = fabMenuExpanded,
                onCheckedChange = { fabMenuExpanded = it },
            ) {
                val imageVector by remember {
                    derivedStateOf {
                        if (checkedProgress > 0.5f) Icons.Filled.Close else Icons.Filled.Add
                    }
                }
                Icon(
                    painter = rememberVectorPainter(imageVector),
                    contentDescription = null,
                    modifier = Modifier.animateIcon({ checkedProgress }),
                )
            }
        }
    ) {
        FloatingActionButtonMenuItem(
            onClick = {
                fabMenuExpanded = false
                coroutineScope.launch(Dispatchers.IO) {
                    val newSchedule = addNewSchedule()
                    withContext(Dispatchers.Main) {
                        openEditScreen(newSchedule)
                    }
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null
                )
            },
            text = {
                Text(text = stringResource(R.string.regular_schedule))
            },
        )

        FloatingActionButtonMenuItem(
            onClick = {
                fabMenuExpanded = false
                coroutineScope.launch(Dispatchers.IO) {
                    val newSchedule = addNewSchedule()
                    withContext(Dispatchers.Main) {
                        openEditScreen(newSchedule.copy(isPomodoro = true))
                    }
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.HourglassEmpty,
                    contentDescription = null
                )
            },
            text = {
                Text(text = stringResource(R.string.pomodoro_schedule))
            },
        )
    }
}
