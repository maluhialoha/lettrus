package com.lettrus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LettrusColorScheme = lightColorScheme(
    primary = LettrusColors.Primary,
    onPrimary = LettrusColors.OnPrimary,
    primaryContainer = LettrusColors.Primary,
    onPrimaryContainer = LettrusColors.OnPrimary,

    secondary = LettrusColors.Secondary,
    onSecondary = LettrusColors.OnPrimary,
    secondaryContainer = LettrusColors.Secondary,
    onSecondaryContainer = LettrusColors.OnPrimary,

    tertiary = LettrusColors.Misplaced,
    onTertiary = LettrusColors.OnMisplaced,

    background = LettrusColors.Background,
    onBackground = LettrusColors.OnBackground,

    surface = LettrusColors.Surface,
    onSurface = LettrusColors.OnSurface,
    surfaceVariant = LettrusColors.SurfaceVariant,
    onSurfaceVariant = LettrusColors.OnSurface,

    outline = LettrusColors.Divider,
    outlineVariant = LettrusColors.Divider
)

@Composable
fun LettrusTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LettrusColorScheme,
        typography = LettrusTypography,
        content = content
    )
}
