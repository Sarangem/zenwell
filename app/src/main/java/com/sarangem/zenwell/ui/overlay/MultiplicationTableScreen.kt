package com.sarangem.zenwell.ui.overlay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.sarangem.zenwell.R
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.ui.overlay.common.APP_BLOCKED
import com.sarangem.zenwell.ui.overlay.common.OverlayScaffold
import com.sarangem.zenwell.ui.theme.Green500
import com.sarangem.zenwell.ui.theme.ZenwellTheme

@Composable
fun MultiplicationTableScreen(
    modifier: Modifier = Modifier,
    schedule: Schedules,
    onTimerEnd: () -> Unit = {}
) {
    var showOpen by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    OverlayScaffold(
        mainPane = { modifier ->
            MultiplicationTableCard(
                modifier = modifier,
                multiplicationMinNum = schedule.multiplicationMinNum,
                multiplicationMaxNum = schedule.multiplicationMaxNum,
                multiplierMinNum = schedule.multiplierMinNum,
                multiplierMaxNum = schedule.multiplierMaxNum,
                onAllCorrect = {
                    focusManager.clearFocus()
                    if (!schedule.requireManualUnlock) onTimerEnd()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiplicationTableCard(
    modifier: Modifier = Modifier,
    multiplicationMinNum: Int,
    multiplicationMaxNum: Int,
    multiplierMinNum: Int,
    multiplierMaxNum: Int,
    onAllCorrect: () -> Unit = {}
) {
    val number = remember { (multiplicationMinNum..multiplicationMaxNum).random() }
    val answers = remember {
        mutableStateMapOf<Int, TextFieldState>().apply {
            (multiplierMinNum..multiplierMaxNum).forEach {
                put(it, TextFieldState())
            }
        }
    }
    val focusManager = LocalFocusManager.current


    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Text(
                text = stringResource(R.string.multiplication_table_of) + " $number",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )
            HorizontalDivider(thickness = dimensionResource(R.dimen.horizontal_divider_thickness))

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = dimensionResource(R.dimen.padding_small))
            ) {
                answers.forEach { (multiplier, state) ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = dimensionResource(R.dimen.padding_tiny),
                                horizontal = dimensionResource(R.dimen.padding_small)
                            ),
                    ) {
                        Text(
                            text = "$number",
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .weight(1f)
                                .padding(dimensionResource(R.dimen.padding_tiny)),
                        )
                        Text(
                            text = "×",
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .weight(1f)
                                .padding(dimensionResource(R.dimen.padding_tiny)),
                        )
                        Text(
                            text = "$multiplier",
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .weight(1f)
                                .padding(dimensionResource(R.dimen.padding_tiny)),
                        )
                        Text(
                            text = "=",
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .weight(1f)
                                .padding(dimensionResource(R.dimen.padding_tiny)),
                        )

                        OutlinedTextField(
                            state = state,
                            modifier = Modifier
                                .weight(5f)
                                .heightIn(min = dimensionResource(R.dimen.padding_small)),
                            contentPadding = PaddingValues(dimensionResource(R.dimen.padding_tiny)),
                            textStyle = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.End),
                            lineLimits = TextFieldLineLimits.SingleLine,
                            colors = OutlinedTextFieldDefaults.colors(
                                errorBorderColor = when (state.text.toString().toIntOrNull()) {
                                    null -> Color.Unspecified
                                    multiplier * number -> Green500
                                    else -> MaterialTheme.colorScheme.error
                                },
                            ),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number,
                                imeAction = if (answers.keys.last() == multiplier) ImeAction.Done else ImeAction.Next
                            ),
                            onKeyboardAction = {
                                if (answers.keys.last() != multiplier) {
                                    focusManager.moveFocus(FocusDirection.Next)
                                }
                                val allCorrect = answers.all { (multiplier, state) ->
                                    state.text.toString().toIntOrNull() == number * multiplier
                                }
                                if (allCorrect) {
                                    onAllCorrect()
                                    focusManager.clearFocus()
                                }
                            }
                        )
                    }
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun MultiplicationTableScreenPreview() {
    ZenwellTheme {
        MultiplicationTableScreen(
            modifier = Modifier.fillMaxSize(),
            schedule = Schedules(
                message = APP_BLOCKED,
                multiplierMaxNum = 20
            )
        )
    }
}