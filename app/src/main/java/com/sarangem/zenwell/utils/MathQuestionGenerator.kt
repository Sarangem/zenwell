package com.sarangem.zenwell.utils

import com.sarangem.zenwell.model.MathOperators
import com.sarangem.zenwell.model.MathQuestion

fun generateMathQuestion(
    numOperands: Int,
    minNumber: Int,
    maxNumber: Int,
    minNumberInMultiplication: Int,
    maxNumberInMultiplication: Int,
    operators: List<MathOperators>,
    showParentheses: Boolean = false,
    allowNegatives: Boolean = false,
): MathQuestion {

    var answer = (minNumber..maxNumber).random()
    var shortQuestion = "$answer"
    var longQuestion = "  $answer"
    var previousOperator = MathOperators.MULTIPLICATION

    repeat(numOperands - 1) {

        val operator = operators.random()

        val num = when (operator) {
            MathOperators.MULTIPLICATION -> {
                (minNumberInMultiplication..maxNumberInMultiplication).random()
            }
            MathOperators.SUBTRACTION if !allowNegatives -> {
                (minNumber..answer).random()
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
                if (previousOperator != MathOperators.MULTIPLICATION && !showParentheses) {
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
    longQuestion += "\n= ?"
    return MathQuestion(answer, shortQuestion, longQuestion)
}