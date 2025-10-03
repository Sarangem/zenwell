package com.sarangem.zenwell.service.overlay.screens.mathequations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.window.core.layout.WindowSizeClass
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.MathOperators
import com.sarangem.zenwell.service.overlay.common.APP_BLOCKED
import com.sarangem.zenwell.service.overlay.common.TimerMessageCard
import com.sarangem.zenwell.ui.theme.Green3
import com.sarangem.zenwell.ui.theme.Green5
import com.sarangem.zenwell.ui.theme.Red5
import com.sarangem.zenwell.ui.theme.ZenwellTheme

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

    val questionCard: @Composable (Modifier) -> Unit = { modifier ->
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
    }
    val messageCard: @Composable (Modifier) -> Unit = { modifier ->
        TimerMessageCard(
            modifier = modifier,
            showOpenDialog = showOpenDialog,
            showOpen = showOpen,
            message = message,
            onTimerEnd = onTimerEnd,
        )
    }

    if (currentWindowAdaptiveInfo()
            .windowSizeClass
            .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    ) {
        Row(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(dimensionResource(R.dimen.padding_small)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            questionCard(Modifier.weight(0.6f))
            messageCard(
                Modifier
                    .weight(0.4f)
                    .padding(dimensionResource(R.dimen.padding_small))
            )
        }
    } else {
        Column(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(dimensionResource(R.dimen.padding_medium)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            questionCard(Modifier.weight(0.7f))
            messageCard(Modifier.weight(0.3f))
        }
    }
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

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        QuestionCard(question = question)
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
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