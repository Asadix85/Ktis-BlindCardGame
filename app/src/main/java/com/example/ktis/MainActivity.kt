package com.example.ktis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.example.ktis.domain.engine.GameEngine
import com.example.ktis.domain.engine.GameResult
import com.example.ktis.domain.model.Card
import com.example.ktis.domain.model.FinalResult
import com.example.ktis.ui.screens.GameScreen
import com.example.ktis.ui.screens.MainMenuScreen
import com.example.ktis.ui.screens.PassPhoneScreen
import com.example.ktis.ui.screens.ResultScreen
import com.example.ktis.ui.screens.SetupGameScreen
import com.example.ktis.ui.theme.KtisTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val gameEngine = GameEngine()

    private var currentScreen by mutableStateOf(Screen.MENU)
    private var gameState by mutableStateOf(gameEngineStateOrNull())
    private var lastPlayedCard by mutableStateOf<Card?>(null)
    private var revealed by mutableStateOf(false)
    private var message by mutableStateOf("")
    private var finalResult by mutableStateOf<FinalResult?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KtisTheme {
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
                        currentScreen = Screen.SETUP
                    }
                )
            }

            Screen.SETUP -> {
                SetupGameScreen(
                    onStartGame = { names ->
                        gameState = gameEngine.startGame(names)
                        lastPlayedCard = null
                        revealed = false
                        message = ""
                        finalResult = null
                        currentScreen = Screen.PASS_PHONE
                    },
                    onBack = {
                        currentScreen = Screen.MENU
                    }
                )
            }

            Screen.PASS_PHONE -> {
                val state = gameState

                if (state != null) {
                    PassPhoneScreen(
                        playerName = state.currentPlayer.name,
                        onContinue = {
                            currentScreen = Screen.GAME
                        }
                    )
                }
            }

            Screen.GAME -> {
                val state = gameState

                if (state != null) {
                    GameScreen(
                        state = state,
                        lastCard = lastPlayedCard,
                        revealed = revealed,
                        message = message,

                        onDrawCard = {
                            if (
                                !revealed &&
                                state.currentPlayer.remainingCards > 0
                            ) {
                                revealed = false
                                message = ""

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
                                    gameState = newState

                                    lifecycleScope.launch {
                                        delay(700)

                                        revealed = true
                                        message = "کارت‌ها رو شدند! 🃏"

                                        delay(1200)

                                        val winner =
                                            gameEngine.resolveRound()

                                        if (winner != null) {
                                            val resolvedState =
                                                gameEngine.getState()

                                            val winnerPlayer =
                                                resolvedState.players.first {
                                                    it.id == winner
                                                }

                                            message =
                                                "${winnerPlayer.name} برنده این دور شد! 🏆"

                                            gameState =
                                                resolvedState

                                            delay(700)
                                        }

                                        val updatedState =
                                            gameEngine.getState()

                                        if (updatedState.gameOver) {
                                            finalResult =
                                                GameResult.calculate(
                                                    updatedState
                                                )

                                            currentScreen =
                                                Screen.RESULT
                                        } else if (
                                            updatedState.centerPile.isEmpty()
                                        ) {
                                            revealed = false
                                            lastPlayedCard = null
                                            currentScreen =
                                                Screen.PASS_PHONE
                                        }
                                    }
                                }
                            }
                        },

                        onRequestCard = {
                            val playerId =
                                gameEngine
                                    .getState()
                                    .currentPlayer
                                    .id

                            val success =
                                gameEngine.requestCard(playerId)

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
                            currentScreen = Screen.MENU
                        }
                    )
                }
            }

            Screen.RESULT -> {
                val result = finalResult

                if (result != null) {
                    val names =
                        gameState?.players
                            ?.associate { it.id to it.name }
                            ?: emptyMap()

                    ResultScreen(
                        result = result,
                        playerNames = names,
                        onNewGame = {
                            currentScreen = Screen.SETUP
                        },
                        onMenu = {
                            currentScreen = Screen.MENU
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