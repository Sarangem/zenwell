/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.overlay

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.overlay.common.APP_BLOCKED
import com.sarangem.zenwell.ui.overlay.common.OpenableMessageCard
import com.sarangem.zenwell.ui.overlay.common.OverlayScaffold
import com.sarangem.zenwell.ui.theme.Green500
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import com.sarangem.zenwell.ui.theme.sizing

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TypingScreen(
    modifier: Modifier = Modifier,
    message: String = APP_BLOCKED,
    requireManualUnlock: Boolean = true,
    onTimerEnd: () -> Unit = {},
    showExit: Boolean = true,
    onExit: () -> Unit = {}
) {
    var showOpen by remember { mutableStateOf(false) }
    var input by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    OverlayScaffold(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
        showExit = showExit,
        onExit = onExit,
        floatingActionButton = {
            MediumExtendedFloatingActionButton(
                onClick = {
                    focusManager.clearFocus()
                    if (input == message) {
                        if (!requireManualUnlock) onTimerEnd()
                        showOpen = true
                    }
                },
                icon = {
                    Icon(
                        painterResource(R.drawable.filled_check),
                        contentDescription = null
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.check),
                        fontWeight = FontWeight.SemiBold
                    )
                },
            )
        },
        content = {
            AnimatedContent(
                targetState = showOpen,
                transitionSpec = { fadeIn(tween()) togetherWith fadeOut(tween()) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(MaterialTheme.sizing.small)
            ) { state ->
                if (state) {
                    OpenableMessageCard(
                        modifier = Modifier.fillMaxSize(),
                        showOpen = true,
                        onTimerEnd = onTimerEnd
                    )
                } else {
                    TypingTextField(
                        message = message,
                        input = input,
                        onInputChange = { input = it }
                    )
                }
            }
        }
    )
}

@Composable
fun TypingTextField(
    modifier: Modifier = Modifier,
    message: String,
    input: String,
    onInputChange: (String) -> Unit = {}
) {
    BasicTextField(
        value = input,
        onValueChange = {
            if (it.length <= message.length) onInputChange(it)
        },
        cursorBrush = SolidColor(TextFieldDefaults.colors().cursorColor),
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        textStyle = MaterialTheme.typography.displaySmall.copy(
            color = Color.Transparent,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace
        ),
        keyboardOptions = KeyboardOptions(
            showKeyboardOnFocus = true,
            autoCorrectEnabled = false,
            imeAction = ImeAction.Default,
            keyboardType = KeyboardType.Text
        ),
        decorationBox = {
            Box {
                Text(
                    text = buildAnnotatedString {
                        message.forEachIndexed { index, char ->
                            val inputChar = if (index < input.length) input[index] else null
                            withStyle(
                                style = SpanStyle(
                                    color = when (inputChar) {
                                        null -> MaterialTheme.colorScheme.outlineVariant
                                        char -> Color.Unspecified
                                        else -> MaterialTheme.colorScheme.error
                                    },
                                    background = if (inputChar != null && inputChar != char) {
                                        MaterialTheme.colorScheme.errorContainer
                                    } else if (inputChar == char) {
                                        Green500.copy(alpha = 0.25f)
                                    } else {
                                        Color.Unspecified
                                    },
                                    fontWeight = if(char == '\n') FontWeight.Thin else null
                                )
                            ) {
                                if (char == '\n') append('↲')
                                append(char)
                            }
                        }
                    },
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace
                )
                it()
            }
        }
    )

}

@Preview(showBackground = true)
@Composable
fun TypingScreenPreview() {
    ZenwellTheme(darkTheme = true) {
        TypingScreen(message = "\uD83D\uDD90\uFE0F\nStop↲\nদাঁড়ান")
    }
}