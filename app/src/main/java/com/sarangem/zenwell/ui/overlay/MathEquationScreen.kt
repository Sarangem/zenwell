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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.MathOperators
import com.sarangem.zenwell.ui.overlay.common.KeypadCard
import com.sarangem.zenwell.ui.overlay.common.APP_BLOCKED
import com.sarangem.zenwell.ui.overlay.common.OverlayScaffold
import com.sarangem.zenwell.ui.theme.Green3
import com.sarangem.zenwell.ui.theme.Green5
import com.sarangem.zenwell.ui.theme.Orbitron
import com.sarangem.zenwell.ui.theme.Red5
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import com.sarangem.zenwell.data.MathQuestion
import com.sarangem.zenwell.utils.generateMathQuestion
import kotlinx.coroutines.launch

@Composable
fun MathEquationScreen(
    modifier: Modifier = Modifier,
    numOperands: Int,
    minOperandDigits: Int,
    maxOperandDigits: Int,
    minOperandDigitsInMultiplication: Int,
    maxOperandDigitsInMultiplication: Int,
    allowedMathOperators: List<MathOperators> = listOf(
        MathOperators.ADDITION,
        MathOperators.SUBTRACTION,
        MathOperators.MULTIPLICATION
    ),
    showParentheses: Boolean = true,
    allowNegatives: Boolean = false,
    onTimerEnd: () -> Unit = {},
    showOpenDialog: Boolean,
    message: String
) {
    var showOpen by remember { mutableStateOf(false) }

    OverlayScaffold(
        mainPane = { modifier ->
            MathEquationCard(
                modifier = modifier,
                question = generateMathQuestion(
                    numOperands,
                    minOperandDigits,
                    maxOperandDigits,
                    minOperandDigitsInMultiplication,
                    maxOperandDigitsInMultiplication,
                    allowedMathOperators,
                    showParentheses,
                    allowNegatives
                ),
                onCorrectAnswer = {
                    showOpen = true
                }
            )
        },
        mainPaneRowWeight = 0.6f,
        mainPaneColumnWeight = 0.7f,
        showOpenDialog = showOpenDialog,
        showOpen = showOpen,
        message = message,
        onTimerEnd = onTimerEnd,
        modifier = modifier.fillMaxSize()
    )
}

enum class MathEquationAnswerState { UNCHECKED, ERROR, CORRECT }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MathEquationCard(
    modifier: Modifier = Modifier,
    question: MathQuestion,
    onCorrectAnswer: () -> Unit = {}
) {
    var answer by remember { mutableIntStateOf(0) }
    var answerState by remember { mutableStateOf(MathEquationAnswerState.UNCHECKED) }

    Column(modifier = modifier) {
        QuestionCard(
            question = question,
            modifier = Modifier.animateContentSize()
        )

        Spacer(Modifier.weight(1f))

        Column {
            OutlinedTextField(
                modifier = Modifier
                    .padding(top = dimensionResource(R.dimen.padding_medium))
                    .fillMaxWidth(),
                value = answer.toString(),
                onValueChange = {},
                shape = MaterialTheme.shapes.medium,
                label = {
                    Text(
                        text = when (answerState) {
                            MathEquationAnswerState.UNCHECKED -> stringResource(R.string.enter_your_answer)
                            MathEquationAnswerState.ERROR -> stringResource(R.string.wrong_answer)
                            else -> stringResource(R.string.correct_answer)
                        }
                    )
                },
                singleLine = true,
                isError = answerState != MathEquationAnswerState.UNCHECKED,
                colors = OutlinedTextFieldDefaults.colors(
                    errorBorderColor = if (answerState == MathEquationAnswerState.ERROR) Red5 else Green3,
                    errorLabelColor = if (answerState == MathEquationAnswerState.ERROR) Red5 else Green5
                ),
                textStyle = MaterialTheme.typography.titleLarge,
            )

            KeypadCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(R.dimen.padding_small)),
                value = answer,
                onValueChange = { answer = it },
                onEnter = {
                    if (answer == question.answer) {
                        answerState = MathEquationAnswerState.CORRECT
                        onCorrectAnswer()
                    } else {
                        answerState = MathEquationAnswerState.ERROR
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QuestionCard(
    modifier: Modifier = Modifier,
    question: MathQuestion
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
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Left,
                fontFamily = Orbitron,
                maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                modifier = Modifier
                    .graphicsLayer(scaleY = 1.5f)
                    .padding(
                        start = dimensionResource(R.dimen.padding_small),
                        end = dimensionResource(R.dimen.padding_small),
                        top = if (isExpanded) 48.dp else dimensionResource(R.dimen.padding_large),
                        bottom = if (isExpanded) 48.dp else dimensionResource(R.dimen.padding_large)
                    )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
fun MathEquationScreenPreview() {
    ZenwellTheme {
        MathEquationScreen(
            modifier = Modifier.fillMaxSize(),
            numOperands = 3,
            minOperandDigits = 3,
            maxOperandDigits = 4,
            minOperandDigitsInMultiplication = 2,
            maxOperandDigitsInMultiplication = 2,
            showOpenDialog = true,
            message = APP_BLOCKED
        )
    }
}