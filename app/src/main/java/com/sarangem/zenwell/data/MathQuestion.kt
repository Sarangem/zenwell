package com.sarangem.zenwell.data

data class MathQuestion(
    val answer: Int,
    val shortQuestion: String,
    val longQuestion: String = shortQuestion
)