package com.sarangem.zenwell.model

data class MathProblem(
    val answer: Int,
    val shortQuestion: String,
    val longQuestion: String = shortQuestion
)