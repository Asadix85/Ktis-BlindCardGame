package com.example.ktis.domain.engine

import com.example.ktis.domain.model.Card
import com.example.ktis.domain.model.Deck
import com.example.ktis.domain.model.GameState
import com.example.ktis.domain.model.Player
import com.example.ktis.domain.model.PlayedCard

class GameEngine {

    private var state: GameState? = null

    fun startGame(
        playerNames: List<String>,
        deckCount: Int = recommendedDeckCount(playerNames.size)
    ): GameState {

        require(playerNames.size >= 2)
        require(deckCount >= 1)

        val names = playerNames.map { it.trim() }

        require(names.none { it.isEmpty() })
        require(names.distinct().size == names.size)

        val deck = Deck(deckCount)
        deck.shuffle()

        val players = names.mapIndexed { index, name ->
            Player(
                id = index,
                name = name
            )
        }

        dealCards(deck, players)

        state = GameState(
            players = players,
            currentPlayerIndex = 0,
            roundPlayerIds = players.map { it.id }
        )

        return state!!
    }

    private fun dealCards(
        deck: Deck,
        players: List<Player>
    ) {
        var index = 0

        while (deck.remainingCards() > 0) {
            val card = deck.draw() ?: break

            players[index].drawPile.add(card)
            index = (index + 1) % players.size
        }

        val minimum =
            players.minOf { it.drawPile.size }

        players.forEach { player ->
            while (player.drawPile.size > minimum) {
                player.drawPile.removeAt(player.drawPile.lastIndex)
            }
        }
    }

    fun playCard(): Card {

        val current = requireState()

        check(!current.gameOver) {
            "Game is over."
        }

        val player = current.currentPlayer

        check(player.id in current.roundPlayerIds) {
            "Player is not active in this round."
        }

        check(player.drawPile.isNotEmpty()) {
            "Player has no cards."
        }

        val card =
            player.drawPile.removeAt(player.drawPile.lastIndex)

        current.centerPile.add(
            PlayedCard(
                playerId = player.id,
                card = card
            )
        )

        val playedIds =
            current.centerPile
                .map { it.playerId }
                .toSet()

        val roundComplete =
            current.roundPlayerIds.all {
                it in playedIds
            }

        if (!roundComplete) {
            state = current.copy(
                currentPlayerIndex =
                    nextActivePlayerIndex(current)
            )
        }

        return card
    }

    fun resolveRound(): Int? {

        val current = requireState()

        val activePlayers =
            current.roundPlayerIds.toSet()

        val roundCards =
            current.centerPile.filter {
                it.playerId in activePlayers
            }

        if (roundCards.size < activePlayers.size) {
            return null
        }

        val latestCards =
            activePlayers.mapNotNull { playerId ->
                roundCards
                    .lastOrNull {
                        it.playerId == playerId
                    }
                    ?.let {
                        playerId to it.card
                    }
            }

        if (latestCards.size != activePlayers.size) {
            return null
        }

        val highest =
            GameRules.highestPlayers(latestCards)

        if (highest.size > 1) {

            val firstTiedIndex =
                current.players.indexOfFirst {
                    it.id == highest.first()
                }

            state = current.copy(
                tiedPlayerIds = highest,
                roundPlayerIds = highest,
                currentPlayerIndex = firstTiedIndex
            )

            return null
        }

        val winnerId = highest.first()

        val winner =
            current.players.first {
                it.id == winnerId
            }

        winner.collectedCards.addAll(
            current.centerPile.map { it.card }
        )

        current.centerPile.clear()

        val gameOver =
            current.players.all {
                it.drawPile.isEmpty()
            }

        state = current.copy(
            currentPlayerIndex =
                if (gameOver) {
                    current.currentPlayerIndex
                } else {
                    nextPlayerAfter(
                        current,
                        winnerId
                    )
                },
            roundNumber = current.roundNumber + 1,
            tiedPlayerIds = emptyList(),
            roundPlayerIds =
                current.players.map { it.id },
            gameOver = gameOver
        )

        return winnerId
    }

    fun canRequestCard(
        playerId: Int
    ): Boolean {

        val current = state ?: return false

        if (current.gameOver) return false

        if (current.currentPlayer.id != playerId) {
            return false
        }

        if (playerId !in current.roundPlayerIds) {
            return false
        }

        val requester =
            current.players.first {
                it.id == playerId
            }

        val maximum =
            current.players.maxOf {
                it.drawPile.size
            }

        return maximum - requester.drawPile.size >= 2
    }

    fun requestCard(
        playerId: Int
    ): Boolean {

        if (!canRequestCard(playerId)) {
            return false
        }

        val current = requireState()

        val requester =
            current.players.first {
                it.id == playerId
            }

        val maximum =
            current.players.maxOf {
                it.drawPile.size
            }

        val donors =
            current.players.filter {
                it.id != playerId &&
                        it.drawPile.size == maximum
            }

        if (donors.isEmpty()) {
            return false
        }

        val donor = donors.random()

        val card =
            donor.drawPile.removeAt(
                donor.drawPile.lastIndex
            )

        requester.drawPile.add(card)

        return true
    }

    fun getState(): GameState {
        return requireState()
    }

    private fun nextActivePlayerIndex(
        current: GameState
    ): Int {

        var index =
            (current.currentPlayerIndex + 1) %
                    current.players.size

        while (
            current.players[index].id
            !in current.roundPlayerIds
        ) {
            index =
                (index + 1) %
                        current.players.size
        }

        return index
    }

    private fun nextPlayerAfter(
        current: GameState,
        playerId: Int
    ): Int {

        val winnerIndex =
            current.players.indexOfFirst {
                it.id == playerId
            }

        var index =
            (winnerIndex + 1) %
                    current.players.size

        repeat(current.players.size) {

            if (
                current.players[index]
                    .drawPile
                    .isNotEmpty()
            ) {
                return index
            }

            index =
                (index + 1) %
                        current.players.size
        }

        return winnerIndex
    }

    private fun requireState(): GameState {
        return state
            ?: error("Game has not started.")
    }

    companion object {

        fun recommendedDeckCount(
            playerCount: Int
        ): Int {

            require(playerCount >= 2)

            return when (playerCount) {
                in 2..4 -> 1
                in 5..8 -> 2
                else -> 3
            }
        }
    }
}