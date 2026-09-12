package com.example.ktis.domain.engine

import com.example.ktis.domain.model.Card
import com.example.ktis.domain.model.Deck
import com.example.ktis.domain.model.GameState
import com.example.ktis.domain.model.Player
import com.example.ktis.domain.model.PlayerSetup
import com.example.ktis.domain.model.PlayerStats
import com.example.ktis.domain.model.PlayedCard
import com.example.ktis.domain.model.Rank
import com.example.ktis.domain.model.Suit
import com.example.ktis.domain.save.CardSaveData
import com.example.ktis.domain.save.GameSaveData
import com.example.ktis.domain.save.PlayedCardSaveData
import com.example.ktis.domain.save.PlayerSaveData
import com.example.ktis.domain.save.PlayerStatsSaveData

class GameEngine {

    private var state: GameState? = null

    private var balanceDeck = Deck(1)

    fun startGame(
        playersSetup: List<PlayerSetup>,
        deckCount: Int =
            recommendedDeckCount(playersSetup.size)
    ): GameState {

        require(playersSetup.size >= 2) {
            "At least 2 players are required."
        }

        require(playersSetup.size <= 8) {
            "Maximum 8 players are allowed."
        }

        require(deckCount >= 1) {
            "Deck count must be at least 1."
        }

        val setups =
            playersSetup.mapIndexed { index, player ->
                PlayerSetup(
                    name = player.name.trim(),
                    seat = index
                )
            }

        require(
            setups.all {
                it.name.isNotEmpty()
            }
        ) {
            "Player names cannot be empty."
        }

        require(
            setups.map { it.name }.distinct().size ==
                    setups.size
        ) {
            "Player names must be unique."
        }

        val deck = Deck(deckCount)
        deck.shuffle()

        balanceDeck = Deck(1)
        balanceDeck.shuffle()

        val players =
            setups.mapIndexed { index, setup ->
                Player(
                    id = index,
                    name = setup.name,
                    seat = setup.seat
                )
            }

        dealCards(
            deck = deck,
            players = players
        )

        val startingPlayerIndex = 0

        val initialStats =
            players.associate { player ->
                player.id to PlayerStats()
            }

        state =
            GameState(
                players = players,
                currentPlayerIndex =
                    startingPlayerIndex,
                roundPlayerIds =
                    players.map {
                        it.id
                    },
                roundPlayedPlayerIds =
                    emptyList(),
                playerStats =
                    initialStats
            )

        return state!!
    }

    fun startGame(
        playerNames: List<String>
    ): GameState {

        val setups =
            playerNames.map { name ->
                PlayerSetup(
                    name = name.trim(),
                    seat = 0
                )
            }

        return startGame(setups)
    }

    private fun dealCards(
        deck: Deck,
        players: List<Player>
    ) {
        if (players.isEmpty()) {
            return
        }

        val cardsPerPlayer =
            deck.remainingCards() /
                    players.size

        repeat(cardsPerPlayer) {

            players.forEach { player ->

                val card =
                    deck.draw()
                        ?: return

                player.drawPile.add(card)
            }
        }
    }

    fun playCard(): Card {

        val current =
            requireState()

        check(!current.gameOver) {
            "Game is over."
        }

        val player =
            current.currentPlayer

        check(
            player.id in current.roundPlayerIds
        ) {
            "Player is not active in this round."
        }

        check(
            player.id !in
                    current.roundPlayedPlayerIds
        ) {
            "Player already played this round."
        }

        check(
            player.drawPile.isNotEmpty()
        ) {
            "Player has no cards."
        }

        val card =
            player.drawPile.removeAt(
                player.drawPile.lastIndex
            )

        current.centerPile.add(
            PlayedCard(
                playerId = player.id,
                card = card
            )
        )

        val playedPlayers =
            current.roundPlayedPlayerIds +
                    player.id

        val roundComplete =
            current.roundPlayerIds.all {
                it in playedPlayers
            }

        if (roundComplete) {

            state =
                current.copy(
                    roundPlayedPlayerIds =
                        playedPlayers
                )

        } else {

            state =
                current.copy(
                    currentPlayerIndex =
                        nextActivePlayerIndex(
                            current
                        ),
                    roundPlayedPlayerIds =
                        playedPlayers
                )
        }

        return card
    }

    fun isRoundComplete(): Boolean {

        val current =
            requireState()

        return current.roundPlayerIds.all {
            it in current.roundPlayedPlayerIds
        }
    }

    fun resolveRound(): Int? {

        val current =
            requireState()

        val activePlayers =
            current.roundPlayerIds.toSet()

        if (
            !activePlayers.all {
                it in current.roundPlayedPlayerIds
            }
        ) {
            return null
        }

        val latestCards =
            activePlayers.mapNotNull { playerId ->

                current.centerPile
                    .lastOrNull {
                        it.playerId == playerId
                    }
                    ?.let {
                        playerId to it.card
                    }
            }

        if (
            latestCards.size !=
            activePlayers.size
        ) {
            return null
        }

        val highest =
            GameRules.highestPlayers(
                latestCards
            )

        if (highest.size > 1) {

            val updatedStats =
                current.playerStats
                    .toMutableMap()

            highest.forEach { playerId ->

                val oldStats =
                    updatedStats[playerId]
                        ?: PlayerStats()

                updatedStats[playerId] =
                    oldStats.copy(
                        tieCount =
                            oldStats.tieCount + 1
                    )
            }

            val firstTiedIndex =
                current.players.indexOfFirst {
                    it.id == highest.first()
                }

            state =
                current.copy(
                    tiedPlayerIds =
                        highest,

                    roundPlayerIds =
                        highest,

                    roundPlayedPlayerIds =
                        emptyList(),

                    currentPlayerIndex =
                        firstTiedIndex,

                    playerStats =
                        updatedStats
                )

            return null
        }

        val winnerId =
            highest.first()

        val winner =
            current.players.first {
                it.id == winnerId
            }

        winner.collectedCards.addAll(
            current.centerPile.map {
                it.card
            }
        )

        current.centerPile.clear()

        val updatedStats =
            current.playerStats
                .toMutableMap()

        val oldWinnerStats =
            updatedStats[winnerId]
                ?: PlayerStats()

        updatedStats[winnerId] =
            oldWinnerStats.copy(
                roundWins =
                    oldWinnerStats.roundWins + 1
            )

        balancePlayers()

        val gameOver =
            current.players.all {
                it.drawPile.isEmpty()
            }

        state =
            current.copy(
                currentPlayerIndex =
                    if (gameOver) {
                        current.currentPlayerIndex
                    } else {
                        nextPlayerAfter(
                            current,
                            winnerId
                        )
                    },

                roundNumber =
                    current.roundNumber + 1,

                tiedPlayerIds =
                    emptyList(),

                roundPlayerIds =
                    current.players.map {
                        it.id
                    },

                roundPlayedPlayerIds =
                    emptyList(),

                gameOver =
                    gameOver,

                playerStats =
                    updatedStats
            )

        return winnerId
    }

    private fun balancePlayers() {

        val current =
            requireState()

        if (current.players.isEmpty()) {
            return
        }

        val counts =
            current.players.map {
                it.drawPile.size
            }

        val target =
            counts
                .groupingBy {
                    it
                }
                .eachCount()
                .entries
                .sortedWith(
                    compareByDescending<
                            Map.Entry<Int, Int>
                            > {
                        it.value
                    }.thenBy {
                        it.key
                    }
                )
                .first()
                .key

        current.players.forEach { player ->

            while (
                player.drawPile.size > target
            ) {

                val card =
                    player.drawPile.removeAt(
                        player.drawPile.lastIndex
                    )

                balanceDeck.add(card)
            }
        }

        current.players.forEach { player ->

            while (
                player.drawPile.size < target &&
                balanceDeck.remainingCards() > 0
            ) {

                val card =
                    balanceDeck.draw()
                        ?: break

                player.drawPile.add(card)
            }
        }
    }

    fun shuffleBalanceDeck() {
        balanceDeck.shuffle()
    }

    fun getBalanceDeckCount(): Int {
        return balanceDeck.remainingCards()
    }

    fun getState(): GameState {
        return requireState()
    }

    fun createSaveData(): GameSaveData {

        val current =
            requireState()

        return GameSaveData(

            players =
                current.players.map { player ->

                    PlayerSaveData(
                        id = player.id,
                        name = player.name,
                        seat = player.seat,

                        drawPile =
                            player.drawPile.map {
                                it.toSaveData()
                            },

                        collectedCards =
                            player.collectedCards.map {
                                it.toSaveData()
                            }
                    )
                },

            currentPlayerIndex =
                current.currentPlayerIndex,

            centerPile =
                current.centerPile.map {

                    PlayedCardSaveData(
                        playerId =
                            it.playerId,

                        card =
                            it.card.toSaveData()
                    )
                },

            balanceDeck =
                balanceDeck
                    .getCards()
                    .map {
                        it.toSaveData()
                    },

            roundNumber =
                current.roundNumber,

            gameOver =
                current.gameOver,

            tiedPlayerIds =
                current.tiedPlayerIds,

            roundPlayerIds =
                current.roundPlayerIds,

            roundPlayedPlayerIds =
                current.roundPlayedPlayerIds,

            playerStats =
                current.playerStats.map {
                        (playerId, stats) ->

                    PlayerStatsSaveData(
                        playerId =
                            playerId,

                        roundWins =
                            stats.roundWins,

                        tieCount =
                            stats.tieCount
                    )
                }
        )
    }

    fun restoreFromSave(
        saveData: GameSaveData
    ): GameState {

        require(saveData.players.size >= 2) {
            "A saved game must have at least 2 players."
        }

        require(saveData.players.size <= 8) {
            "A saved game cannot have more than 8 players."
        }

        val players =
            saveData.players.map { savedPlayer ->

                Player(
                    id =
                        savedPlayer.id,

                    name =
                        savedPlayer.name,

                    seat =
                        savedPlayer.seat,

                    drawPile =
                        savedPlayer.drawPile
                            .map {
                                it.toCard()
                            }
                            .toMutableList(),

                    collectedCards =
                        savedPlayer.collectedCards
                            .map {
                                it.toCard()
                            }
                            .toMutableList()
                )
            }

        val centerPile =
            saveData.centerPile
                .map {
                    PlayedCard(
                        playerId =
                            it.playerId,

                        card =
                            it.card.toCard()
                    )
                }
                .toMutableList()

        balanceDeck = Deck(1)

        balanceDeck.replaceCards(
            saveData.balanceDeck.map {
                it.toCard()
            }
        )

        val loadedStats =
            saveData.playerStats
                .associate {
                    it.playerId to
                            PlayerStats(
                                roundWins =
                                    it.roundWins,

                                tieCount =
                                    it.tieCount
                            )
                }

        val playerStats =
            players.associate { player ->

                player.id to
                        (
                                loadedStats[player.id]
                                    ?: PlayerStats()
                                )
            }

        state =
            GameState(

                players =
                    players,

                currentPlayerIndex =
                    saveData.currentPlayerIndex,

                centerPile =
                    centerPile,

                roundNumber =
                    saveData.roundNumber,

                gameOver =
                    saveData.gameOver,

                tiedPlayerIds =
                    saveData.tiedPlayerIds,

                roundPlayerIds =
                    saveData.roundPlayerIds,

                roundPlayedPlayerIds =
                    saveData.roundPlayedPlayerIds,

                playerStats =
                    playerStats
            )

        return state!!
    }

    private fun nextActivePlayerIndex(
        current: GameState
    ): Int {

        var index =
            (
                    current.currentPlayerIndex + 1
                    ) % current.players.size

        while (
            current.players[index].id !in
            current.roundPlayerIds
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

        repeat(
            current.players.size
        ) {

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
            ?: error(
                "Game has not been started."
            )
    }

    companion object {

        fun recommendedDeckCount(
            playerCount: Int
        ): Int {

            require(
                playerCount >= 2
            )

            return when (playerCount) {
                in 2..4 -> 1
                in 5..8 -> 2
                else -> 3
            }
        }
    }
}

private fun Card.toSaveData(): CardSaveData {

    return CardSaveData(
        suit = suit.name,
        rank = rank.name
    )
}

private fun CardSaveData.toCard(): Card {

    val suit =
        Suit.entries.firstOrNull {
            it.name == this.suit
        }
            ?: error(
                "Invalid saved suit: $suit"
            )

    val rank =
        Rank.entries.firstOrNull {
            it.name == this.rank
        }
            ?: error(
                "Invalid saved rank: $rank"
            )

    return Card(
        suit = suit,
        rank = rank
    )
}