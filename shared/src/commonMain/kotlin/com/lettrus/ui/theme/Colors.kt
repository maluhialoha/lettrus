package com.lettrus.ui.theme

import androidx.compose.ui.graphics.Color

// Studio Jour - Palette principale
object LettrusColors {
    // Fond et surfaces
    val Background = Color(0xFFFFE5D9)      // Corail chaud / Pêche
    val Surface = Color(0xFFFFF8F0)         // Blanc cassé / Crème
    val SurfaceVariant = Color(0xFFFFF0E8)  // Crème plus foncé
    val ActiveCell = Color(0xFFE8DDD5)      // Case active - fond plus sombre

    // Feedback lettres
    val Correct = Color(0xFF4ECDC4)         // Vert menthe - bien placée
    val Misplaced = Color(0xFFFFB347)       // Orange doux - mal placée
    val Absent = Color(0xFFE8E8E8)          // Gris perle - absente
    val Empty = Color(0xFFFFFFFF)           // Blanc - case vide

    // Accents et interactions
    val Primary = Color(0xFFFF6B6B)         // Corail foncé - boutons, accent
    val PrimaryVariant = Color(0xFFE85555)  // Corail plus foncé - pressed
    val Secondary = Color(0xFF4ECDC4)       // Vert menthe - secondaire

    // Texte
    val OnBackground = Color(0xFF333333)    // Texte sur fond
    val OnSurface = Color(0xFF333333)       // Texte sur surface
    val OnPrimary = Color(0xFFFFFFFF)       // Texte sur bouton primary
    val OnCorrect = Color(0xFFFFFFFF)       // Texte sur case correcte
    val OnMisplaced = Color(0xFF333333)     // Texte sur case mal placée
    val OnAbsent = Color(0xFF666666)        // Texte sur case absente

    // Clavier
    val KeyBackground = Color(0xFFFFF8F0)   // Fond touche normale
    val KeyPressed = Color(0xFFE8E0D8)      // Fond touche pressée
    val KeyCorrect = Color(0xFF4ECDC4)      // Touche lettre correcte
    val KeyMisplaced = Color(0xFFFFB347)    // Touche lettre mal placée
    val KeyAbsent = Color(0xFFBDBDBD)       // Touche lettre absente

    // Timer
    val TimerNormal = Color(0xFF4ECDC4)     // Timer OK
    val TimerWarning = Color(0xFFFFB347)    // Timer < 3s
    val TimerCritical = Color(0xFFFF6B6B)   // Timer < 1s

    // Autres
    val Divider = Color(0xFFE0D8D0)
    val Shadow = Color(0x1A000000)
}
