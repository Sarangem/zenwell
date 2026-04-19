/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.edit.fields

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.sarangem.zenwell.R
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.utils.generateMathProblem

@Composable
fun MathProblemExample(schedule: Schedules) {
    val problem = remember(schedule){
         generateMathProblem(
            schedule.mathEquationNumOperands,
            schedule.mathEquationMinNumber,
            schedule.mathEquationMaxNumber,
            schedule.mathEquationMinNumberInMultiplication,
            schedule.mathEquationMaxNumberInMultiplication,
            schedule.allowedMathOperators,
            schedule.mathEquationShowParentheses,
            schedule.mathEquationAllowNegatives
        ) to generateMathProblem(
             schedule.mathEquationNumOperands,
             schedule.mathEquationMinNumber,
             schedule.mathEquationMaxNumber,
             schedule.mathEquationMinNumberInMultiplication,
             schedule.mathEquationMaxNumberInMultiplication,
             schedule.allowedMathOperators,
             schedule.mathEquationShowParentheses,
             schedule.mathEquationAllowNegatives
         )
    }

    DetailsCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
    ) {
        Text(
            text = stringResource(R.string.example),
            style = MaterialTheme.typography.bodyLarge
        )
        AnimatedContent(
            targetState = problem,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))) {
                Text(
                    text = it.first.shortQuestion,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = it.second.shortQuestion,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}