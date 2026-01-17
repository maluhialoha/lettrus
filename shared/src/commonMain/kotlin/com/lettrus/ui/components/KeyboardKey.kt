package com.lettrus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lettrus.domain.model.LetterState
import com.lettrus.ui.theme.LettrusColors

sealed class KeyType {
    data class Letter(val char: Char) : KeyType()
    data object Backspace : KeyType()
    data object Enter : KeyType()
}

@Composable
fun KeyboardKey(
    keyType: KeyType,
    state: LetterState?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (state) {
        LetterState.CORRECT -> LettrusColors.KeyCorrect
        LetterState.MISPLACED -> LettrusColors.KeyMisplaced
        LetterState.ABSENT -> LettrusColors.KeyAbsent
        else -> LettrusColors.KeyBackground
    }

    val textColor = when (state) {
        LetterState.CORRECT -> LettrusColors.OnCorrect
        LetterState.MISPLACED -> LettrusColors.OnMisplaced
        LetterState.ABSENT -> Color.White
        else -> LettrusColors.OnSurface
    }

    val shape = RoundedCornerShape(6.dp)

    val minWidth = when (keyType) {
        is KeyType.Letter -> 32.dp
        else -> 48.dp
    }

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = minWidth, minHeight = 48.dp)
            .shadow(2.dp, shape)
            .clip(shape)
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        when (keyType) {
            is KeyType.Letter -> {
                Text(
                    text = keyType.char.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
            }
            is KeyType.Backspace -> {
                Text(
                    text = "⌫",
                    fontSize = 20.sp,
                    color = textColor
                )
            }
            is KeyType.Enter -> {
                Text(
                    text = "✓",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = LettrusColors.Primary
                )
            }
        }
    }
}
