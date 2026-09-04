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

    private val gameEngine =
        GameEngine()

    private var currentScreen by
    mutableStateOf(Screen.MENU)

    private var gameState by
    mutableStateOf(
        gameEngineStateOrNull()
    )

    private var message by
    mutableStateOf("")

    private var highlightedWinnerId by
    mutableStateOf<Int?>(null)

    private var finalResult by
    mutableStateOf<FinalResult?>(null)

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(
            savedInstanceState
        )

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
                        currentScreen =
                            Screen.SETUP
                    }
                )
            }

            Screen.SETUP -> {

                SetupGameScreen(

                    onStartGame = { names ->

                        gameState =
                            gameEngine.startGame(
                                names
                            )

                        message = ""

                        highlightedWinnerId =
                            null

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

                val state =
                    gameState

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

                val state =
                    gameState

                if (state != null) {

                    val playerId =
                        state.currentPlayer.id

                    val canRequest =
                        gameEngine.canRequestCard(
                            playerId
                        )

                    GameScreen(

                        state = state,

                        message = message,

                        canRequestCard =
                            canRequest,

                        highlightedWinnerId =
                            highlightedWinnerId,

                        onDrawCard = {

                            if (
                                highlightedWinnerId != null
                            ) {
                                return@GameScreen
                            }

                            if (
                                state.currentPlayer
                                    .remainingCards <= 0
                            ) {
                                return@GameScreen
                            }

                            gameEngine.playCard()

                            gameState =
                                gameEngine.getState()

                            val afterPlay =
                                gameEngine.getState()

                            message =
                                "${afterPlay.players.first { it.id == playerId }.name} کارت انداخت! 🃏"

                            if (
                                gameEngine
                                    .isRoundComplete()
                            ) {

                                lifecycleScope.launch {

                                    delay(900)

                                    val winner =
                                        gameEngine
                                            .resolveRound()

                                    val resolved =
                                        gameEngine
                                            .getState()

                                    gameState =
                                        resolved

                                    if (
                                        winner != null
                                    ) {

                                        highlightedWinnerId =
                                            winner

                                        val winnerPlayer =
                                            resolved.players
                                                .first {
                                                    it.id ==
                                                            winner
                                                }

                                        message =
                                            "${winnerPlayer.name} این دست رو برد! 🏆"

                                        delay(1500)

                                        highlightedWinnerId =
                                            null

                                        val updated =
                                            gameEngine
                                                .getState()

                                        if (
                                            updated.gameOver
                                        ) {

                                            finalResult =
                                                GameResult.calculate(
                                                    updated
                                                )

                                            gameState =
                                                updated

                                            currentScreen =
                                                Screen.RESULT

                                        } else {

                                            message = ""

                                            currentScreen =
                                                Screen.PASS_PHONE
                                        }

                                    } else {

                                        val tieState =
                                            gameEngine
                                                .getState()

                                        message =
                                            "مساوی! ⚔️ فقط بازیکن‌های مساوی ادامه میدن."

                                        gameState =
                                            tieState

                                        delay(1000)

                                        message = ""

                                        currentScreen =
                                            Screen.PASS_PHONE
                                    }
                                }

                            } else {

                                currentScreen =
                                    Screen.PASS_PHONE
                            }
                        },

                        onRequestCard = {

                            val currentId =
                                gameEngine
                                    .getState()
                                    .currentPlayer
                                    .id

                            val success =
                                gameEngine.requestCard(
                                    currentId
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

                val result =
                    finalResult

                if (result != null) {

                    val names =
                        gameState
                            ?.players
                            ?.associate {
                                it.id to it.name
                            }
                            ?: emptyMap()

                    ResultScreen(

                        result = result,

                        playerNames = names,

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