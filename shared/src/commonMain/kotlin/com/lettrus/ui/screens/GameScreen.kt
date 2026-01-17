package com.lettrus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lettrus.domain.model.GamePhase
import com.lettrus.presentation.GameUiState
import com.lettrus.presentation.GameViewModel
import com.lettrus.ui.components.Keyboard
import com.lettrus.ui.components.LetterGrid
import com.lettrus.ui.components.calculateKeyboardStates
import com.lettrus.ui.theme.LettrusColors

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            uiState.isLoading -> LoadingContent()
            uiState.game == null -> StartContent(onStartGame = { viewModel.startGame() })
            else -> GameContent(
                uiState = uiState,
                onLetterInput = viewModel::onLetterInput,
                onBackspace = viewModel::onBackspace,
                onSubmit = viewModel::onSubmit
            )
        }

        // Error snackbar
        uiState.error?.let { error ->
            ErrorBanner(
                message = error,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        // Result dialog
        if (uiState.showResult && uiState.game != null) {
            ResultDialog(
                game = uiState.game!!,
                onDismiss = viewModel::dismissResult,
                onPlayAgain = { viewModel.startGame() }
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = LettrusColors.Primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Chargement...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun StartContent(onStartGame: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "LETTRUS",
            style = MaterialTheme.typography.displayLarge,
            color = LettrusColors.Primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Jeu de lettres",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onStartGame,
            colors = ButtonDefaults.buttonColors(
                containerColor = LettrusColors.Primary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Jouer",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun GameContent(
    uiState: GameUiState,
    onLetterInput: (Char) -> Unit,
    onBackspace: () -> Unit,
    onSubmit: () -> Unit
) {
    val game = uiState.game ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header: Logo + Score + Timer
        GameHeader(
            score = game.score,
            timerSeconds = uiState.timerSeconds,
            timerEnabled = game.timerEnabled
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Grid
        LetterGrid(
            game = game,
            cellSize = 44.dp,
            cellSpacing = 4.dp
        )

        Spacer(modifier = Modifier.weight(1f))

        // Keyboard
        Keyboard(
            letterStates = calculateKeyboardStates(game.attempts),
            onLetterClick = onLetterInput,
            onBackspaceClick = onBackspace,
            onEnterClick = onSubmit,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun GameHeader(
    score: Int,
    timerSeconds: Int,
    timerEnabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Score
        Text(
            text = "$score pts",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = LettrusColors.Primary
        )

        // Logo
        Text(
            text = "LETTRUS",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = LettrusColors.Primary
        )

        // Timer
        if (timerEnabled) {
            val timerColor = when {
                timerSeconds <= 2 -> LettrusColors.TimerCritical
                timerSeconds <= 4 -> LettrusColors.TimerWarning
                else -> LettrusColors.TimerNormal
            }
            Text(
                text = "${timerSeconds}s",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = timerColor
            )
        } else {
            Spacer(modifier = Modifier.weight(0.1f))
        }
    }
}

@Composable
private fun ErrorBanner(
    message: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        color = LettrusColors.Primary,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = LettrusColors.OnPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
private fun ResultDialog(
    game: com.lettrus.domain.model.Game,
    onDismiss: () -> Unit,
    onPlayAgain: () -> Unit
) {
    val isWin = game.phase == GamePhase.WON
    val title = when (game.phase) {
        GamePhase.WON -> "Bravo !"
        GamePhase.LOST -> "Perdu..."
        GamePhase.TIMEOUT -> "Temps écoulé !"
        else -> ""
    }
    val message = when (game.phase) {
        GamePhase.WON -> "Trouvé en ${game.attempts.size} essai${if (game.attempts.size > 1) "s" else ""} !"
        GamePhase.LOST, GamePhase.TIMEOUT -> "Le mot était : ${game.targetWord}"
        else -> ""
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = if (isWin) LettrusColors.Correct else LettrusColors.Primary
            )
        },
        text = {
            Column {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (isWin) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Score: ${game.score} pts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = LettrusColors.Primary
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    onPlayAgain()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = LettrusColors.Primary
                )
            ) {
                Text("Rejouer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer")
            }
        }
    )
}
