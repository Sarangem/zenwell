package com.sarangem.zenwell.ui.overlay

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes.Companion.SoftBoom
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.overlay.common.APP_BLOCKED
import com.sarangem.zenwell.ui.overlay.common.OverlayScaffold
import com.sarangem.zenwell.ui.theme.ZenwellTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TypingScreen(
    modifier: Modifier = Modifier,
    message: String = APP_BLOCKED,
    requireManualUnlock: Boolean = true,
    onTimerEnd: () -> Unit = {}
) {
    var showOpen by remember { mutableStateOf(false) }
    var input by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    OverlayScaffold(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
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
                        imageVector = Icons.Default.Check,
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
                transitionSpec = {
                    fadeIn(
                        animationSpec = tween(1000)
                    ) togetherWith fadeOut(animationSpec = tween(1000))
                },
                modifier = Modifier.fillMaxSize()
            ) { state ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.padding_small)),
                ) {
                    if (state) {
                        Button(
                            onClick = onTimerEnd,
                            shape = SoftBoom.toShape(),
                            modifier = Modifier
                                .fillMaxSize()
                                .aspectRatio(1f)
                                .align(Alignment.Center)
                        ) {
                            Text(
                                text = stringResource(R.string.open),
                                style = MaterialTheme.typography.displayLarge,
                                color = MaterialTheme.colorScheme.onPrimary,
                                lineHeight = 1.em,
                                autoSize = TextAutoSize.StepBased(maxFontSize = 80.sp),
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                            )
                        }
                    } else {
                        TypingTextField(
                            message = message,
                            input = input,
                            onInputChange = { input = it }
                        )
                    }
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
            .verticalScroll(rememberScrollState())
            .padding(bottom = dimensionResource(R.dimen.floating_action_button_height)),
        textStyle = MaterialTheme.typography.displaySmall.copy(
            color = Color.Transparent,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace
        ),
        keyboardOptions = KeyboardOptions(
            showKeyboardOnFocus = true,
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Password,
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
                                    } else {
                                        Color.Unspecified
                                    }
                                )
                            ) {
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
fun TypingScreenPreview() {
    ZenwellTheme(darkTheme = false) {
        TypingScreen()
    }
}