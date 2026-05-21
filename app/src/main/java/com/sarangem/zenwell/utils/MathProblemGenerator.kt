/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.utils

import com.sarangem.zenwell.model.MathOperators
import com.sarangem.zenwell.model.MathProblem
import kotlin.math.min

fun generateMathProblem(
    numOperands: Int,
    minNumber: Int,
    maxNumber: Int,
    minNumberInMultiplication: Int,
    maxNumberInMultiplication: Int,
    operators: List<MathOperators>,
    showParentheses: Boolean = false,
    allowNegatives: Boolean = false,
): MathProblem {

    var answer = (minNumber..maxNumber).random()
    var shortQuestion = "$answer"
    var longQuestion = "  $answer"
    var previousOperator = MathOperators.MULTIPLICATION

    repeat(numOperands - 1) {

        val operator = if(minNumber > answer && !allowNegatives){
            (operators - MathOperators.SUBTRACTION).randomOrNull() ?: MathOperators.SUBTRACTION
        } else {
            operators.random()
        }

        val num = when (operator) {
            MathOperators.MULTIPLICATION -> {
                (minNumberInMultiplication..maxNumberInMultiplication).random()
            }
            MathOperators.SUBTRACTION if !allowNegatives -> {
                (minNumber..min(answer, maxNumber)).randomOrNull() ?: 0
            }
            else -> {
                (minNumber..maxNumber).random()
            }
        }

        when (operator) {
            MathOperators.ADDITION -> {
                answer += num
                shortQuestion += " + $num"
                longQuestion += "\n+ $num"
            }

            MathOperators.SUBTRACTION -> {
                answer -= num
                shortQuestion += " - $num"
                longQuestion += "\n- $num"
            }

            MathOperators.MULTIPLICATION -> {
                answer *= num
                if (previousOperator != MathOperators.MULTIPLICATION && showParentheses) {
                    shortQuestion = "($shortQuestion) * $num"
                } else {
                    shortQuestion += " * $num"
                }
                longQuestion += "\n* $num"
            }
        }
        previousOperator = operator
    }

    shortQuestion += " = ?"
    return MathProblem(answer, shortQuestion, longQuestion)
}