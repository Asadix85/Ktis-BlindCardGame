package com.example.ktis.domain.save

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class SaveManager(
    context: Context
) {
    private val saveFile =
        java.io.File(context.filesDir, SAVE_FILE_NAME)

    fun save(saveData: GameSaveData) {
        val json = JSONObject()

        json.put("version", SAVE_VERSION)
        json.put(
            "currentPlayerIndex",
            saveData.currentPlayerIndex
        )
        json.put(
            "roundNumber",
            saveData.roundNumber
        )
        json.put(
            "gameOver",
            saveData.gameOver
        )

        json.put(
            "tiedPlayerIds",
            JSONArray(saveData.tiedPlayerIds)
        )

        json.put(
            "roundPlayerIds",
            JSONArray(saveData.roundPlayerIds)
        )

        json.put(
            "roundPlayedPlayerIds",
            JSONArray(saveData.roundPlayedPlayerIds)
        )

        json.put(
            "players",
            playersToJson(saveData.players)
        )

        json.put(
            "centerPile",
            playedCardsToJson(saveData.centerPile)
        )

        json.put(
            "balanceDeck",
            cardsToJson(saveData.balanceDeck)
        )

        json.put(
            "playerStats",
            playerStatsToJson(saveData.playerStats)
        )

        saveFile.writeText(
            json.toString(),
            Charsets.UTF_8
        )
    }

    fun load(): GameSaveData? {
        if (!saveFile.exists()) {
            return null
        }

        return try {
            val json =
                JSONObject(
                    saveFile.readText(
                        Charsets.UTF_8
                    )
                )

            val version =
                json.optInt(
                    "version",
                    SAVE_VERSION
                )

            if (version != SAVE_VERSION) {
                delete()
                return null
            }

            GameSaveData(
                players =
                    playersFromJson(
                        json.getJSONArray("players")
                    ),

                currentPlayerIndex =
                    json.getInt(
                        "currentPlayerIndex"
                    ),

                centerPile =
                    playedCardsFromJson(
                        json.getJSONArray("centerPile")
                    ),

                balanceDeck =
                    cardsFromJson(
                        json.getJSONArray("balanceDeck")
                    ),

                roundNumber =
                    json.getInt("roundNumber"),

                gameOver =
                    json.getBoolean("gameOver"),

                tiedPlayerIds =
                    intListFromJson(
                        json.getJSONArray(
                            "tiedPlayerIds"
                        )
                    ),

                roundPlayerIds =
                    intListFromJson(
                        json.getJSONArray(
                            "roundPlayerIds"
                        )
                    ),

                roundPlayedPlayerIds =
                    intListFromJson(
                        json.getJSONArray(
                            "roundPlayedPlayerIds"
                        )
                    ),

                playerStats =
                    playerStatsFromJson(
                        json.optJSONArray(
                            "playerStats"
                        )
                    )
            )
        } catch (_: Exception) {
            delete()
            null
        }
    }

    fun hasSavedGame(): Boolean {
        return saveFile.exists() &&
                saveFile.length() > 0
    }

    fun delete() {
        if (saveFile.exists()) {
            saveFile.delete()
        }
    }

    private fun playersToJson(
        players: List<PlayerSaveData>
    ): JSONArray {

        val array = JSONArray()

        players.forEach { player ->

            val json = JSONObject()

            json.put("id", player.id)
            json.put("name", player.name)
            json.put("seat", player.seat)

            json.put(
                "drawPile",
                cardsToJson(player.drawPile)
            )

            json.put(
                "collectedCards",
                cardsToJson(
                    player.collectedCards
                )
            )

            array.put(json)
        }

        return array
    }

    private fun playersFromJson(
        array: JSONArray
    ): List<PlayerSaveData> {

        val players =
            mutableListOf<PlayerSaveData>()

        for (index in 0 until array.length()) {

            val json =
                array.getJSONObject(index)

            players.add(
                PlayerSaveData(
                    id =
                        json.getInt("id"),

                    name =
                        json.getString("name"),

                    seat =
                        json.getInt("seat"),

                    drawPile =
                        cardsFromJson(
                            json.getJSONArray(
                                "drawPile"
                            )
                        ),

                    collectedCards =
                        cardsFromJson(
                            json.getJSONArray(
                                "collectedCards"
                            )
                        )
                )
            )
        }

        return players
    }

    private fun playedCardsToJson(
        cards: List<PlayedCardSaveData>
    ): JSONArray {

        val array = JSONArray()

        cards.forEach { playedCard ->

            val json = JSONObject()

            json.put(
                "playerId",
                playedCard.playerId
            )

            json.put(
                "card",
                cardToJson(
                    playedCard.card
                )
            )

            array.put(json)
        }

        return array
    }

    private fun playedCardsFromJson(
        array: JSONArray
    ): List<PlayedCardSaveData> {

        val cards =
            mutableListOf<PlayedCardSaveData>()

        for (index in 0 until array.length()) {

            val json =
                array.getJSONObject(index)

            cards.add(
                PlayedCardSaveData(
                    playerId =
                        json.getInt(
                            "playerId"
                        ),

                    card =
                        cardFromJson(
                            json.getJSONObject(
                                "card"
                            )
                        )
                )
            )
        }

        return cards
    }

    private fun cardsToJson(
        cards: List<CardSaveData>
    ): JSONArray {

        val array = JSONArray()

        cards.forEach { card ->
            array.put(
                cardToJson(card)
            )
        }

        return array
    }

    private fun cardsFromJson(
        array: JSONArray
    ): List<CardSaveData> {

        val cards =
            mutableListOf<CardSaveData>()

        for (index in 0 until array.length()) {

            cards.add(
                cardFromJson(
                    array.getJSONObject(index)
                )
            )
        }

        return cards
    }

    private fun cardToJson(
        card: CardSaveData
    ): JSONObject {

        return JSONObject().apply {
            put("suit", card.suit)
            put("rank", card.rank)
        }
    }

    private fun cardFromJson(
        json: JSONObject
    ): CardSaveData {

        return CardSaveData(
            suit =
                json.getString("suit"),

            rank =
                json.getString("rank")
        )
    }

    private fun intListFromJson(
        array: JSONArray
    ): List<Int> {

        val result =
            mutableListOf<Int>()

        for (index in 0 until array.length()) {

            result.add(
                array.getInt(index)
            )
        }

        return result
    }

    private fun playerStatsToJson(
        stats: List<PlayerStatsSaveData>
    ): JSONArray {

        val array = JSONArray()

        stats.forEach { stat ->

            val json = JSONObject()

            json.put(
                "playerId",
                stat.playerId
            )

            json.put(
                "roundWins",
                stat.roundWins
            )

            json.put(
                "tieCount",
                stat.tieCount
            )

            array.put(json)
        }

        return array
    }

    private fun playerStatsFromJson(
        array: JSONArray?
    ): List<PlayerStatsSaveData> {

        if (array == null) {
            return emptyList()
        }

        val stats =
            mutableListOf<PlayerStatsSaveData>()

        for (index in 0 until array.length()) {

            val json =
                array.getJSONObject(index)

            stats.add(
                PlayerStatsSaveData(
                    playerId =
                        json.getInt(
                            "playerId"
                        ),

                    roundWins =
                        json.optInt(
                            "roundWins",
                            0
                        ),

                    tieCount =
                        json.optInt(
                            "tieCount",
                            0
                        )
                )
            )
        }

        return stats
    }

    companion object {

        private const val SAVE_FILE_NAME =
            "ktis_save.json"

        private const val SAVE_VERSION =
            1
    }
}