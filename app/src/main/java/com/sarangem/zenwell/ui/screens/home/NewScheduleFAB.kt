/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.home

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sarangem.zenwell.R
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.ui.sequenceshowcase.SequenceShowcaseScope
import com.sarangem.zenwell.ui.theme.sizing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun NewScheduleFAB(
    modifier: Modifier = Modifier,
    firstEntry: Int? = null,
    nextShowcase: () -> Unit = {},
    addNewSchedule: suspend (Context, Boolean) -> Schedules = { _,_ -> Schedules() },
    openEditScreen: (Schedules) -> Unit = {},
) {
    AnimatedContent (
        modifier = modifier,
        targetState = firstEntry
    ) {
        if(it == 1) FirstEntryScheduleFAB(
            addNewSchedule = addNewSchedule,
            openEditScreen = openEditScreen,
            nextShowcase = nextShowcase
        ) else RegularEntryScheduleFAB(
            addNewSchedule = addNewSchedule,
            openEditScreen = openEditScreen
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RegularEntryScheduleFAB(
    modifier: Modifier = Modifier,
    addNewSchedule: suspend (Context, Boolean) -> Schedules = { _,_ -> Schedules() },
    openEditScreen: (Schedules) -> Unit = {},
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var fabMenuExpanded by remember { mutableStateOf(false) }
    val expanded = stringResource(R.string.expanded)
    val collapsed = stringResource(R.string.collapsed)
    val newSchedule = stringResource(R.string.new_schedule)

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
                            stateDescription = if (fabMenuExpanded) expanded else collapsed
                            contentDescription = newSchedule
                        }
                        .animateFloatingActionButton(
                            visible = true,
                            alignment = Alignment.BottomEnd,
                        ),
                checked = fabMenuExpanded,
                onCheckedChange = { fabMenuExpanded = it },
            ) {
                val image by remember {
                    derivedStateOf {
                        if (checkedProgress > 0.5f) R.drawable.filled_close else R.drawable.filled_add
                    }
                }
                Icon(
                    painter = painterResource(image),
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
                    val newSchedule = addNewSchedule(context, false)
                    withContext(Dispatchers.Main) {
                        openEditScreen(newSchedule)
                    }
                }
            },
            icon = {
                Icon(
                    painterResource(R.drawable.filled_calendar_today),
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
                    val newSchedule = addNewSchedule(context, true)
                    withContext(Dispatchers.Main) {
                        openEditScreen(newSchedule.copy(isPomodoro = true))
                    }
                }
            },
            icon = {
                Icon(
                    painterResource(R.drawable.filled_hourglass_empty),
                    contentDescription = null
                )
            },
            text = {
                Text(text = stringResource(R.string.pomodoro_schedule))
            },
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FirstEntryScheduleFAB(
    modifier: Modifier = Modifier,
    addNewSchedule: suspend (Context, Boolean) -> Schedules = { _,_ -> Schedules() },
    openEditScreen: (Schedules) -> Unit = {},
    nextShowcase: () -> Unit = {}
){
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    LargeFloatingActionButton(
        onClick = {
            nextShowcase()
            scope.launch(Dispatchers.IO) {
                val newSchedule = addNewSchedule(context, false)
                withContext(Dispatchers.Main) {
                    openEditScreen(newSchedule)
                }
            }
        },
        modifier = modifier
    ) { Icon(painterResource(R.drawable.filled_add), contentDescription = null) }
}

val showcase1Modifier: @Composable SequenceShowcaseScope.(skipGuide: () -> Unit) -> Modifier = { skip ->
    Modifier.sequenceShowcaseTarget(
        index = 1,
        shape = RoundedCornerShape(32.dp),
        shapeMargin = 0.dp,
        backgroundAlpha = 0.9f,
        fixedContent = { SkipGuideButton(skip) }
    ) {
        Text(
            text = stringResource(R.string.showcase_1),
            style = MaterialTheme.typography.headlineMedium,
            color = darkColorScheme().onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun SequenceShowcaseScope.SkipGuideButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopEnd
    ) {
        Button(
            modifier = Modifier
                .statusBarsPadding()
                .padding(MaterialTheme.sizing.small),
            onClick = {
                showcaseState.dismiss()
                onClick()
            },
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondaryContainer),
            contentPadding = PaddingValues(horizontal = MaterialTheme.sizing.extraSmall)
        ) {
            Text(
                stringResource(R.string.skip_guide),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}