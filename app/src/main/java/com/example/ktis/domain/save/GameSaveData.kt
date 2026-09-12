package com.example.ktis.domain.save

data class GameSaveData(
    val players: List<PlayerSaveData>,
    val currentPlayerIndex: Int,
    val centerPile: List<PlayedCardSaveData>,
    val balanceDeck: List<CardSaveData>,
    val roundNumber: Int,
    val gameOver: Boolean,
    val tiedPlayerIds: List<Int>,
    val roundPlayerIds: List<Int>,
    val roundPlayedPlayerIds: List<Int>,
    val playerStats: List<PlayerStatsSaveData> = emptyList()
)

data class PlayerSaveData(
    val id: Int,
    val name: String,
    val seat: Int,
    val drawPile: List<CardSaveData>,
    val collectedCards: List<CardSaveData>
)

data class PlayedCardSaveData(
    val playerId: Int,
    val card: CardSaveData
)

data class CardSaveData(
    val suit: String,
    val rank: String
)

data class PlayerStatsSaveData(
    val playerId: Int,
    val roundWins: Int,
    val tieCount: Int
)