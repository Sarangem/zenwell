package com.sarangem.zenwell.ui.focusscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.window.core.layout.WindowSizeClass
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.Orbitron

@Composable
fun PomodoroTimerDisplay(
    elapsedTime: Int,
    isWorkTime: Boolean
) {
    val isExpandedWidth =
        currentWindowAdaptiveInfo().windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    val minutes = elapsedTime / 60
    val minuteString = if (minutes < 10) "0$minutes" else minutes.toString()
    val seconds = elapsedTime % 60
    val secondString = if (seconds < 10) "0$seconds" else seconds.toString()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        Icon(
            imageVector = if (isWorkTime) Icons.Filled.Work else Icons.Filled.LocalCafe,
            contentDescription = stringResource(if (isWorkTime) R.string.work_time else R.string.rest_time),
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_large))
                .size(dimensionResource(R.dimen.image_size))
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = if(isExpandedWidth) "$minuteString : $secondString" else "$minuteString\n$secondString",
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_small))
                .graphicsLayer(scaleY = 1.5f),
            fontWeight = FontWeight.Bold,
            lineHeight = 1.em,
            fontFamily = Orbitron,
            maxLines = if(isExpandedWidth) 1 else 2,
            autoSize = TextAutoSize.StepBased()
        )
    }
}