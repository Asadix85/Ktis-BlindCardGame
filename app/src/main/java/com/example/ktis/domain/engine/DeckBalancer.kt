package com.example.ktis.domain.engine

import com.example.ktis.domain.model.Deck
import com.example.ktis.domain.model.Player


/*
 * ============================================================
 * متعادل‌کننده‌ی دسته کارت‌ها
 * ============================================================
 *
 * وقتی یه دست تموم می‌شه، بعضی بازیکن‌ها کارت
 * بیشتری از بقیه دارن. این کلاس کارت‌های اضافه رو
 * از بازیکن‌های پرکارت برمی‌داره و به بازیکن‌های
 * کم‌کارت می‌ده، تا تعداد کارت همه برابر بشه.
 */
class DeckBalancer {

    private var balanceDeck = Deck(1).also {
        it.shuffle()
    }

    fun reset() {
        balanceDeck = Deck(1).also {
            it.shuffle()
        }
    }

    fun shuffle() {
        balanceDeck.shuffle()
    }

    fun remainingCount(): Int {
        return balanceDeck.remainingCards()
    }

    fun getCards(): Deck {
        return balanceDeck
    }

    fun replaceDeck(deck: Deck) {
        balanceDeck = deck
    }

    /*
     * تعادل بین بازیکن‌ها.
     *
     * الگوریتم:
     *  1. تعداد کارت هر بازیکن رو می‌شماریم.
     *  2. تعدادی که بیشترین تکرار رو داره، هدف می‌شه.
     *     (یعنی اگه سه نفر ۵ کارت دارن و یکی ۷ تا،
     *     هدف می‌شه ۵.)
     *  3. کارت‌های اضافه‌ی بازیکن‌های پرکارت رو
     *     به balanceDeck برمی‌گردونیم.
     *  4. به بازیکن‌های کم‌کارت از balanceDeck
     *     کارت می‌دیم.
     */
    fun balance(players: List<Player>) {

        if (players.isEmpty()) return

        val counts =
            players.map { it.drawPile.size }

        val target =
            counts
                .groupingBy { it }
                .eachCount()
                .entries
                .sortedWith(
                    compareByDescending<Map.Entry<Int, Int>> {
                        it.value
                    }.thenBy {
                        it.key
                    }
                )
                .first()
                .key

        players.forEach { player ->

            while (player.drawPile.size > target) {

                val card =
                    player.drawPile.removeAt(
                        player.drawPile.lastIndex
                    )

                balanceDeck.add(card)
            }
        }

        players.forEach { player ->

            while (
                player.drawPile.size < target &&
                balanceDeck.remainingCards() > 0
            ) {

                val card =
                    balanceDeck.draw() ?: break

                player.drawPile.add(card)
            }
        }
    }
}