package com.sarangem.zenwell.service.blockingscreen

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes.Companion.Circle
import androidx.compose.material3.MaterialShapes.Companion.Square
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.toPath
import com.sarangem.zenwell.R
import kotlin.math.max

fun Morph.getBounds() = calculateMaxBounds().let { Rect(it[0], it[1], it[2], it[3]) }

class MorphPolygonShape(
    private val morph: Morph,
    private val percentage: Float
) : Shape {

    private val matrix = Matrix()
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {

        val bounds = morph.getBounds()
        val maxDimension = max(bounds.width, bounds.height)
        matrix.scale(size.width / maxDimension, size.height / maxDimension)
        matrix.translate(-bounds.left, -bounds.top)

        val path = morph.toPath(progress = percentage).asComposePath()
        path.transform(matrix)
        return Outline.Generic(path)

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
            .padding(dimensionResource(R.dimen.padding_small))
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