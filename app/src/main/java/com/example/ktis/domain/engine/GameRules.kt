package com.example.ktis.domain.engine

import com.example.ktis.domain.model.Card

object GameRules {

    fun compareCards(first: Card, second: Card): Int {
        return first.rank.value.compareTo(second.rank.value)
    }

    fun highestCards(
        cards: List<Pair<Int, Card>>
    ): List<Pair<Int, Card>> {
        if (cards.isEmpty()) return emptyList()

        val highestRank = cards.maxOf {
            it.second.rank.value
        }

        return cards.filter {
            it.second.rank.value == highestRank
        }
    }

    fun highestPlayers(
        cards: List<Pair<Int, Card>>
    ): List<Int> {
        return highestCards(cards).map { it.first }.distinct()
    }
}