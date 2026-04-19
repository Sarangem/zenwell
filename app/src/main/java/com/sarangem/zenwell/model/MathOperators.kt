/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.model

import androidx.annotation.StringRes
import com.sarangem.zenwell.R

enum class MathOperators(@get:StringRes val titleRes: Int) {
    ADDITION(R.string.addition),
    SUBTRACTION(R.string.subtraction),
    MULTIPLICATION(R.string.multiplication)
}