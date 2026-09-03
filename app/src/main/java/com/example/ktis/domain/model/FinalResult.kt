package com.example.ktis.domain.model

data class FinalResult(
    val winnerId: Int,
    val winnerName: String,
    val scores: Map<Int, Int>,
    val isTieBroken: Boolean
)