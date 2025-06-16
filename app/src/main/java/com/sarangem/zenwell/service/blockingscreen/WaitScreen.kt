package com.sarangem.zenwell.service.blockingscreen

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.sarangem.zenwell.APP_BLOCKED
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import kotlinx.coroutines.delay

@Composable
fun WaitScreen(
    modifier: Modifier = Modifier,
    onTimerEnd: () -> Unit = {},
    waitTimeInSeconds: Int,
    showOpenDialog: Boolean,
    message: String,
    height: Float,
    width: Float
) {
    var showOpen by remember { mutableStateOf(false) }
    val timerEnd = {
        if (showOpenDialog) {
            showOpen = true
        } else {
            onTimerEnd()
        }
    }

    Card(modifier = modifier) {

        if ((height < 480 && width > 600) || (height < 900 && width > 800)) {
            WaitScreenRow(timerEnd, waitTimeInSeconds, message, showOpen)
        } else {
            WaitScreenColumn(timerEnd, waitTimeInSeconds, message, showOpen)
        }
    }
}


// -- CARDS -- //

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TimerCard(
    modifier: Modifier = Modifier,
    onTimerEnd: () -> Unit,
    initialValue: Int
) {
    var time by remember { mutableIntStateOf(initialValue) }
    Box(
        modifier = modifier
            .clip(Sunny.toShape())
            .background(MaterialTheme.colorScheme.tertiaryContainer),
    ) {
        Text(
            text = time.toString(),
            autoSize = TextAutoSize.StepBased(),
            maxLines = 1,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.image_size))
                .fillMaxSize()
                .wrapContentSize(align = Alignment.Center)
        )
    }
    LaunchedEffect(time) {
        if (time > 0) {
            delay(1000L)
            time--
        } else {
            onTimerEnd()
        }
    }
}


// -- IMPLEMENTATIONS -- //

@Composable
fun WaitScreenColumn(
    onTimerEnd: () -> Unit = {},
    initialValue: Int,
    message: String,
    showOpen: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.weight(0.1F))

        TimerCard(
            modifier = Modifier
                .weight(0.9F)
                .padding(dimensionResource(R.dimen.padding_small)),
            onTimerEnd = onTimerEnd,
            initialValue = initialValue
        )
        MessageCard(
            message = message,
            modifier = Modifier
                .weight(0.9F)
                .padding(dimensionResource(R.dimen.padding_small)),
            showOpenDialog = showOpen,
            onClick = onTimerEnd
        )

        Spacer(Modifier.weight(0.1F))
    }
}

@Composable
fun WaitScreenRow(
    onTimerEnd: () -> Unit = {},
    initialValue: Int,
    message: String,
    showOpen: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(Modifier.weight(0.1F))

        TimerCard(
            modifier = Modifier
                .weight(0.9F)
                .padding(dimensionResource(R.dimen.padding_small)),
            onTimerEnd = onTimerEnd,
            initialValue = initialValue
        )
        MessageCard(
            message = message,
            modifier = Modifier
                .weight(0.9F)
                .padding(dimensionResource(R.dimen.padding_small)),
            showOpenDialog = showOpen,
            onClick = onTimerEnd
        )

        Spacer(Modifier.weight(0.1F))
    }
}


// -- PREVIEW -- //

@Preview(showBackground = true, heightDp = 400, widthDp = 400)
@Composable
fun WaitScreenCompactPreviewLight() {
    ZenwellTheme(darkTheme = false) {
        WaitScreen(Modifier, { }, Int.MAX_VALUE, true, APP_BLOCKED, 400f, 400f)
    }
}

@Preview(heightDp = 400, widthDp = 400)
@Composable
fun WaitScreenCompactPreviewDark() {
    ZenwellTheme(darkTheme = true) {
        WaitScreen(Modifier, { }, 10, true, APP_BLOCKED, 400f, 400f)
    }
}

@Preview(showBackground = true, heightDp = 700, widthDp = 500)
@Composable
fun WaitScreenColumnPreviewLight() {
    ZenwellTheme(darkTheme = false) {
        WaitScreen(Modifier, { }, Int.MAX_VALUE, true, APP_BLOCKED, 700f, 500f)
    }
}

@Preview(heightDp = 700, widthDp = 500)
@Composable
fun WaitScreenColumnPreviewDark() {
    ZenwellTheme(darkTheme = true) {
        WaitScreen(Modifier, { }, 10, true, APP_BLOCKED, 700f, 500f)
    }
}

@Preview(showBackground = true, heightDp = 400, widthDp = 700)
@Composable
fun WaitScreenRowPreviewLight() {
    ZenwellTheme(darkTheme = false) {
        WaitScreen(Modifier, { }, Int.MAX_VALUE, true, APP_BLOCKED, 400f, 700f)
    }
}

@Preview(heightDp = 400, widthDp = 700)
@Composable
fun WaitScreenRowPreviewDark() {
    ZenwellTheme(darkTheme = true) {
        WaitScreen(Modifier, { }, 10, true, APP_BLOCKED, 400f, 700f)
    }
}
