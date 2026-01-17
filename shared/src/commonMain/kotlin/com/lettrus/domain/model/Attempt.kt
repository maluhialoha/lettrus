package com.lettrus.domain.model

data class Attempt(
    val word: String,
    val results: List<LetterResult>,
    val isCorrect: Boolean
) {
    val letterCount: Int get() = word.length
}
