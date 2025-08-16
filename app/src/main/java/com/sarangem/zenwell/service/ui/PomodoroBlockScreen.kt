package com.sarangem.zenwell.service.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes.Companion.Sunny
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.tables.Schedules
import com.sarangem.zenwell.service.PomodoroWindow
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import com.sarangem.zenwell.utils.isExpandedWidth
import com.sarangem.zenwell.utils.secondsToString
import kotlinx.coroutines.delay

@Composable
fun PomodoroBlockScreen(
    modifier: Modifier = Modifier,
    onTimerEnd: () -> Unit = {},
    message: String,
    width: Float,
    pomodoroWindow: PomodoroWindow
) {
    Card(modifier = modifier) {
        if (isExpandedWidth(width)) {
            PomodoroBlockScreenRow(onTimerEnd, pomodoroWindow, message)
        } else {
            PomodoroBlockScreenColumn(onTimerEnd, pomodoroWindow, message)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PomodoroTimerCard(
    modifier: Modifier = Modifier,
    pomodoroWindow: PomodoroWindow
) {
    var elapsedTime by remember { mutableIntStateOf(pomodoroWindow.elapsedTimeInSeconds) }
    LaunchedEffect(elapsedTime) {
        delay(1000L)
        elapsedTime = pomodoroWindow.elapsedTimeInSeconds
    }
    
    Box(
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding_small))
            .clip(Sunny.toShape())
            .background(MaterialTheme.colorScheme.tertiaryContainer),
    ) {
        Text(
            text = secondsToString(elapsedTime),
            autoSize = TextAutoSize.StepBased(),
            maxLines = 1,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.image_size))
                .fillMaxSize()
                .wrapContentSize(align = Alignment.Center)
        )
    }
}

@Composable
fun PomodoroBlockScreenRow(
    onTimerEnd: () -> Unit = {},
    pomodoroWindow: PomodoroWindow,
    message: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(Modifier.weight(0.1F))

        PomodoroTimerCard(
            modifier = Modifier.weight(0.9F),
            pomodoroWindow = pomodoroWindow
        )
        Spacer(Modifier.weight(0.2f))
        MessageCard(
            message = message,
            modifier = Modifier.weight(0.9F),
            onClick = onTimerEnd
        )
        Spacer(Modifier.weight(0.1F))
    }
}

@Composable
fun PomodoroBlockScreenColumn(
    onTimerEnd: () -> Unit = {},
    pomodoroWindow: PomodoroWindow,
    message: String,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.weight(0.1F))
        PomodoroTimerCard(
            modifier = Modifier.weight(0.9F),
            pomodoroWindow = pomodoroWindow
        )
        Spacer(Modifier.weight(0.2f))
        MessageCard(
            message = message,
            modifier = Modifier.weight(0.9F),
            onClick = onTimerEnd
        )
        Spacer(Modifier.weight(0.1F))
    }
}


// -- PREVIEW -- //

@Preview(showBackground = true, heightDp = PREVIEW_HEIGHT, widthDp = MEDIUM_WIDTH)
@Composable
fun PomodoroBlockScreenColumnPreviewLight() {
    ZenwellTheme(darkTheme = false) {
        PomodoroBlockScreen(Modifier, { }, APP_BLOCKED, MEDIUM_WIDTH.toFloat(), PomodoroWindow(Schedules(isPomodoro = true)) )
    }
}

@Preview(heightDp = PREVIEW_HEIGHT, widthDp = MEDIUM_WIDTH)
@Composable
fun PomodoroBlockScreenColumnPreviewDark() {
    ZenwellTheme(darkTheme = true) {
        PomodoroBlockScreen(Modifier, { }, APP_BLOCKED, MEDIUM_WIDTH.toFloat(), PomodoroWindow(Schedules(isPomodoro = true)))
    }
}

@Preview(showBackground = true, heightDp = PREVIEW_HEIGHT, widthDp = EXPANDED_WIDTH)
@Composable
fun PomodoroBlockScreenRowPreviewLight() {
    ZenwellTheme(darkTheme = false) {
        PomodoroBlockScreen(Modifier, { }, APP_BLOCKED, EXPANDED_WIDTH.toFloat(), PomodoroWindow(Schedules(isPomodoro = true)))
    }
}

@Preview(heightDp = PREVIEW_HEIGHT, widthDp = EXPANDED_WIDTH)
@Composable
fun PomodoroBlockScreenRowPreviewDark() {
    ZenwellTheme(darkTheme = true) {
        PomodoroBlockScreen(Modifier, { }, APP_BLOCKED, EXPANDED_WIDTH.toFloat(), PomodoroWindow(Schedules(isPomodoro = true)))
    }
}
