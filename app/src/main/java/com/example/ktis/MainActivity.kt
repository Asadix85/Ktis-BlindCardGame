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
import com.example.ktis.domain.save.SaveManager
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

    private val saveManager by lazy {
        SaveManager(applicationContext)
    }

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

    private var hasSavedGame by
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

        hasSavedGame =
            saveManager.hasSavedGame()

        setContent {
            KtisTheme {
                Surface {
                    AppContent()
                }
            }
        }

        lifecycleScope.launch {
            delay(1500)

            hasSavedGame =
                saveManager.hasSavedGame()

            currentScreen =
                Screen.MENU
        }
    }

    override fun onStop() {
        super.onStop()

        if (
            currentScreen == Screen.GAME &&
            gameState != null
        ) {
            saveCurrentGame()
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

                    onContinue = {
                        continueSavedGame()
                    },

                    onSettings = {
                        currentScreen =
                            Screen.SETTINGS
                    },

                    onTutorial = {
                        currentScreen =
                            Screen.TUTORIAL
                    },

                    continueEnabled =
                        hasSavedGame
                )
            }

            Screen.SETTINGS -> {
                SettingsScreen(
                    soundEnabled =
                        soundEnabled,

                    musicEnabled =
                        musicEnabled,

                    vibrationEnabled =
                        vibrationEnabled,

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

                        highlightedWinnerId =
                            null

                        visibleCenterPile =
                            emptyList()

                        animateCenterCards =
                            true

                        finalResult =
                            null

                        isActionLocked =
                            false

                        saveCurrentGame()

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

                val state =
                    gameState

                if (state != null) {

                    val playerId =
                        state.currentPlayer.id

                    GameScreen(
                        state = state,

                        visibleCenterPile =
                            visibleCenterPile,

                        animateCenterCards =
                            animateCenterCards,

                        message =
                            message,

                        highlightedWinnerId =
                            highlightedWinnerId,

                        onDrawCard = {

                            if (isActionLocked) {
                                return@GameScreen
                            }

                            isActionLocked =
                                true

                            if (
                                state.currentPlayer
                                    .remainingCards <= 0
                            ) {
                                isActionLocked =
                                    false

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

                                animateCenterCards =
                                    true

                                saveCurrentGame()

                                val afterPlay =
                                    gameEngine.getState()

                                message =
                                    "${afterPlay.players.first { it.id == playerId }.name} کارت انداخت! 🃏"

                                if (
                                    !gameEngine
                                        .isRoundComplete()
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
                                            gameEngine
                                                .resolveRound()

                                        val resolved =
                                            gameEngine
                                                .getState()

                                        gameState =
                                            resolved

                                        saveCurrentGame()

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
                                                gameEngine
                                                    .getState()

                                            gameState =
                                                updated

                                            if (
                                                updated.gameOver
                                            ) {

                                                finalResult =
                                                    GameResult.calculate(
                                                        updated
                                                    )

                                                saveManager.delete()

                                                hasSavedGame =
                                                    false

                                                currentScreen =
                                                    Screen.RESULT

                                                isActionLocked =
                                                    false

                                            } else {

                                                message =
                                                    ""

                                                currentScreen =
                                                    Screen.GAME

                                                isActionLocked =
                                                    false
                                            }

                                        } else {

                                            val tieState =
                                                gameEngine
                                                    .getState()

                                            gameState =
                                                tieState

                                            saveCurrentGame()

                                            message =
                                                "مساوی! ⚔️ فقط بازیکن‌های مساوی ادامه میدن."

                                            visibleCenterPile =
                                                tieState
                                                    .centerPile
                                                    .toList()

                                            animateCenterCards =
                                                false

                                            delay(1000)

                                            message =
                                                ""

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

                            gameEngine
                                .shuffleBalanceDeck()

                            saveCurrentGame()

                            message =
                                "کارت‌ها بر زده شدند! 🔀"
                        },

                        onBack = {

                            saveCurrentGame()

                            isActionLocked =
                                false

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

                        playerNames =
                            names,

                        onNewGame = {

                            isActionLocked =
                                false

                            currentScreen =
                                Screen.SETUP
                        },

                        onMenu = {

                            isActionLocked =
                                false

                            currentScreen =
                                Screen.MENU
                        }
                    )
                }
            }
        }
    }

    private fun saveCurrentGame() {

        try {

            if (
                gameEngineStateOrNull() == null
            ) {
                return
            }

            saveManager.save(
                gameEngine.createSaveData()
            )

            hasSavedGame =
                true

        } catch (_: Exception) {
        }
    }

    private fun continueSavedGame() {

        try {

            val saveData =
                saveManager.load()

            if (saveData == null) {

                hasSavedGame =
                    false

                return
            }

            gameState =
                gameEngine.restoreFromSave(
                    saveData
                )

            message = ""

            highlightedWinnerId =
                null

            visibleCenterPile =
                gameState
                    ?.centerPile
                    ?.toList()
                    ?: emptyList()

            animateCenterCards =
                false

            finalResult =
                null

            isActionLocked =
                false

            currentScreen =
                if (
                    gameState?.gameOver == true
                ) {

                    finalResult =
                        GameResult.calculate(
                            gameState!!
                        )

                    saveManager.delete()

                    hasSavedGame =
                        false

                    Screen.RESULT

                } else {

                    hasSavedGame =
                        true

                    Screen.GAME
                }

        } catch (_: Exception) {

            saveManager.delete()

            hasSavedGame =
                false

            gameState =
                null

            message = ""

            highlightedWinnerId =
                null

            visibleCenterPile =
                emptyList()

            finalResult =
                null

            isActionLocked =
                false

            currentScreen =
                Screen.MENU
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