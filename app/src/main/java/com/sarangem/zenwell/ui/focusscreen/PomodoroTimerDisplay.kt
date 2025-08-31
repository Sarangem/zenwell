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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.Orbitron

@Composable
fun PomodoroTimerDisplay(
    elapsedTime: Int,
    isWorkTime: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        val minutes = elapsedTime / 60
        val seconds = elapsedTime % 60
        Icon(
            imageVector = if (isWorkTime) Icons.Filled.Work else Icons.Filled.LocalCafe,
            contentDescription = stringResource(if (isWorkTime) R.string.work_time else R.string.rest_time),
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_large))
                .size(dimensionResource(R.dimen.image_size))
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = if (minutes < 10) "0$minutes" else minutes.toString(),
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_small))
                .graphicsLayer(scaleY = 1.5f),
            fontWeight = FontWeight.Bold,
            fontFamily = Orbitron,
            maxLines = 1,
            autoSize = TextAutoSize.StepBased()
        )
        Text(
            text = if (seconds < 10) "0$seconds" else seconds.toString(),
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_small))
                .graphicsLayer(scaleY = 1.5f),
            fontWeight = FontWeight.Bold,
            fontFamily = Orbitron,
            maxLines = 1,
            autoSize = TextAutoSize.StepBased()
        )
    }
}