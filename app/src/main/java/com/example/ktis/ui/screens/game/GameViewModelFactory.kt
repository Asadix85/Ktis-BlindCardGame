package com.example.ktis.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ktis.domain.engine.GameEngine
import com.example.ktis.domain.save.SaveManager
import com.example.ktis.ui.audio.KtisAudioManager


/*
 * ============================================================
 * GameViewModelFactory
 * ============================================================
 */
class GameViewModelFactory(
    private val gameEngine: GameEngine,
    private val saveManager: SaveManager,
    private val audioManager: KtisAudioManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return GameViewModel(
                gameEngine = gameEngine,
                saveManager = saveManager,
                audioManager = audioManager
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}