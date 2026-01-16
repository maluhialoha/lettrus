package com.lettrus.domain.model

enum class GamePhase {
    NOT_STARTED,  // Partie pas encore commencée
    PLAYING,      // En cours de jeu
    WON,          // Mot trouvé
    LOST,         // 6 essais épuisés sans trouver
    TIMEOUT       // Temps écoulé (timer 8 sec)
}
