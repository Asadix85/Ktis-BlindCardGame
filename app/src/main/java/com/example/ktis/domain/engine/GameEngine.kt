package com.example.ktis.domain.engine

import com.example.ktis.domain.model.Card
import com.example.ktis.domain.model.Deck
import com.example.ktis.domain.model.GameState
import com.example.ktis.domain.model.Player
import com.example.ktis.domain.model.PlayedCard
import com.example.ktis.domain.model.PlayerSetup

class GameEngine {

    private var state: GameState? = null

    /*
     * دسته پشتیبان مخفی برای بالانس تعداد کارت‌ها
     */
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

        /*
         * نام بازیکنان را تمیز می‌کنیم
         * و صندلی را کاملاً خودکار تعیین می‌کنیم.
         *
         * seat:
         * 0 = نقطه شروع / پایین صفحه
         * 1 = بازیکن بعدی در جهت ساعت‌گرد
         * 2 = بازیکن بعدی
         * ...
         */
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

        /*
         * دسته اصلی بازی
         */
        val deck =
            Deck(deckCount)

        deck.shuffle()

        /*
         * دسته پشتیبان کاملاً جدا
         */
        balanceDeck =
            Deck(1)

        balanceDeck.shuffle()

        /*
         * ساخت بازیکنان
         */
        val players =
            setups.mapIndexed { index, setup ->

                Player(
                    id = index,
                    name = setup.name,
                    seat = setup.seat
                )
            }

        /*
         * تقسیم کارت‌ها
         */
        dealCards(
            deck = deck,
            players = players
        )

        /*
         * بازیکن اول همیشه شروع می‌کند.
         * seat = 0
         * یعنی نقطه پایین صفحه / دید اولیه
         */
        val startingPlayerIndex = 0

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
     * نسخه ساده برای سازگاری با کدهای قبلی
     */
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

    /*
     * تقسیم مساوی کارت‌ها
     */
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

    /*
     * انداختن کارت
     */
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

        /*
         * برداشتن آخرین کارت دست
         */
        val card =
            player.drawPile.removeAt(
                player.drawPile.lastIndex
            )

        /*
         * قرار دادن کارت روی زمین
         */
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

            /*
             * همه بازیکنان فعال کارت انداختند
             */
            state =
                current.copy(
                    roundPlayedPlayerIds =
                        playedPlayers
                )

        } else {

            /*
             * رفتن به بازیکن بعدی
             */
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

    /*
     * آیا دست کامل شده؟
     */
    fun isRoundComplete(): Boolean {

        val current =
            requireState()

        return current.roundPlayerIds.all {
            it in current.roundPlayedPlayerIds
        }
    }

    /*
     * مشخص کردن برنده دست
     */
    fun resolveRound(): Int? {

        val current =
            requireState()

        val activePlayers =
            current.roundPlayerIds.toSet()

        /*
         * هنوز همه کارت نینداخته‌اند
         */
        if (
            !activePlayers.all {
                it in current.roundPlayedPlayerIds
            }
        ) {
            return null
        }

        /*
         * آخرین کارت هر بازیکن فعال
         */
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

        /*
         * پیدا کردن بالاترین کارت
         */
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
         * برنده مشخص شده
         */
        val winnerId =
            highest.first()

        val winner =
            current.players.first {
                it.id == winnerId
            }

        /*
         * تمام کارت‌های روی زمین
         * به برنده داده می‌شوند
         */
        winner.collectedCards.addAll(
            current.centerPile.map {
                it.card
            }
        )

        /*
         * زمین پاک می‌شود
         */
        current.centerPile.clear()

        /*
         * بالانس تعداد کارت‌ها
         */
        balancePlayers()

        /*
         * اگر هیچ بازیکنی کارت نداشته باشد
         * بازی تمام شده
         */
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

    /*
     * بالانس تعداد کارت بازیکنان
     */
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

        /*
         * پرتکرارترین تعداد کارت
         * به عنوان مقدار هدف انتخاب می‌شود.
         */
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
         * کارت اضافه بازیکن‌ها
         * وارد دسته پشتیبان می‌شود.
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
         * بازیکن‌هایی که کارت کمتری دارند
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

    /*
     * بر زدن دسته پشتیبان
     */
    fun shuffleBalanceDeck() {
        balanceDeck.shuffle()
    }

    fun getBalanceDeckCount(): Int {
        return balanceDeck.remainingCards()
    }

    fun getState(): GameState {
        return requireState()
    }

    /*
     * رفتن به بازیکن فعال بعدی
     *
     * چون seatها به ترتیب 0..n-1 هستند،
     * این ترتیب همان حرکت ساعت‌گرد است.
     */
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

    /*
     * بعد از بردن یک دست،
     * بازیکن بعدی برنده شروع می‌کند.
     */
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