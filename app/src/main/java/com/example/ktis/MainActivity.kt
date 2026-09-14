package com.example.ktis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.example.ktis.domain.engine.GameEngine
import com.example.ktis.domain.engine.GameResult
import com.example.ktis.domain.model.FinalResult
import com.example.ktis.domain.model.PlayedCard
import com.example.ktis.domain.save.SaveManager
import com.example.ktis.ui.audio.KtisAudioManager
import com.example.ktis.ui.screens.GameModeScreen
import com.example.ktis.ui.screens.GameScreen
import com.example.ktis.ui.screens.LoadingScreen
import com.example.ktis.ui.screens.LocalGameMenuScreen
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

    private val settingsPreferences by lazy {
        getSharedPreferences(
            SETTINGS_FILE_NAME,
            MODE_PRIVATE
        )
    }

    private lateinit var audioManager: KtisAudioManager

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

        loadSettings()

        audioManager =
            KtisAudioManager(
                applicationContext
            )

        audioManager.setSoundEnabled(
            soundEnabled
        )

        audioManager.setMusicEnabled(
            musicEnabled
        )

        audioManager.setVibrationEnabled(
            vibrationEnabled
        )

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
            delay(2000)

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

    override fun onDestroy() {
        audioManager.release()
        super.onDestroy()
    }

    @Composable
    private fun AppContent() {

        LaunchedEffect(
            currentScreen,
            musicEnabled
        ) {
            if (
                currentScreen == Screen.MENU &&
                musicEnabled
            ) {
                audioManager.startMusic()
            } else {
                audioManager.stopMusic()
            }
        }

        when (currentScreen) {

            Screen.LOADING -> {
                LoadingScreen()
            }

            Screen.MENU -> {
                MainMenuScreen(
                    onStart = {

                        audioManager.playButtonClick()

                        currentScreen =
                            Screen.GAME_MODE
                    },

                    onSettings = {

                        audioManager.playButtonClick()

                        currentScreen =
                            Screen.SETTINGS
                    },

                    onTutorial = {

                        audioManager.playButtonClick()

                        currentScreen =
                            Screen.TUTORIAL
                    }
                )
            }

            Screen.GAME_MODE -> {
                GameModeScreen(
                    onLocalGame = {

                        audioManager.playButtonClick()

                        currentScreen =
                            Screen.LOCAL_GAME
                    },

                    onDeviceGame = {
                        // Reserved for future
                        // Bluetooth / Hotspot multiplayer.
                    },

                    onOnlineGame = {
                        // Reserved for future
                        // Internet multiplayer.
                    },

                    onBack = {

                        audioManager.playButtonClick()

                        currentScreen =
                            Screen.MENU
                    }
                )
            }

            Screen.LOCAL_GAME -> {
                LocalGameMenuScreen(
                    continueEnabled =
                        hasSavedGame,

                    onNewGame = {

                        audioManager.playButtonClick()

                        currentScreen =
                            Screen.SETUP
                    },

                    onContinue = {

                        audioManager.playButtonClick()

                        continueSavedGame()
                    },

                    onBack = {

                        audioManager.playButtonClick()

                        currentScreen =
                            Screen.GAME_MODE
                    }
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

                        audioManager.setSoundEnabled(
                            it
                        )

                        saveSettings()
                    },

                    onMusicChanged = {

                        musicEnabled = it

                        audioManager.setMusicEnabled(
                            it
                        )

                        saveSettings()
                    },

                    onVibrationChanged = {

                        vibrationEnabled = it

                        audioManager.setVibrationEnabled(
                            it
                        )

                        saveSettings()
                    },

                    onBack = {

                        audioManager.playButtonClick()

                        currentScreen =
                            Screen.MENU
                    }
                )
            }

            Screen.TUTORIAL -> {
                TutorialScreen(
                    onBack = {

                        audioManager.playButtonClick()

                        currentScreen =
                            Screen.MENU
                    }
                )
            }

            Screen.SETUP -> {
                SetupGameScreen(
                    onStartGame = { players ->

                        audioManager.playButtonClick()

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

                        audioManager.playButtonClick()

                        currentScreen =
                            Screen.LOCAL_GAME
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

                                audioManager.playCardDraw()

                                gameEngine.playCard()

                                gameState =
                                    gameEngine.getState()

                                visibleCenterPile =
                                    gameEngine
                                        .getState()
                                        .centerPile
                                        .toList()

                                /*
                                 * فقط کارت تازه‌انداخته‌شده
                                 * باید انیمیشن پرتاب داشته باشه.
                                 *
                                 * بعد از ۴۵۰ میلی‌ثانیه که
                                 * انیمیشن پرتاب تموم شد،
                                 * animateCenterCards رو false
                                 * می‌کنیم تا کارت‌های قبلی
                                 * دوباره انیمیت نشن.
                                 */
                                animateCenterCards =
                                    true

                                lifecycleScope.launch {

                                    delay(450)

                                    animateCenterCards =
                                        false
                                }

                                saveCurrentGame()

                                val afterPlay =
                                    gameEngine.getState()

                                message =
                                    "${afterPlay.players.first { it.id == playerId }.name} کارت انداخت! 🃏"

                                audioManager.playCardPlace()

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

                                            audioManager.playRoundWin()

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

                                            audioManager.playTie()

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

                            audioManager.playButtonClick()

                            gameEngine
                                .shuffleBalanceDeck()

                            saveCurrentGame()

                            message =
                                "کارت‌ها بر زده شدند! 🔀"
                        },

                        onBack = {

                            audioManager.playButtonClick()

                            saveCurrentGame()

                            isActionLocked =
                                false

                            currentScreen =
                                Screen.LOCAL_GAME
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

                            audioManager.playButtonClick()

                            isActionLocked =
                                false

                            currentScreen =
                                Screen.SETUP
                        },

                        onMenu = {

                            audioManager.playButtonClick()

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

    private fun loadSettings() {

        soundEnabled =
            settingsPreferences.getBoolean(
                KEY_SOUND_ENABLED,
                true
            )

        musicEnabled =
            settingsPreferences.getBoolean(
                KEY_MUSIC_ENABLED,
                true
            )

        vibrationEnabled =
            settingsPreferences.getBoolean(
                KEY_VIBRATION_ENABLED,
                true
            )
    }

    private fun saveSettings() {

        settingsPreferences
            .edit()
            .putBoolean(
                KEY_SOUND_ENABLED,
                soundEnabled
            )
            .putBoolean(
                KEY_MUSIC_ENABLED,
                musicEnabled
            )
            .putBoolean(
                KEY_VIBRATION_ENABLED,
                vibrationEnabled
            )
            .apply()
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
        GAME_MODE,
        LOCAL_GAME,
        SETTINGS,
        TUTORIAL,
        SETUP,
        GAME,
        RESULT
    }

    companion object {

        private const val SETTINGS_FILE_NAME =
            "ktis_settings"

        private const val KEY_SOUND_ENABLED =
            "sound_enabled"

        private const val KEY_MUSIC_ENABLED =
            "music_enabled"

        private const val KEY_VIBRATION_ENABLED =
            "vibration_enabled"
    }
}