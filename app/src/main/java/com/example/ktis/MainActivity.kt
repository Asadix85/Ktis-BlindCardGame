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
import com.example.ktis.domain.model.PlayedCard
import com.example.ktis.ui.screens.GameScreen
import com.example.ktis.ui.screens.LoadingScreen
import com.example.ktis.ui.screens.MainMenuScreen
import com.example.ktis.ui.screens.ResultScreen
import com.example.ktis.ui.screens.SettingsScreen
import com.example.ktis.ui.screens.SetupGameScreen
import com.example.ktis.ui.screens.TutorialScreen
import com.example.ktis.ui.theme.KtisTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val gameEngine =
        GameEngine()

    private var currentScreen by
    mutableStateOf(Screen.LOADING)

    private var gameState by
    mutableStateOf(
        gameEngineStateOrNull()
    )

    private var message by
    mutableStateOf("")

    private var highlightedWinnerId by
    mutableStateOf<Int?>(null)

    private var visibleCenterPile by
    mutableStateOf<List<PlayedCard>>(
        emptyList()
    )

    private var animateCenterCards by
    mutableStateOf(true)

    private var finalResult by
    mutableStateOf<FinalResult?>(null)

    private var isActionLocked by
    mutableStateOf(false)

    private var soundEnabled by
    mutableStateOf(true)

    private var musicEnabled by
    mutableStateOf(true)

    private var vibrationEnabled by
    mutableStateOf(true)

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContent {
            KtisTheme {
                Surface {
                    AppContent()
                }
            }
        }

        lifecycleScope.launch {
            delay(1500)
            currentScreen = Screen.MENU
        }
    }

    @Composable
    private fun AppContent() {
        when (currentScreen) {
            Screen.LOADING -> {
                LoadingScreen()
            }

            Screen.MENU -> {
                MainMenuScreen(
                    onStart = {
                        currentScreen =
                            Screen.SETUP
                    },
                    onContinue = {},
                    onSettings = {
                        currentScreen =
                            Screen.SETTINGS
                    },
                    onTutorial = {
                        currentScreen =
                            Screen.TUTORIAL
                    }
                )
            }

            Screen.SETTINGS -> {
                SettingsScreen(
                    soundEnabled = soundEnabled,
                    musicEnabled = musicEnabled,
                    vibrationEnabled = vibrationEnabled,
                    onSoundChanged = {
                        soundEnabled = it
                    },
                    onMusicChanged = {
                        musicEnabled = it
                    },
                    onVibrationChanged = {
                        vibrationEnabled = it
                    },
                    onBack = {
                        currentScreen =
                            Screen.MENU
                    }
                )
            }

            Screen.TUTORIAL -> {
                TutorialScreen(
                    onBack = {
                        currentScreen =
                            Screen.MENU
                    }
                )
            }

            Screen.SETUP -> {
                SetupGameScreen(
                    onStartGame = { players ->
                        gameState =
                            gameEngine.startGame(
                                players
                            )

                        message = ""
                        highlightedWinnerId = null
                        visibleCenterPile =
                            emptyList()

                        animateCenterCards = true
                        finalResult = null
                        isActionLocked = false

                        currentScreen =
                            Screen.GAME
                    },
                    onBack = {
                        currentScreen =
                            Screen.MENU
                    }
                )
            }

            Screen.GAME -> {
                val state = gameState

                if (state != null) {
                    val playerId =
                        state.currentPlayer.id

                    GameScreen(
                        state = state,
                        visibleCenterPile =
                            visibleCenterPile,
                        animateCenterCards =
                            animateCenterCards,
                        message = message,
                        highlightedWinnerId =
                            highlightedWinnerId,
                        onDrawCard = {
                            if (isActionLocked) {
                                return@GameScreen
                            }

                            isActionLocked = true

                            if (
                                state.currentPlayer
                                    .remainingCards <= 0
                            ) {
                                isActionLocked = false
                                return@GameScreen
                            }

                            try {
                                gameEngine.playCard()

                                gameState =
                                    gameEngine.getState()

                                visibleCenterPile =
                                    gameEngine
                                        .getState()
                                        .centerPile
                                        .toList()

                                animateCenterCards = true

                                val afterPlay =
                                    gameEngine.getState()

                                message =
                                    "${afterPlay.players.first { it.id == playerId }.name} کارت انداخت! 🃏"

                                if (
                                    !gameEngine.isRoundComplete()
                                ) {
                                    lifecycleScope.launch {
                                        delay(800)
                                        isActionLocked =
                                            false
                                    }

                                    return@GameScreen
                                }

                                lifecycleScope.launch {
                                    try {
                                        delay(900)

                                        animateCenterCards =
                                            false

                                        val winner =
                                            gameEngine.resolveRound()

                                        val resolved =
                                            gameEngine.getState()

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
                                                        it.id == winner
                                                    }

                                            message =
                                                "${winnerPlayer.name} این دست رو برد! 🏆"

                                            delay(1500)

                                            highlightedWinnerId =
                                                null

                                            visibleCenterPile =
                                                emptyList()

                                            animateCenterCards =
                                                true

                                            val updated =
                                                gameEngine.getState()

                                            gameState =
                                                updated

                                            if (
                                                updated.gameOver
                                            ) {
                                                finalResult =
                                                    GameResult.calculate(
                                                        updated
                                                    )

                                                currentScreen =
                                                    Screen.RESULT
                                            } else {
                                                message = ""

                                                currentScreen =
                                                    Screen.GAME

                                                isActionLocked =
                                                    false
                                            }
                                        } else {
                                            val tieState =
                                                gameEngine.getState()

                                            gameState =
                                                tieState

                                            message =
                                                "مساوی! ⚔️ فقط بازیکن‌های مساوی ادامه میدن."

                                            visibleCenterPile =
                                                tieState.centerPile
                                                    .toList()

                                            animateCenterCards =
                                                false

                                            delay(1000)

                                            message = ""

                                            animateCenterCards =
                                                true

                                            currentScreen =
                                                Screen.GAME

                                            isActionLocked =
                                                false
                                        }
                                    } catch (_: Exception) {
                                        isActionLocked =
                                            false
                                    }
                                }
                            } catch (_: Exception) {
                                isActionLocked =
                                    false
                            }
                        },
                        onShuffle = {
                            if (isActionLocked) {
                                return@GameScreen
                            }

                            gameEngine.shuffleBalanceDeck()

                            message =
                                "کارت‌ها بر زده شدند! 🔀"
                        },
                        onBack = {
                            isActionLocked = false
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
                            isActionLocked = false
                            currentScreen =
                                Screen.SETUP
                        },
                        onMenu = {
                            isActionLocked = false
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
        LOADING,
        MENU,
        SETTINGS,
        TUTORIAL,
        SETUP,
        GAME,
        RESULT
    }
}