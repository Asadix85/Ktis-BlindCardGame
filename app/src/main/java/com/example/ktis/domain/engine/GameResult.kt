package com.example.ktis.domain.engine

import com.example.ktis.domain.model.FinalResult
import com.example.ktis.domain.model.GameState
import com.example.ktis.domain.model.Player

object GameResult {

    fun calculate(
        state: GameState
    ): FinalResult {

        check(state.gameOver) {
            "Game is not over."
        }

        val scores =
            state.players.associate {
                it.id to it.score
            }

        val highestScore =
            scores.values.maxOrNull()
                ?: error("No players.")

        val tiedPlayers =
            state.players.filter {
                it.score == highestScore
            }

        /*
         * فقط یک برنده
         */
        if (tiedPlayers.size == 1) {

            val winner =
                tiedPlayers.first()

            return FinalResult(
                winnerId =
                    winner.id,

                winnerName =
                    winner.name,

                scores =
                    scores,

                isTieBroken =
                    false
            )
        }

        /*
         * مساوی در امتیاز نهایی
         *
         * کارت‌های جمع‌شده‌ی بازیکنان مساوی
         * وارد قرعه‌ی نهایی می‌شوند.
         */
        return breakFinalTie(
            tiedPlayers = tiedPlayers,
            scores = scores
        )
    }

    private fun breakFinalTie(
        tiedPlayers: List<Player>,
        scores: Map<Int, Int>
    ): FinalResult {

        /*
         * همه‌ی کارت‌های جمع‌شده‌ی بازیکنان مساوی
         * وارد یک Pool می‌شوند.
         */
        val pool =
            tiedPlayers
                .flatMap {
                    it.collectedCards
                }
                .shuffled()

        /*
         * اگر به هر دلیل کارت جمع‌شده‌ای وجود نداشت،
         * نمی‌توانیم قرعه‌ی کارتی انجام دهیم.
         *
         * در این حالت یک بازیکن به‌صورت قطعی
         * و بدون حلقه‌ی بی‌نهایت انتخاب می‌شود.
         */
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

                isTieBroken =
                    true
            )
        }

        /*
         * چندین دور قرعه انجام می‌دهیم.
         *
         * در هر دور هر بازیکن یک کارت می‌گیرد.
         * اگر یک نفر بالاترین کارت را داشته باشد،
         * برنده مشخص شده است.
         *
         * اگر دوباره مساوی شود،
         * دور بعدی انجام می‌شود.
         */
        var availableCards =
            pool.toMutableList()

        while (availableCards.isNotEmpty()) {

            /*
             * اگر کارت کافی برای همه‌ی بازیکنان
             * باقی نمانده باشد، Pool دوباره ساخته می‌شود.
             */
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

                    isTieBroken =
                        true
                )
            }
        }

        /*
         * حالت بسیار نادر:
         * تمام کارت‌ها بدون تعیین برنده تمام شدند.
         *
         * برای جلوگیری از گیر کردن بازی،
         * یک برنده‌ی قطعی انتخاب می‌کنیم.
         */
        val winner =
            tiedPlayers.first()

        return FinalResult(
            winnerId =
                winner.id,

            winnerName =
                winner.name,

            scores =
                scores,

            isTieBroken =
                true
        )
    }
}