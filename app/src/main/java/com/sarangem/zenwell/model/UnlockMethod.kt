package com.sarangem.zenwell.model

import androidx.annotation.StringRes
import com.sarangem.zenwell.R

enum class UnlockMethod(@get:StringRes val title: Int) {
    StrictBlock(R.string.strict_block),
    Timer(R.string.timer),
    Breathing(R.string.breathing),
    MathProblem(R.string.math_problem),
    MultiplicationTable(R.string.multiplication_table),
    Typing(R.string.typing),
}