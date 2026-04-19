/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.overlay

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.text.isDigitsOnly
import com.sarangem.zenwell.R
import com.sarangem.zenwell.model.MathProblem
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.ui.overlay.common.APP_BLOCKED
import com.sarangem.zenwell.ui.overlay.common.OverlayScaffold
import com.sarangem.zenwell.ui.theme.Green500
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import com.sarangem.zenwell.utils.generateMathProblem
import kotlinx.coroutines.launch

@Composable
fun MathProblemScreen(
    modifier: Modifier = Modifier,
    schedule: Schedules,
    onTimerEnd: () -> Unit = {},
) {
    var showOpen by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    OverlayScaffold(
        mainPane = { modifier ->
            MathEquationCard(
                modifier = modifier,
                question = generateMathProblem(
                    schedule.mathEquationNumOperands,
                    schedule.mathEquationMinNumber,
                    schedule.mathEquationMaxNumber,
                    schedule.mathEquationMinNumberInMultiplication,
                    schedule.mathEquationMaxNumberInMultiplication,
                    schedule.allowedMathOperators,
                    schedule.mathEquationShowParentheses,
                    schedule.mathEquationAllowNegatives
                ),
                onCorrectAnswer = {
                    focusManager.clearFocus()
                    if(!schedule.requireManualUnlock) onTimerEnd()
                    showOpen = true
                }
            )
        },
        mainPaneRowWeight = 0.6f,
        mainPaneColumnWeight = 0.7f,
        showOpen = showOpen,
        message = schedule.message,
        onTimerEnd = onTimerEnd,
        modifier = modifier.fillMaxSize()
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MathEquationCard(
    modifier: Modifier = Modifier,
    question: MathProblem,
    onCorrectAnswer: () -> Unit = {}
) {
    var answer: Int? by remember { mutableStateOf(null) }
    var isCorrect: Boolean? by remember { mutableStateOf(null) }
    val borderColor = when (isCorrect) {
        null -> Color.Black
        true -> Green500
        false -> MaterialTheme.colorScheme.error
    }

    Column(modifier = modifier) {
        QuestionCard(
            question = question,
            modifier = Modifier.animateContentSize()
        )

        OutlinedTextField(
            modifier = Modifier
                .padding(top = dimensionResource(R.dimen.padding_medium))
                .fillMaxWidth(),
            value = answer?.toString() ?: "",
            onValueChange = { num ->
                if (num.isDigitsOnly()) answer = num.toIntOrNull()
            },
            shape = MaterialTheme.shapes.medium,
            label = {
                Text(
                    text = when (isCorrect) {
                        null -> stringResource(R.string.enter_your_answer)
                        true -> stringResource(R.string.correct_answer)
                        false -> stringResource(R.string.wrong_answer)
                    }
                )
            },
            singleLine = true,
            isError = isCorrect != null,
            colors = OutlinedTextFieldDefaults.colors(
                errorBorderColor = borderColor,
                errorLabelColor = borderColor,
            ),
            textStyle = MaterialTheme.typography.titleLarge,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (answer == question.answer) {
                        isCorrect = true
                        onCorrectAnswer()
                    } else {
                        isCorrect = false
                    }
                }
            )
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QuestionCard(
    modifier: Modifier = Modifier,
    question: MathProblem
) {
    var isExpanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val tooltipState = rememberTooltipState()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_small)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.problem),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )
            Spacer(Modifier.weight(1f))
            TooltipBox(
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                    TooltipAnchorPosition.Left
                ),
                state = tooltipState,
                tooltip = {
                    RichTooltip(
                        title = {
                            Text(
                                text = stringResource(R.string.order_of_equations),
                                style = MaterialTheme.typography.titleSmallEmphasized
                            )
                        },
                        text = {
                            Text(
                                text = if (isExpanded) {
                                    stringResource(R.string.top_to_bottom)
                                } else {
                                    stringResource(R.string.left_to_right)
                                }
                            )
                        }
                    )
                }
            ) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            tooltipState.show()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = stringResource(R.string.more_information)
                    )
                }
            }
            IconButton(
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)),
                onClick = { isExpanded = !isExpanded }
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) stringResource(R.string.collapse) else stringResource(
                        R.string.expand
                    )
                )
            }
        }

        HorizontalDivider(thickness = dimensionResource(R.dimen.horizontal_divider_thickness))

        Row(Modifier.horizontalScroll(rememberScrollState())) {
            Text(
                text = if (isExpanded) question.longQuestion else question.shortQuestion,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Left,
                fontFamily = FontFamily.Monospace,
                maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                modifier = Modifier
                    .padding(
                        start = dimensionResource(R.dimen.padding_small),
                        end = dimensionResource(R.dimen.padding_small),
                        top = dimensionResource(R.dimen.padding_medium),
                        bottom = dimensionResource(R.dimen.padding_medium)
                    )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
fun MathProblemScreenPreview() {
    ZenwellTheme {
        MathProblemScreen(
            modifier = Modifier.fillMaxSize(),
            schedule = Schedules(
                mathEquationNumOperands = 3,
                mathEquationMinNumber = 3,
                mathEquationMaxNumber = 4,
                mathEquationMinNumberInMultiplication = 2,
                mathEquationMaxNumberInMultiplication = 2,
                requireManualUnlock = true,
                message = APP_BLOCKED
            )
        )
    }
}

@Preview
@Composable
fun MathProblemScreenDarkPreview() {
    ZenwellTheme(darkTheme = true) {
        MathProblemScreen(
            modifier = Modifier.fillMaxSize(),
            schedule = Schedules(
                mathEquationNumOperands = 3,
                mathEquationMinNumber = 3,
                mathEquationMaxNumber = 4,
                mathEquationMinNumberInMultiplication = 2,
                mathEquationMaxNumberInMultiplication = 2,
                requireManualUnlock = true,
                message = APP_BLOCKED
            )
        )
    }
}