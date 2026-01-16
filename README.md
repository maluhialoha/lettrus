# Lettrus

Jeu de lettres français pour Android, iOS et F-Droid.

## Description

Lettrus est un jeu de devinette de mots avec une interface chaleureuse et lumineuse. 100% offline, sans pub, sans traceur.

## Fonctionnalités

- **Mode Classique** : Parties illimitées avec des mots aléatoires
- **Mot du Jour** : Le même mot pour tous, partagez votre score
- **Mode Entraînement** : Sans timer, pour s'améliorer (Premium)
- **Contre-la-Montre** : 10 mots en 4min30 (Ultimate)

## Règles du jeu

- 6 essais pour trouver le mot
- Timer de 8 secondes par essai
- Première lettre donnée
- Feedback visuel :
  - Vert = lettre bien placée
  - Orange = lettre mal placée
  - Gris = lettre absente

## Stack technique

- **Framework** : Kotlin Multiplatform (KMP)
- **UI** : Compose Multiplatform
- **Plateformes** : Android, iOS, F-Droid

## Structure du projet

```
lettrus/
├── shared/          # Code partagé KMP (95%)
├── androidApp/      # Shell Android
├── iosApp/          # Shell iOS
└── web/             # Site web (lettrus.com)
```

## Développement

### Prérequis

- JDK 17+
- Android Studio ou IntelliJ IDEA

### Installation des hooks Git

```bash
# Installer le hook pre-push
cp scripts/validator.sh .git/hooks/pre-push
chmod +x .git/hooks/pre-push
```

Le hook exécute automatiquement les tests avant chaque push.

### Build

```bash
# Android
./gradlew :androidApp:assembleDebug

# Tests
./gradlew :shared:allTests
```

## Licence

AGPL-3.0 - Voir [LICENSE](LICENSE)

---

*Zéro pub. Zéro traceur. Juste du jeu.*
