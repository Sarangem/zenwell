/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.Green500
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import com.sarangem.zenwell.ui.theme.sizing
import kotlin.math.absoluteValue

@Composable
fun MotivationalCard(
    dailyUsage: List<DailyAppUsageData>,
    weeklyAverageInMinutes: Int,
    modifier: Modifier = Modifier
){
    val savedTimeInMinutes = remember(dailyUsage, weeklyAverageInMinutes) {
        val yesterday = dailyUsage[dailyUsage.size - 2].totalTimeInMinutes
        weeklyAverageInMinutes - yesterday
    }
    MotivationalCard(modifier, savedTimeInMinutes)
}

@Composable
fun MotivationalCard(
    modifier: Modifier = Modifier,
    savedTimeInMinutes: Int
) {
    val isPositive = savedTimeInMinutes >= 0
    val color = if (isPositive) Green500 else darkColorScheme().errorContainer
    if (currentWindowAdaptiveInfoV2().windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)){
        Column(
            modifier = modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .clip(MaterialTheme.shapes.medium)
                .background(color.copy(alpha = 0.2f))
                .drawBehind {
                    val strokeWidthPx = 6.dp.toPx()
                    val yCoordinate = strokeWidthPx / 2
                    drawLine(
                        color = if (isPositive) Color(0xFF2E7D32) else color,
                        start = Offset(x = 0f, y = yCoordinate),
                        end = Offset(x = size.width, y = yCoordinate),
                        strokeWidth = strokeWidthPx
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MotivationalCardText(Modifier.wrapContentWidth().fillMaxWidth(), Modifier.weight(1f), savedTimeInMinutes, color, isPositive)
        }
    } else{
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .clip(MaterialTheme.shapes.medium)
                .background(color.copy(alpha = 0.2f))
                .drawBehind {
                    val strokeWidthPx = 6.dp.toPx()
                    val xCoordinate =
                        if (layoutDirection == LayoutDirection.Rtl) size.width - (strokeWidthPx / 2) else strokeWidthPx / 2
                    drawLine(
                        color = if (isPositive) Color(0xFF2E7D32) else color,
                        start = Offset(x = xCoordinate, y = 0f),
                        end = Offset(x = xCoordinate, y = size.height),
                        strokeWidth = strokeWidthPx
                    )
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            MotivationalCardText(Modifier.wrapContentHeight().fillMaxHeight(), Modifier.weight(1f), savedTimeInMinutes, color, isPositive)
        }
    }
}

@Composable
fun MotivationalCardText(
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    savedTimeInMinutes: Int,
    color: Color,
    isPositive: Boolean
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(color.copy(alpha = 0.3f))
            .padding(MaterialTheme.sizing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = ( if(isPositive) "\uD83D\uDC7C" else "\uD83E\uDDDF" ) + "\u200B",
            fontSize = 25.sp
        )
        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        fontSize = if (isPositive) 60.sp else 40.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                ) {
                    append((savedTimeInMinutes.absoluteValue / 60).toString())
                }
                append(stringResource(R.string.hour_abbreviation))
                append("\u200B")
                withStyle(
                    SpanStyle(
                        fontSize = if (isPositive) 30.sp else 25.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                ) {
                    append("%02d".format(savedTimeInMinutes.absoluteValue % 60))
                }
                append(stringResource(R.string.minute_abbreviation))
            },
            fontFamily = if (isPositive) FontFamily.Monospace else FontFamily.Serif,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
    Text(
        text = stringResource(if (isPositive) R.string.positive_motivation else R.string.negative_motivation),
        style = MaterialTheme.typography.titleMediumEmphasized,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = textModifier.padding(MaterialTheme.sizing.small)
    )
}

@Preview(showBackground = true)
@Composable
fun MotivationalCardPreview() {
    ZenwellTheme {
        Column {
            MotivationalCard(savedTimeInMinutes = 150)
            MotivationalCard(savedTimeInMinutes = -90)
        }
    }
}