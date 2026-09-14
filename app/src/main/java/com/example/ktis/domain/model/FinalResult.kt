package com.example.ktis.domain.model

data class FinalResult(
    val winnerId: Int,
    val winnerName: String,
    val scores: Map<Int, Float>,
    val isTieBroken: Boolean,
    val playerStats: Map<Int, PlayerStats> = emptyMap(),
    val sharedCardsApplied: Boolean = false
)