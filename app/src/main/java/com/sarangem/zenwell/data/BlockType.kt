package com.sarangem.zenwell.data

import androidx.annotation.StringRes
import com.sarangem.zenwell.R

enum class BlockType(@get:StringRes val title: Int) {
    FullBlock(R.string.full_block),
    Wait(R.string.wait),
    Breathing(R.string.breathing),
    MathEquation(R.string.math_equation),
    MultiplicationTable(R.string.multiplication_table),
    Typing(R.string.typing),
    Grayscale(R.string.grayscale)
}