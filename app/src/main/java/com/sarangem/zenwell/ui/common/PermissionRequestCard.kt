/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import com.sarangem.zenwell.ui.theme.sizing

@Composable
fun PermissionRequestCard(
    modifier: Modifier = Modifier,
    text: String,
    buttonText: String,
    onClick: () -> Unit = {},
    cardColor: Color = darkColorScheme().error,
    textColor: Color = darkColorScheme().onError,
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(MaterialTheme.sizing.small)
            .shadow(
                elevation = MaterialTheme.sizing.tiny,
                shape = MaterialTheme.shapes.small
            )
            .clip(MaterialTheme.shapes.small)
            .background(cardColor)
            .height(IntrinsicSize.Min)
    ) {
        Text(
            text = AnnotatedString.fromHtml(text),
            color = textColor,
            modifier = Modifier
                .padding(MaterialTheme.sizing.small)
                .weight(2f)
        )
        Box(
            Modifier
                .fillMaxHeight()
                .background(darkColorScheme().errorContainer)
                .clickable(onClick = onClick)
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = buttonText,
                style = MaterialTheme.typography.bodyLarge,
                color = darkColorScheme().onErrorContainer,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }
}