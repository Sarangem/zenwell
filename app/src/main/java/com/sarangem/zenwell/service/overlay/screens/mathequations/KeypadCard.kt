package com.sarangem.zenwell.service.overlay.screens.mathequations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.TextAutoSize
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.sarangem.zenwell.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun KeypadCard(
    modifier: Modifier = Modifier,
    value: Int,
    onValueChange: (Int) -> Unit = {},
    onEnter: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
            .padding(dimensionResource(R.dimen.padding_small)),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_tiny))
        ) {
            (0..2).forEach { rowIndex ->
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_tiny))
                ) {
                    (1..3).forEach { colIndex ->
                        val number = rowIndex * 3 + colIndex
                        KeypadButton(
                            text = number.toString(),
                            onClick = { onValueChange(value * 10 + number) },
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_tiny))
            ) {
                KeypadButton(
                    text = "+/-",
                    onClick = { onValueChange(-value) }
                )
                KeypadButton(
                    text = "0",
                    onClick = { onValueChange(value * 10) }
                )
                KeypadButton(
                    icon = Icons.AutoMirrored.Outlined.Backspace,
                    iconContentDescription = stringResource(R.string.backspace),
                    onClick = { onValueChange(value / 10) }
                )
            }
        }
        Spacer(Modifier.size(dimensionResource(R.dimen.padding_large)))
        Button(
            modifier = Modifier.fillMaxHeight(),
            onClick = { onEnter() },
            shapes = ButtonDefaults.shapes(MaterialTheme.shapes.extraLarge),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardReturn,
                contentDescription = stringResource(R.string.check_answer),
                tint = MaterialTheme.colorScheme.onPrimary
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
    onClick: () -> Unit = {}
) {
    Button(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
        onClick = onClick,
        shapes = ButtonDefaults.shapes(
            shape = MaterialTheme.shapes.small,
            pressedShape = ButtonGroupDefaults.connectedMiddleButtonPressShape
        ),
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.inversePrimary),
    ) {
        if (icon == null) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                autoSize = TextAutoSize.StepBased(
                    
                )
            )
        } else {
            Icon(
                imageVector = icon,
                contentDescription = iconContentDescription
            )
        }
    }
}