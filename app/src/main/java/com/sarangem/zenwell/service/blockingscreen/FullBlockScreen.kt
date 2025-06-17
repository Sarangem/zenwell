package com.sarangem.zenwell.service.blockingscreen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.spring
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
import androidx.compose.material3.MaterialShapes.Companion.Circle
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.Morph
import com.sarangem.zenwell.APP_BLOCKED
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.ZenwellTheme

@Composable
fun FullBlockScreen(
    modifier: Modifier = Modifier,
    message: String = APP_BLOCKED,
    height: Float,
    width: Float
) {
    Card(modifier = modifier) {

        if ((height < 480 && width > 600) || (height < 900 && width > 800)) {
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
    showOpenDialog: Boolean = false,
    onClick: () -> Unit = {}
) {
    val morph = Morph(Square, Circle)
    val animatedProgress = animateFloatAsState(
        targetValue = if (showOpenDialog) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = (Spring.StiffnessHigh / 10)
        )
    )

    Box(
        modifier = modifier
            .clip(MorphPolygonShape(morph, animatedProgress.value))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .fillMaxSize()
            .clickable(onClick = { if (showOpenDialog) onClick() }),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (showOpenDialog) stringResource(R.string.open) else message,
            autoSize = TextAutoSize.StepBased(
                maxFontSize = MaterialTheme.typography.displayLarge.fontSize
            ),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            textAlign = TextAlign.Center,
            lineHeight = MaterialTheme.typography.displayLarge.lineHeight,
            modifier = Modifier.padding(32.dp)
        )
    }
}


// -- IMPLEMENTATIONS -- //

@Composable
fun FullBlockScreenColumn(message: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.weight(0.1F))

        LockIconCard(
            modifier = Modifier
                .weight(0.9F)
                .padding(dimensionResource(R.dimen.padding_small))
        )
        Spacer(Modifier.weight(0.2f))
        MessageCard(
            message = message,
            modifier = Modifier
                .weight(0.9F)
                .padding(dimensionResource(R.dimen.padding_small))
        )

        Spacer(Modifier.weight(0.1F))
    }
}


@Composable
fun FullBlockScreenRow(message: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(Modifier.weight(0.1F))

        LockIconCard(
            modifier = Modifier
                .weight(0.8F)
                .padding(dimensionResource(R.dimen.padding_small))
        )
        Spacer(Modifier.weight(0.2f))
        MessageCard(
            message = message,
            modifier = Modifier
                .weight(1F)
                .padding(dimensionResource(R.dimen.padding_small))
        )

        Spacer(Modifier.weight(0.1F))
    }
}


// -- PREVIEW -- //

@Preview(showBackground = true, heightDp = 400, widthDp = 400)
@Composable
fun FullBlockScreenCompactPreviewLight() {
    ZenwellTheme(darkTheme = false) {
        FullBlockScreen(height = 400f, width = 400f)
    }
}

@Preview(heightDp = 400, widthDp = 400)
@Composable
fun FullBlockScreenCompactPreviewDark() {
    ZenwellTheme(darkTheme = true) {
        FullBlockScreen(height = 400f, width = 400f)
    }
}

@Preview(showBackground = true, heightDp = 700, widthDp = 500)
@Composable
fun FullBlockScreenColumnPreviewLight() {
    ZenwellTheme(darkTheme = false) {
        FullBlockScreen(height = 700f, width = 500f)
    }
}

@Preview(heightDp = 700, widthDp = 500)
@Composable
fun FullBlockScreenColumnPreviewDark() {
    ZenwellTheme(darkTheme = true) {
        FullBlockScreen(height =  700f, width = 500f)
    }
}

@Preview(showBackground = true, heightDp = 400, widthDp = 700)
@Composable
fun FullBlockScreenRowPreviewLight() {
    ZenwellTheme(darkTheme = false) {
        FullBlockScreen(height = 400f, width = 700f)
    }
}

@Preview(heightDp = 400, widthDp = 700)
@Composable
fun FullBlockScreenRowPreviewDark() {
    ZenwellTheme(darkTheme = true) {
        FullBlockScreen(height = 400f, width = 700f)
    }
}
