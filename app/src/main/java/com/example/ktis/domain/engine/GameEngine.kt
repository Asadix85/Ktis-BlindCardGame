package com.example.ktis.domain.engine

import com.example.ktis.domain.model.Card
import com.example.ktis.domain.model.Deck
import com.example.ktis.domain.model.GameState
import com.example.ktis.domain.model.Player
import com.example.ktis.domain.model.PlayerSetup
import com.example.ktis.domain.model.PlayerStats
import com.example.ktis.domain.model.PlayedCard
import com.example.ktis.domain.save.GameSaveData


class GameEngine {

    private var state: GameState? = null

    private val deckBalancer =
        DeckBalancer()

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

        deckBalancer.reset()

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

        val initialStats =
            players.associate { player ->
                player.id to PlayerStats()
            }

        state =
            GameState(
                players = players,
                currentPlayerIndex = 0,
                roundPlayerIds =
                    players.map { it.id },
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
        if (players.isEmpty()) return

        val cardsPerPlayer =
            deck.remainingCards() / players.size

        repeat(cardsPerPlayer) {

            players.forEach { player ->

                val card =
                    deck.draw() ?: return

                player.drawPile.add(card)
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

        check(player.id !in current.roundPlayedPlayerIds) {
            "Player already played this round."
        }

        check(player.drawPile.isNotEmpty()) {
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
            current.roundPlayedPlayerIds + player.id

        val roundComplete =
            current.roundPlayerIds.all {
                it in playedPlayers
            }

        state =
            if (roundComplete) {
                current.copy(
                    roundPlayedPlayerIds = playedPlayers
                )
            } else {
                current.copy(
                    currentPlayerIndex =
                        nextActivePlayerIndex(current),
                    roundPlayedPlayerIds = playedPlayers
                )
            }

        return card
    }

    fun isRoundComplete(): Boolean {

        val current = requireState()

        return current.roundPlayerIds.all {
            it in current.roundPlayedPlayerIds
        }
    }

    fun resolveRound(): Int? {

        val current = requireState()

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

        if (latestCards.size != activePlayers.size) {
            return null
        }

        val highest =
            GameRules.highestPlayers(latestCards)

        if (highest.size > 1) {

            /*
             * چک کن که آیا همه‌ی بازیکن‌های مساوی
             * دیگه کارتی برای بازی کردن ندارن.
             *
             * اگه همه خالی باشن، دیگه نمی‌تونیم
             * tie رو ادامه بدیم. بازی تموم می‌شه و
             * امتیاز کارت‌های روی زمین توی
             * GameResult بینشون تقسیم می‌شه.
             */
            val allTiedPlayersAreEmpty =
                highest.all { playerId ->
                    current.players
                        .first { it.id == playerId }
                        .drawPile
                        .isEmpty()
                }

            if (allTiedPlayersAreEmpty) {

                /*
                 * بازی رو تموم کن.
                 *
                 * tiedPlayerIds و centerPile رو
                 * دست‌نخورده نگه می‌داریم تا
                 * GameResult بتونه ازشون استفاده کنه.
                 */
                state =
                    current.copy(
                        gameOver = true,
                        roundNumber =
                            current.roundNumber + 1
                    )

                return null
            }

            /*
             * وگرنه، رفتار قبلی: tie ادامه پیدا می‌کنه.
             */
            return handleTie(current, highest)
        }

        return handleWinner(
            current = current,
            winnerId = highest.first()
        )
    }

    private fun handleTie(
        current: GameState,
        tiedPlayerIds: List<Int>
    ): Int? {

        val updatedStats =
            current.playerStats.toMutableMap()

        tiedPlayerIds.forEach { playerId ->

            val oldStats =
                updatedStats[playerId] ?: PlayerStats()

            updatedStats[playerId] =
                oldStats.copy(
                    tieCount = oldStats.tieCount + 1
                )
        }

        val firstTiedIndex =
            current.players.indexOfFirst {
                it.id == tiedPlayerIds.first()
            }

        state =
            current.copy(
                tiedPlayerIds = tiedPlayerIds,
                roundPlayerIds = tiedPlayerIds,
                roundPlayedPlayerIds = emptyList(),
                currentPlayerIndex = firstTiedIndex,
                playerStats = updatedStats
            )

        return null
    }

    private fun handleWinner(
        current: GameState,
        winnerId: Int
    ): Int {

        val winner =
            current.players.first {
                it.id == winnerId
            }

        winner.collectedCards.addAll(
            current.centerPile.map { it.card }
        )

        current.centerPile.clear()

        val updatedStats =
            current.playerStats.toMutableMap()

        val oldWinnerStats =
            updatedStats[winnerId] ?: PlayerStats()

        updatedStats[winnerId] =
            oldWinnerStats.copy(
                roundWins = oldWinnerStats.roundWins + 1
            )

        deckBalancer.balance(current.players)

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
                        nextPlayerAfter(current, winnerId)
                    },
                roundNumber = current.roundNumber + 1,
                tiedPlayerIds = emptyList(),
                roundPlayerIds =
                    current.players.map { it.id },
                roundPlayedPlayerIds = emptyList(),
                gameOver = gameOver,
                playerStats = updatedStats
            )

        return winnerId
    }

    fun shuffleBalanceDeck() {
        deckBalancer.shuffle()
    }

    fun getBalanceDeckCount(): Int {
        return deckBalancer.remainingCount()
    }

    fun getState(): GameState {
        return requireState()
    }

    fun createSaveData(): GameSaveData {

        return GameSaveMapper.toSaveData(
            state = requireState(),
            balanceDeck = deckBalancer.getCards()
        )
    }

    fun restoreFromSave(
        saveData: GameSaveData
    ): GameState {

        val (restoredState, restoredDeck) =
            GameSaveMapper.fromSaveData(saveData)

        deckBalancer.replaceDeck(restoredDeck)

        state = restoredState

        return restoredState
    }

    private fun nextActivePlayerIndex(
        current: GameState
    ): Int {

        var index =
            (current.currentPlayerIndex + 1) %
                    current.players.size

        while (
            current.players[index].id !in
            current.roundPlayerIds
        ) {
            index =
                (index + 1) % current.players.size
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
            (winnerIndex + 1) % current.players.size

        repeat(current.players.size) {

            if (
                current.players[index]
                    .drawPile
                    .isNotEmpty()
            ) {
                return index
            }

            index =
                (index + 1) % current.players.size
        }

        return winnerIndex
    }

    private fun requireState(): GameState {
        return state
            ?: error("Game has not been started.")
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