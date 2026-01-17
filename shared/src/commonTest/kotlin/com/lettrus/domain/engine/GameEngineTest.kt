package com.lettrus.domain.engine

import com.lettrus.data.dictionary.Difficulty
import com.lettrus.domain.model.GamePhase
import com.lettrus.domain.model.LetterState
import kotlin.test.*

class GameEngineTest {

    private lateinit var engine: GameEngine
    private lateinit var repository: FakeDictionaryRepository

    @BeforeTest
    fun setup() {
        repository = FakeDictionaryRepository(
            validWords = setOf("POISSON", "POULETS", "PENDANT", "PRESQUE", "PASSION"),
            wordToReturn = "POISSON"
        )
        engine = GameEngine(repository)
    }

    @Test
    fun startGame_createsGameWithCorrectState() {
        val game = engine.startGame(7, Difficulty.EASY)

        assertEquals("POISSON", game.targetWord)
        assertEquals(7, game.letterCount)
        assertEquals(Difficulty.EASY, game.difficulty)
        assertEquals(GamePhase.PLAYING, game.phase)
        assertEquals(6, game.maxAttempts)
        assertEquals(0, game.attempts.size)
        assertEquals('P', game.firstLetter)
    }

    @Test
    fun submitWord_correctWord_winsGame() {
        val game = engine.startGame(7, Difficulty.EASY)
        val result = engine.submitWord(game, "POISSON")

        assertTrue(result is GameEngine.SubmitResult.Success)
        val updatedGame = (result as GameEngine.SubmitResult.Success).game

        assertEquals(GamePhase.WON, updatedGame.phase)
        assertEquals(50, updatedGame.score)
        assertEquals(1, updatedGame.attempts.size)
        assertTrue(updatedGame.attempts.first().isCorrect)
    }

    @Test
    fun submitWord_wrongLength_returnsError() {
        val game = engine.startGame(7, Difficulty.EASY)
        val result = engine.submitWord(game, "POISSONS") // 8 lettres

        assertTrue(result is GameEngine.SubmitResult.InvalidWord)
        assertEquals(
            GameEngine.InvalidReason.WRONG_LENGTH,
            (result as GameEngine.SubmitResult.InvalidWord).reason
        )
    }

    @Test
    fun submitWord_wrongFirstLetter_returnsError() {
        val game = engine.startGame(7, Difficulty.EASY)
        val result = engine.submitWord(game, "MAISONS") // Ne commence pas par P

        assertTrue(result is GameEngine.SubmitResult.InvalidWord)
        assertEquals(
            GameEngine.InvalidReason.WRONG_FIRST_LETTER,
            (result as GameEngine.SubmitResult.InvalidWord).reason
        )
    }

    @Test
    fun submitWord_notInDictionary_returnsError() {
        val game = engine.startGame(7, Difficulty.EASY)
        val result = engine.submitWord(game, "PZZZZZZ")

        assertTrue(result is GameEngine.SubmitResult.InvalidWord)
        assertEquals(
            GameEngine.InvalidReason.NOT_IN_DICTIONARY,
            (result as GameEngine.SubmitResult.InvalidWord).reason
        )
    }

    @Test
    fun submitWord_alreadyTried_returnsError() {
        val game = engine.startGame(7, Difficulty.EASY)
        val result1 = engine.submitWord(game, "POULETS")
        assertTrue(result1 is GameEngine.SubmitResult.Success)

        val gameAfterFirst = (result1 as GameEngine.SubmitResult.Success).game
        val result2 = engine.submitWord(gameAfterFirst, "POULETS")

        assertTrue(result2 is GameEngine.SubmitResult.InvalidWord)
        assertEquals(
            GameEngine.InvalidReason.ALREADY_TRIED,
            (result2 as GameEngine.SubmitResult.InvalidWord).reason
        )
    }

    @Test
    fun submitWord_sixWrongAttempts_losesGame() {
        // Créer un repository avec 6 mots différents pour les 6 essais
        val words = setOf("POISSON", "PENDANT", "PRESQUE", "POULETS", "PASSION", "PLAFOND", "PISCINE")
        repository = FakeDictionaryRepository(validWords = words, wordToReturn = "POISSON")
        engine = GameEngine(repository)

        var game = engine.startGame(7, Difficulty.EASY)
        val wrongGuesses = listOf("PENDANT", "PRESQUE", "POULETS", "PASSION", "PLAFOND", "PISCINE")

        for (guess in wrongGuesses) {
            val result = engine.submitWord(game, guess)
            assertTrue(result is GameEngine.SubmitResult.Success, "Failed on guess: $guess")
            game = (result as GameEngine.SubmitResult.Success).game
        }

        assertEquals(GamePhase.LOST, game.phase)
        assertEquals(6, game.attempts.size)
    }

    // Tests de l'algorithme 2-pass

    @Test
    fun evaluateWord_allCorrect() {
        val game = engine.startGame(7, Difficulty.EASY)
        val result = engine.submitWord(game, "POISSON") as GameEngine.SubmitResult.Success

        val results = result.game.attempts.first().results
        assertTrue(results.all { it.state == LetterState.CORRECT })
    }

    @Test
    fun evaluateWord_allAbsent() {
        // POULETS vs POISSON - certaines lettres absentes
        val game = engine.startGame(7, Difficulty.EASY)
        val result = engine.submitWord(game, "POULETS") as GameEngine.SubmitResult.Success

        val results = result.game.attempts.first().results

        // P correct (position 0)
        assertEquals(LetterState.CORRECT, results[0].state)
        // O correct (position 1)
        assertEquals(LetterState.CORRECT, results[1].state)
        // U absent
        assertEquals(LetterState.ABSENT, results[2].state)
        // L absent
        assertEquals(LetterState.ABSENT, results[3].state)
        // E absent
        assertEquals(LetterState.ABSENT, results[4].state)
        // T absent
        assertEquals(LetterState.ABSENT, results[5].state)
        // S misplaced (S existe dans POISSON mais pas en position 6)
        assertEquals(LetterState.MISPLACED, results[6].state)
    }

    @Test
    fun evaluateWord_doubleLetter_onlyOneMarkedMisplaced() {
        // Test avec lettres doubles
        // Target: POISSON = P(0) O(1) I(2) S(3) S(4) O(5) N(6)
        // Guess:  PASSION = P(0) A(1) S(2) S(3) I(4) O(5) N(6)

        repository = FakeDictionaryRepository(
            validWords = setOf("POISSON", "PASSION"),
            wordToReturn = "POISSON"
        )
        engine = GameEngine(repository)

        val game = engine.startGame(7, Difficulty.EASY)
        val result = engine.submitWord(game, "PASSION") as GameEngine.SubmitResult.Success

        val results = result.game.attempts.first().results

        // P: correct (P=P)
        assertEquals(LetterState.CORRECT, results[0].state)
        // A: absent (pas de A dans POISSON)
        assertEquals(LetterState.ABSENT, results[1].state)
        // S: misplaced (S existe dans POISSON aux positions 3,4, mais pas en 2)
        assertEquals(LetterState.MISPLACED, results[2].state)
        // S: correct (S=S à position 3)
        assertEquals(LetterState.CORRECT, results[3].state)
        // I: misplaced (I existe dans POISSON à position 2, mais pas en 4)
        assertEquals(LetterState.MISPLACED, results[4].state)
        // O: correct (O=O)
        assertEquals(LetterState.CORRECT, results[5].state)
        // N: correct (N=N)
        assertEquals(LetterState.CORRECT, results[6].state)
    }

    @Test
    fun handleTimeout_setsTimeoutPhase() {
        val game = engine.startGame(7, Difficulty.EASY)
        val timedOutGame = engine.handleTimeout(game)

        assertEquals(GamePhase.TIMEOUT, timedOutGame.phase)
    }

    @Test
    fun handleTimeout_alreadyOver_noChange() {
        var game = engine.startGame(7, Difficulty.EASY)
        val result = engine.submitWord(game, "POISSON") as GameEngine.SubmitResult.Success
        game = result.game

        assertEquals(GamePhase.WON, game.phase)

        val afterTimeout = engine.handleTimeout(game)
        assertEquals(GamePhase.WON, afterTimeout.phase) // Pas changé en TIMEOUT
    }

    @Test
    fun correctLettersForNextAttempt_includesFirstLetter() {
        val game = engine.startGame(7, Difficulty.EASY)

        val correctLetters = game.correctLettersForNextAttempt
        assertEquals(1, correctLetters.size)
        assertEquals('P', correctLetters[0])
    }

    @Test
    fun correctLettersForNextAttempt_includesCorrectFromPreviousAttempt() {
        val game = engine.startGame(7, Difficulty.EASY)
        val result = engine.submitWord(game, "POULETS") as GameEngine.SubmitResult.Success
        val gameAfter = result.game

        val correctLetters = gameAfter.correctLettersForNextAttempt

        // P et O sont corrects
        assertTrue(correctLetters.containsKey(0))
        assertTrue(correctLetters.containsKey(1))
        assertEquals('P', correctLetters[0])
        assertEquals('O', correctLetters[1])
    }

    @Test
    fun usedLetters_tracksLetterStates() {
        val game = engine.startGame(7, Difficulty.EASY)
        val result = engine.submitWord(game, "POULETS") as GameEngine.SubmitResult.Success
        val gameAfter = result.game

        val used = gameAfter.usedLetters

        assertEquals(LetterState.CORRECT, used['P'])
        assertEquals(LetterState.CORRECT, used['O'])
        assertEquals(LetterState.ABSENT, used['U'])
        assertEquals(LetterState.ABSENT, used['L'])
        assertEquals(LetterState.ABSENT, used['E'])
        assertEquals(LetterState.ABSENT, used['T'])
        assertEquals(LetterState.MISPLACED, used['S'])
    }
}
