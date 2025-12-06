package com.sarangem.zenwell.data

import androidx.annotation.StringRes
import com.sarangem.zenwell.R

enum class MathOperators(@get:StringRes val titleRes: Int) {
    ADDITION(R.string.addition),
    SUBTRACTION(R.string.subtraction),
    MULTIPLICATION(R.string.multiplication)
}