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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.ktis.domain.engine.GameEngine
import com.example.ktis.domain.save.SaveManager
import com.example.ktis.ui.audio.KtisAudioManager
import com.example.ktis.ui.game.GameViewModel
import com.example.ktis.ui.game.GameViewModelFactory
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

    private val gameEngine = GameEngine()

    private lateinit var saveManager: SaveManager

    private lateinit var audioManager: KtisAudioManager

    private lateinit var gameViewModel: GameViewModel

    private val settingsPreferences by lazy {
        getSharedPreferences(
            SETTINGS_FILE_NAME,
            MODE_PRIVATE
        )
    }

    private var currentScreen by
    mutableStateOf(Screen.LOADING)

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

        saveManager = SaveManager(applicationContext)

        audioManager = KtisAudioManager(applicationContext)

        audioManager.setSoundEnabled(soundEnabled)
        audioManager.setMusicEnabled(musicEnabled)
        audioManager.setVibrationEnabled(vibrationEnabled)

        gameViewModel =
            ViewModelProvider(
                this,
                GameViewModelFactory(
                    gameEngine = gameEngine,
                    saveManager = saveManager,
                    audioManager = audioManager
                )
            )[GameViewModel::class.java]

        /*
         * وقتی بازی تموم شد، برو به Result.
         */
        gameViewModel.onGameOver = {
            currentScreen = Screen.RESULT
        }

        setContent {
            KtisTheme {
                Surface {
                    AppContent()
                }
            }
        }

        lifecycleScope.launch {
            delay(2200)
            currentScreen = Screen.MENU
        }
    }

    override fun onStop() {
        super.onStop()

        if (
            currentScreen == Screen.GAME
        ) {
            gameViewModel.saveCurrentGame()
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
                        currentScreen = Screen.GAME_MODE
                    },
                    onSettings = {
                        audioManager.playButtonClick()
                        currentScreen = Screen.SETTINGS
                    },
                    onTutorial = {
                        audioManager.playButtonClick()
                        currentScreen = Screen.TUTORIAL
                    }
                )
            }

            Screen.GAME_MODE -> {
                GameModeScreen(
                    onLocalGame = {
                        audioManager.playButtonClick()
                        currentScreen = Screen.LOCAL_GAME
                    },
                    onDeviceGame = {
                        // Reserved for future
                    },
                    onOnlineGame = {
                        // Reserved for future
                    },
                    onBack = {
                        audioManager.playButtonClick()
                        currentScreen = Screen.MENU
                    }
                )
            }

            Screen.LOCAL_GAME -> {
                LocalGameMenuScreen(
                    continueEnabled = gameViewModel.hasSavedGame,

                    onNewGame = {
                        audioManager.playButtonClick()
                        currentScreen = Screen.SETUP
                    },

                    onContinue = {
                        audioManager.playButtonClick()

                        val wentToGame =
                            gameViewModel.continueSavedGame()

                        currentScreen =
                            if (wentToGame) {
                                Screen.GAME
                            } else {
                                Screen.RESULT
                            }
                    },

                    onBack = {
                        audioManager.playButtonClick()
                        currentScreen = Screen.GAME_MODE
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
                        audioManager.setSoundEnabled(it)
                        saveSettings()
                    },

                    onMusicChanged = {
                        musicEnabled = it
                        audioManager.setMusicEnabled(it)
                        saveSettings()
                    },

                    onVibrationChanged = {
                        vibrationEnabled = it
                        audioManager.setVibrationEnabled(it)
                        saveSettings()
                    },

                    onBack = {
                        audioManager.playButtonClick()
                        currentScreen = Screen.MENU
                    }
                )
            }

            Screen.TUTORIAL -> {
                TutorialScreen(
                    onBack = {
                        audioManager.playButtonClick()
                        currentScreen = Screen.MENU
                    }
                )
            }

            Screen.SETUP -> {
                SetupGameScreen(
                    onStartGame = { players ->
                        audioManager.playButtonClick()

                        gameViewModel.startNewGame(players)

                        currentScreen = Screen.GAME
                    },

                    onBack = {
                        audioManager.playButtonClick()
                        currentScreen = Screen.LOCAL_GAME
                    }
                )
            }

            Screen.GAME -> {

                val state = gameViewModel.gameState

                if (state != null) {

                    GameScreen(
                        state = state,

                        visibleCenterPile =
                            gameViewModel.visibleCenterPile,

                        animateCenterCards =
                            gameViewModel.animateCenterCards,

                        message =
                            gameViewModel.message,

                        highlightedWinnerId =
                            gameViewModel.highlightedWinnerId,

                        onDrawCard = {
                            gameViewModel.performMove()
                        },

                        onShuffle = {
                            gameViewModel.shuffle()
                        },

                        onBack = {
                            audioManager.playButtonClick()

                            gameViewModel.onBackToMenu()

                            currentScreen = Screen.LOCAL_GAME
                        }
                    )
                }
            }

            Screen.RESULT -> {

                val result = gameViewModel.finalResult

                if (result != null) {

                    val names =
                        gameViewModel
                            .gameState
                            ?.players
                            ?.associate {
                                it.id to it.name
                            }
                            ?: emptyMap()

                    val isAIMap =
                        gameViewModel
                            .gameState
                            ?.players
                            ?.associate {
                                it.id to it.isAI
                            }
                            ?: emptyMap()

                    ResultScreen(
                        result = result,

                        playerNames = names,

                        playerIsAI = isAIMap,

                        onNewGame = {
                            audioManager.playButtonClick()
                            currentScreen = Screen.SETUP
                        },

                        onMenu = {
                            audioManager.playButtonClick()
                            currentScreen = Screen.MENU
                        }
                    )
                }
            }
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
            .putBoolean(KEY_SOUND_ENABLED, soundEnabled)
            .putBoolean(KEY_MUSIC_ENABLED, musicEnabled)
            .putBoolean(KEY_VIBRATION_ENABLED, vibrationEnabled)
            .apply()
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