package com.lettrus.presentation

import com.lettrus.data.dictionary.Difficulty
import com.lettrus.data.dictionary.DictionaryRepository
import com.lettrus.domain.engine.GameEngine
import com.lettrus.domain.model.Game
import com.lettrus.domain.model.GamePhase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GameUiState(
    val game: Game? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val showResult: Boolean = false,
    val timerSeconds: Int = 8
)

class GameViewModel(
    private val dictionaryRepository: DictionaryRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val gameEngine = GameEngine(dictionaryRepository)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadDictionary()
    }

    private fun loadDictionary() {
        scope.launch {
            try {
                dictionaryRepository.loadDictionary()
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Erreur chargement dictionnaire: ${e.message}"
                )
            }
        }
    }

    fun startGame(
        letterCount: Int = 7,
        difficulty: Difficulty = Difficulty.EASY,
        timerEnabled: Boolean = true
    ) {
        val game = gameEngine.startGame(letterCount, difficulty, timerEnabled)
        _uiState.value = _uiState.value.copy(
            game = game,
            showResult = false,
            timerSeconds = GameEngine.TIMER_SECONDS
        )
        if (timerEnabled) {
            startTimer()
        }
    }

    fun onLetterInput(letter: Char) {
        val currentGame = _uiState.value.game ?: return
        if (currentGame.isGameOver) return

        val newInput = currentGame.currentInput + letter
        if (newInput.length <= currentGame.letterCount) {
            val updatedGame = gameEngine.updateInput(currentGame, newInput)
            _uiState.value = _uiState.value.copy(game = updatedGame)
        }
    }

    fun onBackspace() {
        val currentGame = _uiState.value.game ?: return
        if (currentGame.isGameOver) return

        val correctLetters = currentGame.correctLettersForNextAttempt
        val minLength = correctLetters.keys.maxOrNull()?.let { it + 1 } ?: 1

        if (currentGame.currentInput.length > minLength) {
            val newInput = currentGame.currentInput.dropLast(1)
            val updatedGame = gameEngine.updateInput(currentGame, newInput)
            _uiState.value = _uiState.value.copy(game = updatedGame)
        }
    }

    fun onSubmit() {
        val currentGame = _uiState.value.game ?: return
        if (currentGame.isGameOver) return
        if (currentGame.currentInput.length != currentGame.letterCount) return

        when (val result = gameEngine.submitWord(currentGame, currentGame.currentInput)) {
            is GameEngine.SubmitResult.Success -> {
                val newGame = result.game
                _uiState.value = _uiState.value.copy(
                    game = newGame,
                    showResult = newGame.isGameOver,
                    timerSeconds = GameEngine.TIMER_SECONDS
                )

                if (newGame.isGameOver) {
                    stopTimer()
                } else if (newGame.timerEnabled) {
                    restartTimer()
                }
            }
            is GameEngine.SubmitResult.InvalidWord -> {
                _uiState.value = _uiState.value.copy(
                    error = when (result.reason) {
                        GameEngine.InvalidReason.NOT_IN_DICTIONARY -> "Mot inconnu"
                        GameEngine.InvalidReason.WRONG_FIRST_LETTER -> "Mauvaise première lettre"
                        GameEngine.InvalidReason.ALREADY_TRIED -> "Déjà essayé"
                        GameEngine.InvalidReason.WRONG_LENGTH -> "Mauvaise longueur"
                        GameEngine.InvalidReason.GAME_OVER -> "Partie terminée"
                    }
                )
                // Clear error after delay
                scope.launch {
                    delay(2000)
                    _uiState.value = _uiState.value.copy(error = null)
                }
            }
        }
    }

    fun dismissResult() {
        _uiState.value = _uiState.value.copy(showResult = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.launch {
            for (seconds in GameEngine.TIMER_SECONDS downTo 1) {
                _uiState.value = _uiState.value.copy(timerSeconds = seconds)
                delay(1000)
            }
            // Timeout
            handleTimeout()
        }
    }

    private fun restartTimer() {
        startTimer()
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun handleTimeout() {
        val currentGame = _uiState.value.game ?: return
        val newGame = gameEngine.handleTimeout(currentGame)
        _uiState.value = _uiState.value.copy(
            game = newGame,
            showResult = true
        )
        stopTimer()
    }
}
