package com.sarangem.zenwell.ui.overlay.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.automirrored.outlined.KeyboardReturn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.sarangem.zenwell.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun KeypadCard(
    modifier: Modifier = Modifier,
    value: Int?,
    onValueChange: (Int?) -> Unit = {},
    onEnter: () -> Unit = {}
) {
    val currentValue = value ?: 0

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.1f))
            .padding(dimensionResource(R.dimen.padding_small)),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_tiny))
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_tiny))
            ) {
                (1..3).forEach {
                    KeypadButton(
                        text = it.toString(),
                        onClick = { onValueChange(currentValue * 10 + it) },
                    )
                }
                KeypadButton(
                    text = "+/-",
                    onClick = { onValueChange(-currentValue) },
                    color = MaterialTheme.colorScheme.tertiaryFixedDim,
                    textColor = MaterialTheme.colorScheme.onTertiaryFixed
                )
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_tiny))
            ) {
                (4..6).forEach {
                    KeypadButton(
                        text = it.toString(),
                        onClick = { onValueChange(currentValue * 10 + it) },
                    )
                }
                KeypadButton(
                    text = "0",
                    onClick = { onValueChange(currentValue * 10) }
                )
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_tiny))
            ) {
                (7..9).forEach {
                    KeypadButton(
                        text = it.toString(),
                        onClick = { onValueChange(currentValue * 10 + it) },
                    )
                }
                KeypadButton(
                    icon = Icons.AutoMirrored.Outlined.Backspace,
                    iconContentDescription = stringResource(R.string.backspace),
                    onClick = { onValueChange(currentValue / 10) },
                    color = MaterialTheme.colorScheme.errorContainer,
                    textColor = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        Spacer(Modifier.width(dimensionResource(R.dimen.padding_large)))
        Button(
            modifier = Modifier.fillMaxHeight(),
            onClick = { onEnter() },
            shapes = ButtonDefaults.shapes(MaterialTheme.shapes.extraLarge),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.tertiary),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardReturn,
                contentDescription = stringResource(R.string.check_answer),
                tint = MaterialTheme.colorScheme.onTertiary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RowScope.KeypadButton(
    text: String = "",
    icon: ImageVector? = null,
    iconContentDescription: String? = null,
    color: Color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
    textColor: Color = MaterialTheme.colorScheme.onTertiaryContainer,
    onClick: () -> Unit = {}
) {
    Button(
        modifier = Modifier.weight(1f),
        onClick = onClick,
        shapes = ButtonDefaults.shapes(
            shape = MaterialTheme.shapes.small,
            pressedShape = ButtonGroupDefaults.connectedMiddleButtonPressShape
        ),
        colors = ButtonDefaults.buttonColors(color),
    ) {
        if (icon == null) {
            Text(
                text = text,
                color = textColor,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        } else {
            Icon(
                imageVector = icon,
                contentDescription = iconContentDescription,
                tint = textColor
            )
        }
    }
}