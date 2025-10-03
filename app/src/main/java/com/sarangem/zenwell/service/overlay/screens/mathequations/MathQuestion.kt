package com.sarangem.zenwell.service.overlay.screens.mathequations

import com.sarangem.zenwell.data.MathOperators

data class MathQuestion(
    val answer: Int,
    val shortQuestion: String,
    val longQuestion: String = shortQuestion
)

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
    var shortQuestion = answer.toString()
    var longQuestion = shortQuestion
    var previousOperator = MathOperators.MULTIPLICATION

    repeat(numOperands - 1) {

        val operator = operators.random()

        val num = if (operator == MathOperators.MULTIPLICATION) {
            (minNumberInMultiplication..maxNumberInMultiplication).random()
        } else if (operator == MathOperators.SUBTRACTION && !allowNegatives) {
            (minNumber..answer).random()
        } else {
            (minNumber..maxNumber).random()
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