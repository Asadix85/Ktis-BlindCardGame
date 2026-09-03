package com.example.ktis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.ktis.domain.engine.GameEngine
import com.example.ktis.domain.engine.GameResult
import com.example.ktis.domain.model.Card
import com.example.ktis.domain.model.FinalResult
import com.example.ktis.ui.screens.GameScreen
import com.example.ktis.ui.screens.MainMenuScreen
import com.example.ktis.ui.screens.PassPhoneScreen
import com.example.ktis.ui.screens.ResultScreen
import com.example.ktis.ui.screens.SetupGameScreen

class MainActivity : ComponentActivity() {

    private val gameEngine = GameEngine()

    private var currentScreen by mutableStateOf(Screen.MENU)

    private var gameState by mutableStateOf(
        gameEngineStateOrNull()
    )

    private var lastPlayedCard by mutableStateOf<Card?>(null)

    private var message by mutableStateOf("")

    private var finalResult by mutableStateOf<FinalResult?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface {
                    AppContent()
                }
            }
        }
    }

    @Composable
    private fun AppContent() {

        when (currentScreen) {

            Screen.MENU -> {

                MainMenuScreen(
                    onStart = {
                        currentScreen =
                            Screen.SETUP
                    }
                )
            }

            Screen.SETUP -> {

                SetupGameScreen(
                    onStartGame = { names ->

                        gameState =
                            gameEngine.startGame(names)

                        lastPlayedCard = null
                        message = ""
                        finalResult = null

                        currentScreen =
                            Screen.PASS_PHONE
                    },
                    onBack = {
                        currentScreen =
                            Screen.MENU
                    }
                )
            }

            Screen.PASS_PHONE -> {

                val state = gameState

                if (state != null) {

                    PassPhoneScreen(
                        playerName =
                            state.currentPlayer.name,

                        onContinue = {

                            currentScreen =
                                Screen.GAME
                        }
                    )
                }
            }

            Screen.GAME -> {

                val state = gameState

                if (state != null) {

                    GameScreen(
                        state = state,

                        lastCardText =
                            lastPlayedCard?.let {
                                "${it.rank.name} ${it.suit.name}"
                            },

                        message = message,

                        onDrawCard = {

                            if (
                                state.currentPlayer
                                    .remainingCards > 0
                            ) {

                                lastPlayedCard =
                                    gameEngine.playCard()

                                gameState =
                                    gameEngine.getState()

                                val newState =
                                    gameEngine.getState()

                                if (
                                    newState.centerPile.size >=
                                    newState.roundPlayerIds.size
                                ) {

                                    val winner =
                                        gameEngine.resolveRound()

                                    if (winner != null) {

                                        val winnerPlayer =
                                            newState.players.first {
                                                it.id == winner
                                            }

                                        message =
                                            "${winnerPlayer.name} برنده این دور شد! 🏆"
                                    }

                                    gameState =
                                        gameEngine.getState()
                                }

                                val updatedState =
                                    gameEngine.getState()

                                if (
                                    updatedState.gameOver
                                ) {

                                    finalResult =
                                        GameResult.calculate(
                                            updatedState
                                        )

                                    currentScreen =
                                        Screen.RESULT

                                } else if (
                                    updatedState.centerPile.isEmpty()
                                ) {

                                    currentScreen =
                                        Screen.PASS_PHONE
                                }
                            }
                        },

                        onRequestCard = {

                            val playerId =
                                gameEngine.getState()
                                    .currentPlayer.id

                            val success =
                                gameEngine.requestCard(
                                    playerId
                                )

                            gameState =
                                gameEngine.getState()

                            message =
                                if (success) {
                                    "یک کارت دریافت شد! 🃏"
                                } else {
                                    "امکان درخواست کارت وجود ندارد."
                                }
                        },

                        onBack = {
                            currentScreen =
                                Screen.MENU
                        }
                    )
                }
            }

            Screen.RESULT -> {

                val result = finalResult

                if (result != null) {

                    ResultScreen(
                        result = result,

                        onNewGame = {

                            currentScreen =
                                Screen.SETUP
                        },

                        onMenu = {

                            currentScreen =
                                Screen.MENU
                        }
                    )
                }
            }
        }
    }

    private fun gameEngineStateOrNull() =
        try {
            gameEngine.getState()
        } catch (_: Exception) {
            null
        }

    private enum class Screen {
        MENU,
        SETUP,
        PASS_PHONE,
        GAME,
        RESULT
    }
}