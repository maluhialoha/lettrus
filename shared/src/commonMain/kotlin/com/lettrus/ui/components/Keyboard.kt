package com.lettrus.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lettrus.domain.model.LetterState

// Layout AZERTY français
private val ROW_1 = listOf('A', 'Z', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P')
private val ROW_2 = listOf('Q', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L')
private val ROW_3 = listOf('M', 'W', 'X', 'C', 'V', 'B', 'N')

@Composable
fun Keyboard(
    letterStates: Map<Char, LetterState>,
    onLetterClick: (Char) -> Unit,
    onBackspaceClick: () -> Unit,
    onEnterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ligne 1: A Z E R T Y U I O P
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ROW_1.forEach { letter ->
                KeyboardKey(
                    keyType = KeyType.Letter(letter),
                    state = letterStates[letter],
                    onClick = { onLetterClick(letter) }
                )
            }
        }

        // Ligne 2: Q S D F G H J K L ⌫
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Petit espace pour décaler
            Spacer(modifier = Modifier.width(16.dp))

            ROW_2.forEach { letter ->
                KeyboardKey(
                    keyType = KeyType.Letter(letter),
                    state = letterStates[letter],
                    onClick = { onLetterClick(letter) }
                )
            }

            KeyboardKey(
                keyType = KeyType.Backspace,
                state = null,
                onClick = onBackspaceClick
            )
        }

        // Ligne 3: M W X C V B N [espace] ✓
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Plus grand espace pour décaler
            Spacer(modifier = Modifier.width(32.dp))

            ROW_3.forEach { letter ->
                KeyboardKey(
                    keyType = KeyType.Letter(letter),
                    state = letterStates[letter],
                    onClick = { onLetterClick(letter) }
                )
            }

            // Espace avant le bouton valider
            Spacer(modifier = Modifier.width(48.dp))

            KeyboardKey(
                keyType = KeyType.Enter,
                state = null,
                onClick = onEnterClick
            )
        }
    }
}

/**
 * Calcule l'état de chaque lettre du clavier basé sur les essais précédents.
 * Priorité : CORRECT > MISPLACED > ABSENT
 */
fun calculateKeyboardStates(
    attempts: List<com.lettrus.domain.model.Attempt>
): Map<Char, LetterState> {
    val states = mutableMapOf<Char, LetterState>()

    attempts.forEach { attempt ->
        attempt.results.forEach { result ->
            val currentState = states[result.letter]
            val newState = result.state

            // Mettre à jour si le nouvel état est "meilleur"
            val shouldUpdate = when {
                currentState == null -> true
                newState == LetterState.CORRECT -> true
                newState == LetterState.MISPLACED && currentState != LetterState.CORRECT -> true
                else -> false
            }

            if (shouldUpdate) {
                states[result.letter] = newState
            }
        }
    }

    return states
}
