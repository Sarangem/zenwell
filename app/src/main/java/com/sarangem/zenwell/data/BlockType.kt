package com.sarangem.zenwell.data

import androidx.annotation.StringRes
import com.sarangem.zenwell.R

enum class BlockType(@StringRes val title: Int) {
    FullBlock(R.string.full_block),
    Wait(R.string.wait),
    Breathing(R.string.breathing),
    MathEquation(R.string.math_equation),
    ShowImage(R.string.show_image),
    Grayscale(R.string.grayscale)
}