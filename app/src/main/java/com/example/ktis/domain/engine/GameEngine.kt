package com.example.ktis.domain.engine

import com.example.ktis.domain.model.Card
import com.example.ktis.domain.model.Deck
import com.example.ktis.domain.model.GameState
import com.example.ktis.domain.model.Player
import com.example.ktis.domain.model.PlayedCard
import com.example.ktis.domain.model.PlayerSetup

class GameEngine {

    private var state: GameState? = null

    private var balanceDeck = Deck(1)

    fun startGame(
        playersSetup: List<PlayerSetup>,
        deckCount: Int =
            recommendedDeckCount(playersSetup.size)
    ): GameState {

        require(playersSetup.size >= 2)
        require(playersSetup.size <= 8)
        require(deckCount >= 1)

        val setups =
            playersSetup.map {
                PlayerSetup(
                    name = it.name.trim(),
                    seat = it.seat
                )
            }

        require(
            setups.all {
                it.name.isNotEmpty()
            }
        )

        require(
            setups.map { it.name }.distinct().size ==
                    setups.size
        )

        require(
            setups.map { it.seat }.distinct().size ==
                    setups.size
        )

        require(
            setups.all {
                it.seat in 0..7
            }
        )

        /*
         * دسته اصلی بازی
         */
        val deck =
            Deck(deckCount)

        deck.shuffle()

        /*
         * دسته پشتیبان مخفی
         */
        balanceDeck =
            Deck(1)

        balanceDeck.shuffle()

        /*
         * ساخت بازیکنان همراه با صندلی
         */
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

        /*
         * بازیکن شروع‌کننده:
         *
         * بازیکنی که روی صندلی 7
         * (پایین صفحه / دید اولیه)
         * نشسته باشد.
         *
         * اگر نبود، بازیکن اول.
         */
        val startingPlayerIndex =
            players.indexOfFirst {
                it.seat == 7
            }.let {
                if (it >= 0) it else 0
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
                    emptyList()
            )

        return state!!
    }

    /*
     * سازگاری با کدهای قدیمی
     *
     * فعلاً اگر جایی startGame(List<String>)
     * استفاده شده باشد، بازی هم اجرا می‌شود.
     */
    fun startGame(
        playerNames: List<String>
    ): GameState {

        val setups =
            playerNames.mapIndexed { index, name ->

                PlayerSetup(
                    name = name,
                    seat = defaultSeatForPlayer(
                        index,
                        playerNames.size
                    )
                )
            }

        return startGame(setups)
    }

    private fun defaultSeatForPlayer(
        index: Int,
        playerCount: Int
    ): Int {

        return when (playerCount) {

            2 -> {
                if (index == 0) 7 else 3
            }

            3 -> {
                when (index) {
                    0 -> 7
                    1 -> 3
                    else -> 5
                }
            }

            4 -> {
                when (index) {
                    0 -> 7
                    1 -> 1
                    2 -> 3
                    else -> 5
                }
            }

            5 -> {
                when (index) {
                    0 -> 7
                    1 -> 1
                    2 -> 2
                    3 -> 3
                    else -> 5
                }
            }

            6 -> {
                when (index) {
                    0 -> 7
                    1 -> 0
                    2 -> 2
                    3 -> 3
                    4 -> 4
                    else -> 6
                }
            }

            7 -> {
                index
            }

            else -> {
                index
            }
        }
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

        /*
         * مساوی
         */
        if (highest.size > 1) {

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
                        firstTiedIndex
                )

            return null
        }

        /*
         * برنده
         */
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

        /*
         * بالانس کارت‌ها
         */
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
                    gameOver
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

        /*
         * بازیکن‌هایی که بیشتر دارند
         * کارت اضافه را به دسته پشتیبان می‌دهند.
         */
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

        /*
         * بازیکن‌هایی که کمتر دارند
         * از دسته پشتیبان کارت می‌گیرند.
         */
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