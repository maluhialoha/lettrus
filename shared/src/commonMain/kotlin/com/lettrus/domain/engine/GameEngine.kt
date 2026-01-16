package com.lettrus.domain.engine

import com.lettrus.data.dictionary.Difficulty
import com.lettrus.data.dictionary.DictionaryRepository
import com.lettrus.domain.model.*

class GameEngine(
    private val dictionaryRepository: DictionaryRepository
) {
    companion object {
        const val MAX_ATTEMPTS = 6
        const val TIMER_SECONDS = 8
        const val POINTS_PER_WORD = 50
    }

    fun startGame(
        letterCount: Int,
        difficulty: Difficulty,
        timerEnabled: Boolean = true
    ): Game {
        val targetWord = dictionaryRepository.getRandomWord(letterCount, difficulty)
        return Game(
            targetWord = targetWord,
            letterCount = letterCount,
            difficulty = difficulty,
            maxAttempts = MAX_ATTEMPTS,
            phase = GamePhase.PLAYING,
            timerEnabled = timerEnabled,
            timerSeconds = TIMER_SECONDS
        )
    }

    fun startGameWithWord(
        word: String,
        difficulty: Difficulty,
        timerEnabled: Boolean = true
    ): Game {
        return Game(
            targetWord = word.uppercase(),
            letterCount = word.length,
            difficulty = difficulty,
            maxAttempts = MAX_ATTEMPTS,
            phase = GamePhase.PLAYING,
            timerEnabled = timerEnabled,
            timerSeconds = TIMER_SECONDS
        )
    }

    sealed class SubmitResult {
        data class Success(val game: Game) : SubmitResult()
        data class InvalidWord(val reason: InvalidReason) : SubmitResult()
    }

    enum class InvalidReason {
        WRONG_LENGTH,
        NOT_IN_DICTIONARY,
        WRONG_FIRST_LETTER,
        ALREADY_TRIED,
        GAME_OVER
    }

    fun submitWord(game: Game, word: String): SubmitResult {
        val normalizedWord = word.uppercase()

        // Vérifications
        if (game.isGameOver) {
            return SubmitResult.InvalidWord(InvalidReason.GAME_OVER)
        }

        if (normalizedWord.length != game.letterCount) {
            return SubmitResult.InvalidWord(InvalidReason.WRONG_LENGTH)
        }

        if (normalizedWord.first() != game.firstLetter) {
            return SubmitResult.InvalidWord(InvalidReason.WRONG_FIRST_LETTER)
        }

        if (game.attempts.any { it.word == normalizedWord }) {
            return SubmitResult.InvalidWord(InvalidReason.ALREADY_TRIED)
        }

        if (!dictionaryRepository.contains(normalizedWord)) {
            return SubmitResult.InvalidWord(InvalidReason.NOT_IN_DICTIONARY)
        }

        // Calculer le feedback avec l'algorithme 2-pass
        val results = evaluateWord(normalizedWord, game.targetWord)
        val isCorrect = normalizedWord == game.targetWord

        val attempt = Attempt(
            word = normalizedWord,
            results = results,
            isCorrect = isCorrect
        )

        val newAttempts = game.attempts + attempt

        val newPhase = when {
            isCorrect -> GamePhase.WON
            newAttempts.size >= game.maxAttempts -> GamePhase.LOST
            else -> GamePhase.PLAYING
        }

        val newScore = if (isCorrect) game.score + POINTS_PER_WORD else game.score

        return SubmitResult.Success(
            game.copy(
                attempts = newAttempts,
                currentInput = "",
                phase = newPhase,
                score = newScore
            )
        )
    }

    fun handleTimeout(game: Game): Game {
        if (game.isGameOver) return game

        return game.copy(phase = GamePhase.TIMEOUT)
    }

    fun updateInput(game: Game, input: String): Game {
        if (game.isGameOver) return game

        // S'assurer que l'input commence par les bonnes lettres
        val correctLetters = game.correctLettersForNextAttempt
        var validInput = input.uppercase()

        // Construire l'input valide en préservant les lettres correctes
        val sb = StringBuilder()
        for (i in 0 until game.letterCount) {
            when {
                i < validInput.length -> {
                    // Si c'est une position avec lettre correcte fixe, utiliser celle-ci
                    if (correctLetters.containsKey(i) && i == 0) {
                        sb.append(correctLetters[i])
                    } else {
                        sb.append(validInput[i])
                    }
                }
                else -> break
            }
        }

        return game.copy(currentInput = sb.toString().take(game.letterCount))
    }

    /**
     * Algorithme 2-pass pour évaluer un mot
     *
     * Pass 1: Identifier toutes les lettres CORRECT (bien placées)
     * Pass 2: Identifier les MISPLACED parmi les lettres restantes
     */
    private fun evaluateWord(guess: String, target: String): List<LetterResult> {
        val results = MutableList(guess.length) { i ->
            LetterResult(guess[i], LetterState.ABSENT, i)
        }

        // Compteur des lettres restantes dans le mot cible
        val targetLetterCounts = target.groupingBy { it }.eachCount().toMutableMap()

        // Pass 1: Marquer les lettres CORRECT
        for (i in guess.indices) {
            if (guess[i] == target[i]) {
                results[i] = LetterResult(guess[i], LetterState.CORRECT, i)
                targetLetterCounts[guess[i]] = targetLetterCounts.getValue(guess[i]) - 1
            }
        }

        // Pass 2: Marquer les lettres MISPLACED
        for (i in guess.indices) {
            if (results[i].state != LetterState.CORRECT) {
                val count = targetLetterCounts[guess[i]] ?: 0
                if (count > 0) {
                    results[i] = LetterResult(guess[i], LetterState.MISPLACED, i)
                    targetLetterCounts[guess[i]] = count - 1
                }
            }
        }

        return results
    }
}
