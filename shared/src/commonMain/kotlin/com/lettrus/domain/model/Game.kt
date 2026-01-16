package com.lettrus.domain.model

import com.lettrus.data.dictionary.Difficulty

data class Game(
    val targetWord: String,
    val letterCount: Int,
    val difficulty: Difficulty,
    val maxAttempts: Int = 6,
    val attempts: List<Attempt> = emptyList(),
    val currentInput: String = "",
    val phase: GamePhase = GamePhase.NOT_STARTED,
    val score: Int = 0,
    val timerEnabled: Boolean = true,
    val timerSeconds: Int = 8
) {
    val firstLetter: Char get() = targetWord.first()
    val attemptsRemaining: Int get() = maxAttempts - attempts.size
    val isGameOver: Boolean get() = phase in listOf(GamePhase.WON, GamePhase.LOST, GamePhase.TIMEOUT)

    // Lettres bien placées à auto-remplir pour le prochain essai
    val correctLettersForNextAttempt: Map<Int, Char>
        get() {
            if (attempts.isEmpty()) return mapOf(0 to firstLetter)

            val correctPositions = mutableMapOf<Int, Char>()
            correctPositions[0] = firstLetter // Première lettre toujours donnée

            attempts.lastOrNull()?.results?.forEach { result ->
                if (result.state == LetterState.CORRECT) {
                    correctPositions[result.position] = result.letter
                }
            }
            return correctPositions
        }

    // Lettres déjà utilisées avec leur meilleur état
    val usedLetters: Map<Char, LetterState>
        get() {
            val letterStates = mutableMapOf<Char, LetterState>()
            attempts.flatMap { it.results }.forEach { result ->
                val currentState = letterStates[result.letter]
                // Priorité: CORRECT > MISPLACED > ABSENT
                if (currentState == null ||
                    (currentState == LetterState.ABSENT && result.state != LetterState.ABSENT) ||
                    (currentState == LetterState.MISPLACED && result.state == LetterState.CORRECT)) {
                    letterStates[result.letter] = result.state
                }
            }
            return letterStates
        }
}
