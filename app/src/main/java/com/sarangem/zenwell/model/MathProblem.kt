/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.model

data class MathProblem(
    val answer: Int,
    val shortQuestion: String,
    val longQuestion: String = shortQuestion
)