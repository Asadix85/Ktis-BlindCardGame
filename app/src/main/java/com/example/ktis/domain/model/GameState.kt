package com.example.ktis.domain.model

data class GameState(
    val players: List<Player>,
    val currentPlayerIndex: Int = 0,
    val centerPile: MutableList<PlayedCard> = mutableListOf(),
    val bank: MutableList<Card> = mutableListOf(),
    val roundNumber: Int = 1,
    val gameOver: Boolean = false,
    val tiedPlayerIds: List<Int> = emptyList(),
    val roundPlayerIds: List<Int> = emptyList(),
    val roundPlayedPlayerIds: List<Int> = emptyList(),
    val playerStats: Map<Int, PlayerStats> = emptyMap()
) {
    val currentPlayer: Player
        get() = players[currentPlayerIndex]

    val totalCollectedCards: Int
        get() = players.sumOf { it.score }
}