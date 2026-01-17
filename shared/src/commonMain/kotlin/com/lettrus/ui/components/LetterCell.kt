package com.lettrus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lettrus.domain.model.LetterState
import com.lettrus.ui.theme.LettrusColors

@Composable
fun LetterCell(
    letter: Char?,
    state: LetterState,
    isActive: Boolean = false,
    size: Dp = 44.dp,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (state) {
        LetterState.CORRECT -> LettrusColors.Correct
        LetterState.MISPLACED -> LettrusColors.Misplaced
        LetterState.ABSENT -> LettrusColors.Absent
        LetterState.EMPTY -> if (isActive) LettrusColors.SurfaceVariant else LettrusColors.Surface
    }

    val textColor = when (state) {
        LetterState.CORRECT -> LettrusColors.OnCorrect
        LetterState.MISPLACED -> LettrusColors.OnMisplaced
        LetterState.ABSENT -> LettrusColors.OnAbsent
        LetterState.EMPTY -> LettrusColors.OnSurface
    }

    // Forme : carré arrondi pour CORRECT, cercle pour MISPLACED, carré pour le reste
    val shape: Shape = when (state) {
        LetterState.CORRECT -> RoundedCornerShape(8.dp)
        LetterState.MISPLACED -> CircleShape
        else -> RoundedCornerShape(4.dp)
    }

    val borderColor = when {
        isActive -> LettrusColors.Primary
        state == LetterState.EMPTY -> LettrusColors.Divider
        else -> Color.Transparent
    }

    val borderWidth = if (isActive) 2.dp else 1.dp

    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = if (state != LetterState.EMPTY) 2.dp else 0.dp,
                shape = shape
            )
            .clip(shape)
            .background(backgroundColor)
            .border(borderWidth, borderColor, shape),
        contentAlignment = Alignment.Center
    ) {
        letter?.let {
            Text(
                text = it.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}
