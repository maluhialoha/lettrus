package com.lettrus.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lettrus.domain.model.Attempt
import com.lettrus.domain.model.Game
import com.lettrus.domain.model.LetterResult
import com.lettrus.domain.model.LetterState

@Composable
fun LetterGrid(
    game: Game,
    modifier: Modifier = Modifier,
    cellSize: Dp = 44.dp,
    cellSpacing: Dp = 4.dp
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(cellSpacing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(game.maxAttempts) { rowIndex ->
            LetterRow(
                rowIndex = rowIndex,
                game = game,
                cellSize = cellSize,
                cellSpacing = cellSpacing
            )
        }
    }
}

@Composable
private fun LetterRow(
    rowIndex: Int,
    game: Game,
    cellSize: Dp,
    cellSpacing: Dp
) {
    val isCurrentRow = rowIndex == game.attempts.size && !game.isGameOver
    val attempt = game.attempts.getOrNull(rowIndex)

    Row(
        horizontalArrangement = Arrangement.spacedBy(cellSpacing)
    ) {
        repeat(game.letterCount) { colIndex ->
            val cellData = getCellData(
                rowIndex = rowIndex,
                colIndex = colIndex,
                game = game,
                attempt = attempt,
                isCurrentRow = isCurrentRow
            )

            LetterCell(
                letter = cellData.letter,
                state = cellData.state,
                isActive = cellData.isActive,
                size = cellSize
            )
        }
    }
}

private data class CellData(
    val letter: Char?,
    val state: LetterState,
    val isActive: Boolean
)

private fun getCellData(
    rowIndex: Int,
    colIndex: Int,
    game: Game,
    attempt: Attempt?,
    isCurrentRow: Boolean
): CellData {
    return when {
        // Ligne avec un essai validé
        attempt != null -> {
            val result = attempt.results[colIndex]
            CellData(
                letter = result.letter,
                state = result.state,
                isActive = false
            )
        }

        // Ligne en cours de saisie
        isCurrentRow -> {
            val correctLetters = game.correctLettersForNextAttempt
            val inputLetter = game.currentInput.getOrNull(colIndex)
            val prefilledLetter = correctLetters[colIndex]

            when {
                // Lettre saisie par le joueur
                inputLetter != null -> CellData(
                    letter = inputLetter,
                    state = LetterState.EMPTY,
                    isActive = colIndex == game.currentInput.length - 1
                )
                // Lettre pré-remplie (correcte de l'essai précédent)
                prefilledLetter != null -> CellData(
                    letter = prefilledLetter,
                    state = LetterState.EMPTY,
                    isActive = false
                )
                // Case vide, active si c'est la prochaine à remplir
                else -> CellData(
                    letter = null,
                    state = LetterState.EMPTY,
                    isActive = colIndex == game.currentInput.length
                )
            }
        }

        // Ligne future (pas encore jouée)
        else -> {
            // Afficher la première lettre sur toutes les lignes futures
            if (colIndex == 0) {
                CellData(
                    letter = game.firstLetter,
                    state = LetterState.EMPTY,
                    isActive = false
                )
            } else {
                CellData(
                    letter = null,
                    state = LetterState.EMPTY,
                    isActive = false
                )
            }
        }
    }
}
