package com.lettrus.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// TODO: Intégrer Nunito quand les fonts custom seront configurées
// Pour l'instant on utilise la font par défaut (sans-serif)
val LettrusFontFamily = FontFamily.Default

val LettrusTypography = Typography(
    // Titre principal (LETTRUS logo)
    displayLarge = TextStyle(
        fontFamily = LettrusFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        lineHeight = 56.sp,
        letterSpacing = 2.sp
    ),

    // Titre écran
    headlineLarge = TextStyle(
        fontFamily = LettrusFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),

    // Sous-titre
    headlineMedium = TextStyle(
        fontFamily = LettrusFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),

    // Titre de section
    titleLarge = TextStyle(
        fontFamily = LettrusFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),

    // Lettre dans la grille
    titleMedium = TextStyle(
        fontFamily = LettrusFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),

    // Bouton
    labelLarge = TextStyle(
        fontFamily = LettrusFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),

    // Touche clavier
    labelMedium = TextStyle(
        fontFamily = LettrusFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),

    // Corps de texte
    bodyLarge = TextStyle(
        fontFamily = LettrusFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),

    // Texte secondaire
    bodyMedium = TextStyle(
        fontFamily = LettrusFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),

    // Petit texte (score, timer)
    bodySmall = TextStyle(
        fontFamily = LettrusFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)
