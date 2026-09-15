package com.example.ktis.domain.engine

import com.example.ktis.domain.model.Card
import com.example.ktis.domain.model.Deck
import com.example.ktis.domain.model.GameState
import com.example.ktis.domain.model.Player
import com.example.ktis.domain.model.PlayerStats
import com.example.ktis.domain.model.PlayedCard
import com.example.ktis.domain.model.Rank
import com.example.ktis.domain.model.Suit
import com.example.ktis.domain.save.CardSaveData
import com.example.ktis.domain.save.GameSaveData
import com.example.ktis.domain.save.PlayedCardSaveData
import com.example.ktis.domain.save.PlayerSaveData
import com.example.ktis.domain.save.PlayerStatsSaveData


/*
 * ============================================================
 * تبدیل بین GameState و GameSaveData
 * ============================================================
 *
 * این کلاس مسئول ذخیره و بازیابی وضعیت بازی است.
 * GameEngine فقط صداش می‌زنه.
 */
object GameSaveMapper {

    fun toSaveData(
        state: GameState,
        balanceDeck: Deck
    ): GameSaveData {

        return GameSaveData(

            players =
                state.players.map { player ->

                    PlayerSaveData(
                        id = player.id,
                        name = player.name,
                        seat = player.seat,
                        isAI = player.isAI,

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
                state.currentPlayerIndex,

            centerPile =
                state.centerPile.map {

                    PlayedCardSaveData(
                        playerId = it.playerId,
                        card = it.card.toSaveData()
                    )
                },

            balanceDeck =
                balanceDeck.getCards().map {
                    it.toSaveData()
                },

            roundNumber =
                state.roundNumber,

            gameOver =
                state.gameOver,

            tiedPlayerIds =
                state.tiedPlayerIds,

            roundPlayerIds =
                state.roundPlayerIds,

            roundPlayedPlayerIds =
                state.roundPlayedPlayerIds,

            playerStats =
                state.playerStats.map {
                        (playerId, stats) ->

                    PlayerStatsSaveData(
                        playerId = playerId,
                        roundWins = stats.roundWins,
                        tieCount = stats.tieCount
                    )
                }
        )
    }

    fun fromSaveData(
        saveData: GameSaveData
    ): Pair<GameState, Deck> {

        require(saveData.players.size >= 2) {
            "A saved game must have at least 2 players."
        }

        require(saveData.players.size <= 8) {
            "A saved game cannot have more than 8 players."
        }

        val players =
            saveData.players.map { savedPlayer ->

                Player(
                    id = savedPlayer.id,
                    name = savedPlayer.name,
                    seat = savedPlayer.seat,
                    isAI = savedPlayer.isAI,

                    drawPile =
                        savedPlayer.drawPile
                            .map { it.toCard() }
                            .toMutableList(),

                    collectedCards =
                        savedPlayer.collectedCards
                            .map { it.toCard() }
                            .toMutableList()
                )
            }

        val centerPile =
            saveData.centerPile
                .map {
                    PlayedCard(
                        playerId = it.playerId,
                        card = it.card.toCard()
                    )
                }
                .toMutableList()

        val balanceDeck =
            Deck(1).also { deck ->
                deck.replaceCards(
                    saveData.balanceDeck.map {
                        it.toCard()
                    }
                )
            }

        val loadedStats =
            saveData.playerStats.associate {
                it.playerId to
                        PlayerStats(
                            roundWins = it.roundWins,
                            tieCount = it.tieCount
                        )
            }

        val playerStats =
            players.associate { player ->
                player.id to
                        (loadedStats[player.id] ?: PlayerStats())
            }

        val state =
            GameState(
                players = players,
                currentPlayerIndex = saveData.currentPlayerIndex,
                centerPile = centerPile,
                roundNumber = saveData.roundNumber,
                gameOver = saveData.gameOver,
                tiedPlayerIds = saveData.tiedPlayerIds,
                roundPlayerIds = saveData.roundPlayerIds,
                roundPlayedPlayerIds = saveData.roundPlayedPlayerIds,
                playerStats = playerStats
            )

        return state to balanceDeck
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
            ?: error("Invalid saved suit: $suit")

    val rank =
        Rank.entries.firstOrNull {
            it.name == this.rank
        }
            ?: error("Invalid saved rank: $rank")

    return Card(
        suit = suit,
        rank = rank
    )
}