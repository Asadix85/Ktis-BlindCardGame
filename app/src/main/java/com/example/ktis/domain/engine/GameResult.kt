package com.example.ktis.domain.engine

import com.example.ktis.domain.model.FinalResult
import com.example.ktis.domain.model.GameState
import com.example.ktis.domain.model.Player
import com.example.ktis.domain.model.PlayerStats

object GameResult {

    fun calculate(
        state: GameState
    ): FinalResult {

        check(state.gameOver) {
            "Game is not over."
        }

        /*
         * ====================================================
         * امتیاز پایه
         * ====================================================
         *
         * امتیاز هر بازیکن از تعداد کارت‌های جمع‌شده
         * محاسبه می‌شه. از Float استفاده می‌کنیم چون
         * ممکنه توی حالت تقسیم کارت‌های باقی‌مانده،
         * امتیاز اعشاری بشه (مثلاً ۲.۵).
         */
        val scores =
            state.players
                .associate {
                    it.id to it.score.toFloat()
                }
                .toMutableMap()

        var sharedCardsApplied = false

        /*
         * ====================================================
         * تقسیم کارت‌های باقی‌مانده در حالت مساوی پایان بازی
         * ====================================================
         *
         * وقتی توی دور آخر، بازیکن‌های مساوی هیچ کارتی
         * برای بازی کردن نداشتن، بازی با gameOver = true
         * تموم شد و کارت‌های روی زمین توی centerPile
         * باقی موندن.
         *
         * حالا این کارت‌ها رو به صورت مساوی بین
         * بازیکن‌های مساوی تقسیم می‌کنیم (به شکل امتیاز
         * اعشاری، نه کارت فیزیکی).
         */
        if (
            state.tiedPlayerIds.isNotEmpty() &&
            state.centerPile.isNotEmpty()
        ) {

            val sharedCards =
                state.centerPile.size.toFloat() /
                        state.tiedPlayerIds.size

            state.tiedPlayerIds.forEach { playerId ->

                scores[playerId] =
                    (scores[playerId] ?: 0f) + sharedCards
            }

            sharedCardsApplied = true
        }

        val playerStats =
            state.players.associate { player ->

                player.id to
                        (
                                state.playerStats[player.id]
                                    ?: PlayerStats()
                                )
            }

        val highestScore =
            scores.values.maxOrNull()
                ?: error("No players.")

        val tiedPlayerIds =
            scores.filterValues {
                it == highestScore
            }.keys.toList()

        if (tiedPlayerIds.size == 1) {

            val winnerId =
                tiedPlayerIds.first()

            val winner =
                state.players.first {
                    it.id == winnerId
                }

            return FinalResult(
                winnerId =
                    winner.id,

                winnerName =
                    winner.name,

                scores =
                    scores,

                playerStats =
                    playerStats,

                isTieBroken =
                    false,

                sharedCardsApplied =
                    sharedCardsApplied
            )
        }

        /*
         * اگه هنوز بعد از تقسیم، چند نفر امتیاز برابر
         * داشتن، با قرعه‌کشی نهایی برنده رو مشخص می‌کنیم.
         */
        val tiedPlayers =
            state.players.filter {
                it.id in tiedPlayerIds
            }

        return breakFinalTie(
            tiedPlayers = tiedPlayers,
            scores = scores,
            playerStats = playerStats,
            sharedCardsApplied = sharedCardsApplied
        )
    }

    private fun breakFinalTie(
        tiedPlayers: List<Player>,
        scores: Map<Int, Float>,
        playerStats: Map<Int, PlayerStats>,
        sharedCardsApplied: Boolean
    ): FinalResult {

        val pool =
            tiedPlayers
                .flatMap {
                    it.collectedCards
                }
                .shuffled()

        if (pool.isEmpty()) {

            val winner =
                tiedPlayers.first()

            return FinalResult(
                winnerId =
                    winner.id,

                winnerName =
                    winner.name,

                scores =
                    scores,

                playerStats =
                    playerStats,

                isTieBroken =
                    true,

                sharedCardsApplied =
                    sharedCardsApplied
            )
        }

        var availableCards =
            pool.toMutableList()

        while (availableCards.isNotEmpty()) {

            if (
                availableCards.size <
                tiedPlayers.size
            ) {
                availableCards =
                    pool.toMutableList()
            }

            availableCards.shuffle()

            val drawn =
                tiedPlayers.map { player ->

                    val card =
                        availableCards.removeAt(
                            availableCards.lastIndex
                        )

                    player.id to card
                }

            val highestRank =
                drawn.maxOf {
                    it.second.rank.value
                }

            val winners =
                drawn.filter {
                    it.second.rank.value ==
                            highestRank
                }

            if (winners.size == 1) {

                val winnerId =
                    winners.first().first

                val winner =
                    tiedPlayers.first {
                        it.id == winnerId
                    }

                return FinalResult(
                    winnerId =
                        winner.id,

                    winnerName =
                        winner.name,

                    scores =
                        scores,

                    playerStats =
                        playerStats,

                    isTieBroken =
                        true,

                    sharedCardsApplied =
                        sharedCardsApplied
                )
            }
        }

        val winner =
            tiedPlayers.first()

        return FinalResult(
            winnerId =
                winner.id,

            winnerName =
                winner.name,

            scores =
                scores,

            playerStats =
                playerStats,

            isTieBroken =
                true,

            sharedCardsApplied =
                sharedCardsApplied
        )
    }
}