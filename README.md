# 🎴 Ktis — Blind Card Game

🇬🇧 English | [🇮🇷 فارسی](README.fa.md)

A local multiplayer Android card game based entirely on luck.

In each turn, players blindly draw a card from their own pile and place it face-down in the center. Once everyone has played, the cards are revealed and the highest-ranked card wins the round and collects all cards in the center.

> **No strategy. No card selection. Just luck. 🍀**

---

## ✨ Features

- 🎮 Local multiplayer gameplay
- 👥 2–8 players
- 🃏 Standard 52-card deck
- 🃏 Support for multiple decks
- 🔀 Automatic shuffling and dealing
- 🙈 Blind card drawing
- 🏆 Round winner calculation
- ⚔️ Tie-breaking rounds
- 🔁 Consecutive tie handling
- 🏅 Card collection and scoring
- 👑 Final winner calculation
- 🎲 Final tie-break system
- 📨 Request Card system
- 📱 Pass-the-phone multiplayer flow
- 🔄 Restart and new game
- 🎨 Basic Android UI

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

## 📨 Request Card

A player who is at least **2 cards behind** the player with the most remaining cards may request one random card.

The card is randomly taken from one of the players who currently has the highest number of remaining cards.

This mechanic can help a player who is falling behind return to the game.

---

## 🏆 Game Ending

When all playable cards have been exhausted, each player's collected cards become their final score.

The player with the highest score wins.

If the final score is tied, the tied players enter a final random tie-break to determine the winner.

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

**Ktis v1.0.0 — Functional Prototype**

[⬇️ Download APK](../../releases/tag/v1.0.0)

> This release contains the first functional version of the game.

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
    │       │   └── engine/
    │       ├── ui/
    │       │   ├── components/
    │       │   └── screens/
    │       └── MainActivity.kt
    │
    └── test/
        └── java/com/example/ktis/
```
        
## 🚀 Version 1.0.0

Status: Functional Prototype

The main goal of version 1.0.0 is to provide a complete playable implementation of the core game rules.

The gameplay system is functional, while the visual design is intentionally kept simple.

### 🗺️ Roadmap

Future versions may include:

🎨 Custom card artwork

✨ Improved visual design

🎬 Better animations 

🔊 Sound effects and music

🌙 Improved themes

📱 Improved responsive layouts

🃏 More polished card interactions

⚙️ Additional game customization

Online multiplayer and other major features may be considered separately in the future.

### 📜 License

This project is currently a personal project.

License information will be added in a future version.

### 🌐 Language
🇬🇧 English

🇮🇷 فارسی

Ktis — Draw a card. Trust your luck. 🍀🎴


---