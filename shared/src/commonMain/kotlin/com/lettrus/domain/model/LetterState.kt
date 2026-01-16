package com.lettrus.domain.model

enum class LetterState {
    CORRECT,    // Lettre bien placée (vert)
    MISPLACED,  // Lettre mal placée (orange)
    ABSENT,     // Lettre absente (gris)
    EMPTY       // Case vide (pas encore remplie)
}
