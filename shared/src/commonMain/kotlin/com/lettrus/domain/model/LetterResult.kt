package com.lettrus.domain.model

data class LetterResult(
    val letter: Char,
    val state: LetterState,
    val position: Int
)
