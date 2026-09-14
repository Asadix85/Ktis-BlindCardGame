# 🎴 Ktis — Blind Card Game

🇬🇧 English | [🇮🇷 فارسی](README.fa.md)

A local multiplayer Android card game based entirely on luck.

In each turn, players blindly draw a card from their own pile and place it face-down in the center. Once everyone has played, the cards are revealed and the highest-ranked card wins the round and collects all cards in the center.

> **No strategy. No card selection. Just luck. 🍀**

---

## ✨ Features

### Gameplay

- 🎮 Local multiplayer gameplay
- 👥 2–8 players
- 🃏 Standard 52-card deck
- 🃏 Support for multiple decks
- 🔀 Automatic shuffling and dealing
- 🙈 Blind card drawing
- 🏆 Round winner calculation
- ⚔️ Tie-breaking rounds
- 🔁 Consecutive tie handling
- 🤝 Final tie rule — remaining table cards are split evenly between tied players when no cards remain
- 🏅 Card collection and scoring
- 👑 Final winner calculation
- 🎲 Final tie-break system

### Experience

- 💾 Save and continue support
- 🎨 Unified wooden theme across all screens
- ✨ Animated loading screen
- 🎬 Smooth card animations (throw, rotation, landing)
- 🔄 Rotating table with delayed turn transition
- 📱 Pass-the-phone multiplayer flow
- 🔊 Sound effects and background music
- 📳 Haptic feedback (vibration)

### Settings

- 🔊 Toggle sound
- 🎵 Toggle music
- 📳 Toggle vibration

---

## 🎮 How to Play

1. Choose the number of players.
2. Enter the players' names.
3. The deck is shuffled and distributed evenly.
4. Players take turns drawing one card from their own pile **without seeing it**.
5. The card is placed face-down in the center.
6. After all active players have played, the cards are revealed.
7. The player with the highest card wins the round.
8. The winner collects all cards played in the center.
9. If two or more players have the same highest rank, only those players continue the tie-break.
10. The game continues until all playable cards have been used.
11. The player with the most collected cards wins the game.

### Card Ranking

From highest to lowest:

**A → K → Q → J → 10 → 9 → 8 → 7 → 6 → 5 → 4 → 3 → 2**

Suits do not affect the result.

---

## 🏆 Game Ending

When all playable cards have been exhausted, each player's collected cards become their final score.

The player with the highest score wins.

If the final score is tied, the tied players enter a final random tie-break to determine the winner.

### Tie in the Final Round

If the last round ends in a tie and the tied players have no cards left to play, the cards remaining on the table are split evenly between them as fractional scores (for example, 2.5 cards each).

---

## 🃏 Deck System

The recommended number of decks depends on the number of players:

| Players | Decks |
|---------|-------|
| 2–4     | 1     |
| 5–8     | 2     |
| 9+      | 3     |

Cards are distributed as evenly as possible. Any remaining cards that cannot be distributed evenly are removed.

---

## 📱 Download

### Latest Release

**Ktis v1.5.0 — Local Polish**

[⬇️ Download APK](../../releases/tag/v1.5.0)

> A polished local release with save/continue, unified theme, animations, sound, and the final tie rule.

---

## 🛠️ Tech Stack

- **Kotlin**
- **Android**
- **Jetpack Compose**
- **Gradle**
- **Android SDK**
- **JUnit**

The project is designed with a simple separation between game logic and the user interface.

---

## 📂 Project Structure

```text
app/
└── src/
    ├── main/
    │   └── java/com/example/ktis/
    │       ├── domain/
    │       │   ├── model/
    │       │   ├── engine/
    │       │   │   ├── GameEngine.kt
    │       │   │   ├── GameRules.kt
    │       │   │   ├── GameResult.kt
    │       │   │   ├── GameSaveMapper.kt
    │       │   │   └── DeckBalancer.kt
    │       │   └── save/
    │       │       ├── GameSaveData.kt
    │       │       └── SaveManager.kt
    │       ├── ui/
    │       │   ├── audio/
    │       │   ├── components/
    │       │   │   ├── CardView.kt
    │       │   │   ├── PlayerView.kt
    │       │   │   └── WoodenButton.kt
    │       │   ├── screens/
    │       │   │   ├── game/
    │       │   │   │   ├── GameConstants.kt
    │       │   │   │   ├── GameTopBar.kt
    │       │   │   │   ├── MessageCard.kt
    │       │   │   │   ├── TurnLabel.kt
    │       │   │   │   ├── GameBottomButtons.kt
    │       │   │   │   ├── CardCountLabel.kt
    │       │   │   │   ├── PlayerCardStack.kt
    │       │   │   │   ├── TableCard.kt
    │       │   │   │   └── TableArea.kt
    │       │   │   └── (menu screens)
    │       │   └── theme/
    │       │       ├── Color.kt
    │       │       ├── Type.kt
    │       │       ├── Dimens.kt
    │       │       └── Theme.kt
    │       └── MainActivity.kt
    │
    └── test/
        └── java/com/example/ktis/
```

## 🚀 Version 1.5.0
Status: Local Polish Release

Version 1.5.0 focuses on polishing the local experience and adding the final missing gameplay rule.

### What's New in 1.5.0
New Rules

Tie in the final round is resolved by splitting the remaining table cards among tied players.

Scores are now fractional (Float) to support this split.

#### UI / UX

Unified wooden theme across every screen.

New animated loading screen (logo, brand typing, progress bar).

Shared WoodenButton component with press animation.

Improved Result screen with fractional scores and split notification.

Delayed table rotation — card lands in front of the current player, then the table rotates.

#### Game Animations

Cards now face the center of the table.

Cards from the same player land exactly on top of each other.

Fixed card jumping during consecutive throws.

#### Code Cleanup

Extracted GameSaveMapper and DeckBalancer from GameEngine.

Split GameScreen into focused components under ui/screens/game/.

Consolidated colors and fonts under ui/theme/.

### What's New in 1.0.0
The first playable implementation of the core game rules.

### 🗺️ Roadmap
Done in 1.5.0:

✅ Save and continue

✅ Settings

✅ Sound and music

✅ Vibration

✅ Animated loading

✅ Improved visual design

### Still planned:

🎬 Drag and throw card interaction

🎨 Custom card artwork

🌙 Alternative themes

📱 Improved responsive layouts

⚙️ Additional game customization

🌐 Multi-device multiplayer (Bluetooth / Hotspot)

🌐 Online multiplayer

## 📜 License
This project is currently a personal project.

License information will be added in a future version.

## 🌐 Language
🇬🇧 English

🇮🇷 فارسی

#### Ktis — Draw a card. Trust your luck. 🍀🎴
