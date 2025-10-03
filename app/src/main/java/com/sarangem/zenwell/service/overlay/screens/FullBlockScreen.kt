package com.sarangem.zenwell.service.overlay.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes.Companion.ClamShell
import androidx.compose.material3.MaterialShapes.Companion.SoftBoom
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.graphics.shapes.Morph
import com.sarangem.zenwell.R
import com.sarangem.zenwell.service.overlay.common.APP_BLOCKED
import com.sarangem.zenwell.service.overlay.common.EXPANDED_WIDTH
import com.sarangem.zenwell.service.overlay.common.MEDIUM_WIDTH
import com.sarangem.zenwell.service.overlay.common.MessageCard
import com.sarangem.zenwell.service.overlay.common.MorphPolygonShape
import com.sarangem.zenwell.service.overlay.common.PREVIEW_HEIGHT
import com.sarangem.zenwell.ui.theme.ZenwellTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FullBlockScreen(
    modifier: Modifier = Modifier,
    message: String = APP_BLOCKED
) {
    var isClicked by remember { mutableStateOf(false) }
    val animatedRotation = animateFloatAsState(
        targetValue = if (isClicked) 1f else 0f,
        animationSpec = tween(2000, easing = FastOutSlowInEasing)
    )

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(dimensionResource(R.dimen.padding_small)),
        contentAlignment = Alignment.Center
    ) {
        MessageCard(
            state = isClicked,
            onClick = { isClicked = !isClicked },
            message = message,
            morphPolygonShape = MorphPolygonShape(
                morph = Morph(ClamShell, SoftBoom),
                percentage = animatedRotation.value
            ),
            falseStateContent = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.fillMaxSize(0.45f)
                )
            }
        )
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true, heightDp = PREVIEW_HEIGHT, widthDp = MEDIUM_WIDTH)
@Composable
fun FullBlockScreenColumnPreviewLight() {
    ZenwellTheme(darkTheme = false) {
        FullBlockScreen()
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(heightDp = PREVIEW_HEIGHT, widthDp = MEDIUM_WIDTH)
@Composable
fun FullBlockScreenColumnPreviewDark() {
    ZenwellTheme(darkTheme = true) {
        FullBlockScreen()
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true, heightDp = PREVIEW_HEIGHT, widthDp = EXPANDED_WIDTH)
@Composable
fun FullBlockScreenRowPreviewLight() {
    ZenwellTheme(darkTheme = false) {
        FullBlockScreen()
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(heightDp = PREVIEW_HEIGHT, widthDp = EXPANDED_WIDTH)
@Composable
fun FullBlockScreenRowPreviewDark() {
    ZenwellTheme(darkTheme = true) {
        FullBlockScreen()
    }
}
