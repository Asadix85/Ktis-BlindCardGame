package com.example.ktis.ui

import com.example.ktis.domain.model.Card
import com.example.ktis.domain.model.GameState

data class GameUiState(
    val gameState: GameState,
    val lastPlayedCard: Card? = null,
    val revealed: Boolean = false,
    val message: String = ""
)