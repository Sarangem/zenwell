package com.sarangem.zenwell.ui.overlay

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.database.tables.Schedules
import com.sarangem.zenwell.ui.overlay.common.APP_BLOCKED
import com.sarangem.zenwell.ui.overlay.common.KeypadCard
import com.sarangem.zenwell.ui.overlay.common.OverlayScaffold
import com.sarangem.zenwell.ui.theme.Green5
import com.sarangem.zenwell.ui.theme.ZenwellTheme

@Composable
fun MultiplicationTableScreen(
    modifier: Modifier = Modifier,
    schedule: Schedules,
    onTimerEnd: () -> Unit = {}
) {
    var showOpen by remember { mutableStateOf(false) }

    OverlayScaffold(
        mainPane = { modifier ->
            MultiplicationTableCard(
                modifier = modifier,
                multiplicationMinNum = schedule.multiplicationMinNum,
                multiplicationMaxNum = schedule.multiplicationMaxNum,
                multiplierMinNum = schedule.multiplierMinNum,
                multiplierMaxNum = schedule.multiplierMaxNum,
                onAllCorrect = {
                    showOpen = true
                }
            )
        },
        mainPaneRowWeight = 0.6f,
        mainPaneColumnWeight = 0.7f,
        showOpenDialog = schedule.waitEnterButton,
        showOpen = showOpen,
        message = schedule.message,
        onTimerEnd = onTimerEnd,
        modifier = modifier.fillMaxSize()
    )
}

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
        mutableStateMapOf<Int, Int?>().apply {
            (multiplierMinNum..multiplierMaxNum).forEach {
                put(it,null)
            }
        }
    }
    var selectedMultiplier by remember { mutableIntStateOf(1) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        TableCard(
            modifier = Modifier.weight(1f, fill = false),
            number = number,
            answers = answers,
            selectedMultiplier = selectedMultiplier,
            onMultiplierClick = { selectedMultiplier = it }
        )

        KeypadCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(R.dimen.padding_small)),
            value = answers[selectedMultiplier],
            onValueChange = { answers[selectedMultiplier] = it },
            onEnter = {
                if (selectedMultiplier < multiplierMaxNum) selectedMultiplier++
                val allCorrect = answers.all { (multiplier, answer) ->
                    answer == number * multiplier
                }
                if(allCorrect) onAllCorrect()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TableCard(
    modifier: Modifier = Modifier,
    number: Int,
    answers: Map<Int,Int?>,
    selectedMultiplier: Int,
    onMultiplierClick: (Int) -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
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
            answers.forEach { (multiplier, answer) ->

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
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "×",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "$multiplier",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "=",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )

                    val isSelected = (multiplier == selectedMultiplier)
                    Box(
                        modifier = Modifier
                            .weight(6f)
                            .clickable(onClick = { onMultiplierClick(multiplier) })
                            .border(
                                width = if (answer == null && !isSelected) 0.5.dp else 2.dp,
                                color = when (answer) {
                                    null if isSelected -> {
                                        MaterialTheme.colorScheme.primary
                                    }
                                    null -> {
                                        Color.Black
                                    }
                                    multiplier * number -> {
                                        Green5
                                    }
                                    else -> {
                                        MaterialTheme.colorScheme.error
                                    }
                                },
                                shape = MaterialTheme.shapes.extraSmall
                            )
                    ) {
                        Text(
                            text = answer?.toString() ?: "",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(
                                start = dimensionResource(R.dimen.padding_small),
                                end = dimensionResource(R.dimen.padding_small)
                            )
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
fun MultiplicationTableScreenPreview() {
    ZenwellTheme {
        MultiplicationTableScreen(
            modifier = Modifier.fillMaxSize(),
            schedule = Schedules(
                message = APP_BLOCKED
            )
        )
    }
}