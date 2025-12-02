package com.sarangem.zenwell.ui.screens.common

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import com.sarangem.zenwell.R

@Composable
fun TimerBox(
    modifier: Modifier = Modifier,
    progress: Float = 1f,
    backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    strokeColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
    trackColor: Color = MaterialTheme.colorScheme.primary,
    cornerRadiusInDp: Dp = dimensionResource(R.dimen.padding_large),
    strokeWidthInDp: Dp = dimensionResource(R.dimen.padding_large),
    content: @Composable () -> Unit = {}
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .drawBehind {
                val cornerRadius = CornerRadius(cornerRadiusInDp.toPx())
                drawRoundRect(
                    color = backgroundColor,
                    cornerRadius = cornerRadius
                )
                drawRoundRect(
                    color = strokeColor,
                    cornerRadius = cornerRadius,
                    style = Stroke(width = strokeWidthInDp.toPx())
                )
                val path = Path().apply {
                    addRoundRect(
                        RoundRect(
                            rect = Rect(Offset.Zero, size),
                            cornerRadius = cornerRadius
                        )
                    )
                }
                val pathMeasure = PathMeasure()
                pathMeasure.setPath(path, false)
                val pathLength = pathMeasure.length
                val progressPath = Path()
                pathMeasure.getSegment(0f, pathLength * progress, progressPath)
                drawPath(
                    path = progressPath,
                    color = trackColor,
                    style = Stroke(width = strokeWidthInDp.toPx())
                )
            }
    ) {
        content()
    }
}