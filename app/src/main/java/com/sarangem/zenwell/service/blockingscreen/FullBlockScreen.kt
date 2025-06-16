package com.sarangem.zenwell.service.blockingscreen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes.Companion.Square
import androidx.compose.material3.MaterialShapes.Companion.Sunny
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sarangem.zenwell.APP_BLOCKED
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.ZenwellTheme

@Composable
fun FullBlockScreen(
    modifier: Modifier = Modifier,
    message: String,
    height: Float,
    width: Float
) {
    Card(modifier = modifier) {

        if (height < 480 && width < 600) {
            FullBlockScreenCompact(message)
        } else if ((height < 480 && width > 600) || (height < 900 && width > 800)) {
            FullBlockScreenRow(message)
        } else {
            FullBlockScreenColumn(message)
        }
    }
}


// -- CARDS -- //

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LockIconCard(modifier: Modifier = Modifier) {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedRotation = animateFloatAsState(
        targetValue = if (isPressed) 10f else 0f,
        animationSpec = repeatable(
            iterations = 2,
            animation = tween(100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = modifier
            .clip(Sunny.toShape())
            .graphicsLayer(rotationZ = animatedRotation.value)
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .clickable(interactionSource = interactionSource, indication = null, onClick = {})
    ) {
        Icon(
            imageVector = Icons.Filled.Lock,
            contentDescription = null,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_large))
                .fillMaxSize()
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MessageCard(
    modifier: Modifier = Modifier,
    message: String,
) {
    Box(
        modifier = modifier
            .clip(Square.toShape())
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            autoSize = TextAutoSize.StepBased(
                maxFontSize = MaterialTheme.typography.displayMedium.fontSize
            ),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            textAlign = TextAlign.Center,
            lineHeight = MaterialTheme.typography.displayMedium.lineHeight
        )
    }
}


// -- IMPLEMENTATIONS -- //

@Composable
fun FullBlockScreenCompact(message: String = APP_BLOCKED) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        LockIconCard(
            modifier = Modifier
                .weight(0.8F)
                .padding(dimensionResource(R.dimen.padding_large))
        )
        MessageCard(
            message = message,
            modifier = Modifier
                .weight(0.4F)
                .padding(dimensionResource(R.dimen.padding_large))
        )
    }
}

@Composable
fun FullBlockScreenColumn(message: String = APP_BLOCKED) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.weight(0.2F))
        LockIconCard(
            modifier = Modifier
                .weight(0.8F)
                .padding(dimensionResource(R.dimen.padding_large))
        )
        Spacer(Modifier.weight(0.1F))
        MessageCard(
            message = message,
            modifier = Modifier
                .weight(0.7F)
                .padding(dimensionResource(R.dimen.padding_large))
        )
        Spacer(Modifier.weight(0.2F))
    }
}


@Composable
fun FullBlockScreenRow(message: String = APP_BLOCKED) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 150.dp)
    ) {
        Spacer(Modifier.weight(0.1F))
        LockIconCard(
            modifier = Modifier
                .weight(0.6F)
                .padding(dimensionResource(R.dimen.padding_large))
        )
        MessageCard(
            message = message,
            modifier = Modifier
                .weight(0.6F)
                .padding(dimensionResource(R.dimen.padding_large))
        )
        Spacer(Modifier.weight(0.1F))
    }
}


// -- PREVIEW -- //

@Preview(showBackground = true, heightDp = 400, widthDp = 500)
@Composable
fun FullBlockScreenCompactPreviewLight() {
    ZenwellTheme(darkTheme = false) {
        FullBlockScreenCompact()
    }
}

@Preview(heightDp = 400, widthDp = 500)
@Composable
fun FullBlockScreenCompactPreviewDark() {
    ZenwellTheme(darkTheme = true) {
        FullBlockScreenCompact()
    }
}

@Preview(showBackground = true, heightDp = 800, widthDp = 600)
@Composable
fun FullBlockScreenColumnPreviewLight() {
    ZenwellTheme(darkTheme = false) {
        FullBlockScreenColumn()
    }
}

@Preview(heightDp = 800, widthDp = 600)
@Composable
fun FullBlockScreenColumnPreviewDark() {
    ZenwellTheme(darkTheme = true) {
        FullBlockScreenColumn()
    }
}

@Preview(showBackground = true, heightDp = 800, widthDp = 900)
@Composable
fun FullBlockScreenRowPreviewLight() {
    ZenwellTheme(darkTheme = false) {
        FullBlockScreenRow()
    }
}

@Preview(heightDp = 800, widthDp = 900)
@Composable
fun FullBlockScreenRowPreviewDark() {
    ZenwellTheme(darkTheme = true) {
        FullBlockScreenRow()
    }
}