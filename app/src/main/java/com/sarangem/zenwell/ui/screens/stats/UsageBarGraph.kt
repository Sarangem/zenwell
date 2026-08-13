/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.stats

import android.graphics.drawable.Drawable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import coil3.compose.AsyncImage
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.sizing
import kotlinx.coroutines.launch
import kotlin.math.ceil

@Composable
fun UsageBarGraph(
    modifier: Modifier = Modifier,
    filteredDailyUsage: List<DailyAppUsageData>,
    statsFilter: StatsFilter = StatsFilter.AllApps
) {
    BoxWithConstraints(modifier) {
        UsageBarGraph(
            filteredDailyUsage,
            maxOf(maxWidth, 350.dp),
            statsFilter is StatsFilter.Custom
        )
    }
}

@Composable
fun UsageBarGraph(
    dailyUsageList: List<DailyAppUsageData>,
    maxWidth: Dp,
    isCustom: Boolean
){
    val maxHours = (dailyUsageList
        .maxOfOrNull { it.totalTimeInMinutes } ?: 0)
        .let { ceil(it / 60.0).toInt() }
        .coerceIn(4, 24)
    val labelSize = with(LocalDensity.current) { LocalTextStyle.current.fontSize.toDp() * 2 }
    val block = (maxWidth - labelSize) / dailyUsageList.size
    var columnClicked by remember { mutableIntStateOf(dailyUsageList.size - 1) }

    Row(
        Modifier
            .width(maxWidth)
            .height(block * maxHours + labelSize),
    ) {
        Column(
            Modifier
                .width(labelSize)
                .fillMaxSize(),
        ) {
            (maxHours downTo 1).forEach{
                Box(
                    modifier = Modifier.height(block),
                    contentAlignment = Alignment.TopEnd
                ){
                    Text(it.toString() + stringResource(R.string.hour_abbreviation))
                }
            }
            Box(Modifier)
        }
        LazyRow(
            Modifier.fillMaxSize(),
            state = rememberLazyListState(initialFirstVisibleItemIndex = dailyUsageList.size - 1)
        ){
            itemsIndexed(dailyUsageList) { index, usageData ->
                Column(
                    Modifier
                        .animateContentSize()
                        .width(block)
                        .fillMaxHeight()
                        .background(
                            if(columnClicked == index){
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.75f)
                            } else Color.Unspecified
                        )
                        .clickable(onClick = { columnClicked = index}),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    AnimatedVisibility(columnClicked == index) {
                        Text(
                            stringResource(
                                R.string.hour_minute_abbr,
                                usageData.totalTimeInMinutes / 60,
                                usageData.totalTimeInMinutes % 60
                            )
                        )
                    }
                    LazyColumn(
                        Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                            .height(usageData.totalTimeInMinutes / 60f * block)
                            .padding(horizontal = MaterialTheme.sizing.tiny)
                            .clip(MaterialTheme.shapes.medium)
                            .background(if(isCustom) MaterialTheme.colorScheme.primary else Color.Gray),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        if(!isCustom){
                            items(usageData.data) {
                                Box(
                                    Modifier
                                        .height(it.timeInMinutes / 60f * block)
                                        .fillMaxWidth()
                                        .background(it.iconColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ToolboxIconButton(
                                        it.icon,
                                        it.appName ?: it.packageName,
                                        it.timeInMinutes
                                    )
                                }
                            }
                        }
                    }
                    Text(usageData.weekDayName ?: "")
                }
            }
        }
    }
}

@Composable
fun ToolboxIconButton(
    icon: Drawable?,
    name: String,
    timeInMinutes: Int,
    modifier: Modifier = Modifier
) {
    val tooltipState = rememberTooltipState()
    val scope = rememberCoroutineScope()
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Left),
        tooltip = {
            RichTooltip(
                title = { Text(name) },
                text = { Text(stringResource(R.string.hour_minute_abbr, timeInMinutes / 60, timeInMinutes % 60)) }
            )
        },
        state = tooltipState
    ) {
        AsyncImage(
            model = icon,
            contentDescription = name,
            modifier = modifier
                .size(MaterialTheme.sizing.image)
                .padding(MaterialTheme.sizing.small)
                .clickable {
                    scope.launch {
                        if (tooltipState.isVisible) tooltipState.dismiss() else tooltipState.show()
                    }
                }
        )
    }
}