package com.sarangem.zenwell.ui.screens.edit.sections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.MathOperators
import com.sarangem.zenwell.data.database.tables.Schedules
import com.sarangem.zenwell.ui.screens.edit.ValidationError
import com.sarangem.zenwell.ui.screens.edit.DetailsCardWithNumberField
import com.sarangem.zenwell.ui.screens.edit.DetailsCardWithRangeNumberField
import com.sarangem.zenwell.ui.screens.edit.DetailsCardWithSwitch
import com.sarangem.zenwell.ui.screens.edit.LabelDetailsCard
import com.sarangem.zenwell.ui.screens.edit.LabelState

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MathEquationDetailsSection(
    schedule: Schedules,
    validationErrors: Set<ValidationError>,
    updateSchedule: (Schedules) -> Unit = {}
){
    // num of operands
    DetailsCardWithNumberField(
        mainText = stringResource(R.string.number_of_operands),
        textFieldValue = schedule.mathEquationNumOperands,
        updateSchedule = {
            updateSchedule(
                schedule.copy(
                    mathEquationNumOperands = it
                )
            )
        },
        isError = ValidationError.MathEquationNumOperands in validationErrors,
        errorMessage = stringResource(R.string.must_be_2_or_greater),
        isStacked = true,
    )

    // Digits of operands in addition and subtraction
    DetailsCardWithRangeNumberField(
        mainText = stringResource(R.string.range_of_digit_of_operands),
        isStacked = true,
        firstFieldValue = schedule.mathEquationMinNumber,
        lastFieldValue = schedule.mathEquationMaxNumber,
        updateFirstValue = {
            updateSchedule(
                schedule.copy(
                    mathEquationMinNumber = it
                )
            )
        },
        updateLastValue = {
            updateSchedule(
                schedule.copy(
                    mathEquationMaxNumber =  it
                )
            )
        }
    )

    // maximum digits of operands in multiplication
    AnimatedVisibility(MathOperators.MULTIPLICATION in schedule.allowedMathOperators) {
        DetailsCardWithRangeNumberField(
            mainText = stringResource(R.string.range_of_digit_of_operands_in_multiplication),
            isStacked = true,
            firstFieldValue = schedule.mathEquationMinNumberInMultiplication,
            lastFieldValue = schedule.mathEquationMaxNumberInMultiplication,
            updateFirstValue = {
                updateSchedule(
                    schedule.copy(
                        mathEquationMinNumberInMultiplication = it
                    )
                )
            },
            updateLastValue = {
                updateSchedule(
                    schedule.copy(
                        mathEquationMaxNumberInMultiplication = it
                    )
                )
            }
        )
    }

    // list of operands
    LabelDetailsCard(
        mainText = stringResource(R.string.operators_to_use),
        labelList = MathOperators.entries.map { operator ->
            LabelState(
                title = stringResource(operator.titleRes),
                isSelected = operator in schedule.allowedMathOperators,
                onSelectChange = {
                    updateSchedule(
                        schedule.copy(
                            allowedMathOperators = if (it) {
                                schedule.allowedMathOperators + operator
                            } else {
                                schedule.allowedMathOperators - operator
                            }
                        )
                    )
                }
            )
        },
        isError = ValidationError.MathOperators in validationErrors,
        errorMessage = stringResource(R.string.select_at_least_one_operator)
    )

    // allow negative answers
    DetailsCardWithSwitch(
        isStacked = true,
        mainText = stringResource(R.string.allow_negative_answers),
        checked = schedule.mathEquationAllowNegatives,
        onCheckedChange = {
            updateSchedule(
                schedule.copy(
                    mathEquationAllowNegatives = it
                )
            )
        }
    )

    // show parentheses
    DetailsCardWithSwitch(
        isStacked = true,
        mainText = stringResource(R.string.show_parentheses),
        checked = schedule.mathEquationShowParentheses,
        onCheckedChange = {
            updateSchedule(
                schedule.copy(
                    mathEquationShowParentheses = it
                )
            )
        }
    )
}