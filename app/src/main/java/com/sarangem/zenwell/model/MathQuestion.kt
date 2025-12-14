package com.sarangem.zenwell.model

data class MathQuestion(
    val answer: Int,
    val shortQuestion: String,
    val longQuestion: String = shortQuestion
)