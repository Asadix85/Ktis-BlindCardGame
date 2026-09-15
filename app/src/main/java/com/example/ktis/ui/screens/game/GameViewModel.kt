package com.example.ktis.ui.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktis.domain.engine.GameEngine
import com.example.ktis.domain.engine.GameResult
import com.example.ktis.domain.model.FinalResult
import com.example.ktis.domain.model.PlayerSetup
import com.example.ktis.domain.model.PlayedCard
import com.example.ktis.domain.save.SaveManager
import com.example.ktis.ui.audio.KtisAudioManager
import com.example.ktis.ui.screens.game.GameConstants
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/*
 * ============================================================
 * GameViewModel
 * ============================================================
 *
 * این کلاس مسئول منطق بازی و AI Loop هست.
 *
 * از MainActivity فقط یه نمونه ساخته می‌شه و توی
 * AppContent استفاده می‌شه.
 *
 * کارهاش:
 *  - نگه‌داشتن state بازی (gameState, message, ...)
 *  - AI Loop خودکار
 *  - ذخیره/بازیابی خودکار
 *  - پخش صدا
 *
 * ناوبری بین صفحه‌ها و settings توی MainActivity می‌مونه.
 */
class GameViewModel(
    private val gameEngine: GameEngine,
    private val saveManager: SaveManager,
    private val audioManager: KtisAudioManager
) : ViewModel() {

    /*
     * ========================================================
     * وضعیت بازی
     * ========================================================
     */

    var gameState by
    mutableStateOf(
        gameEngineStateOrNull()
    )
        private set

    var message by
    mutableStateOf("")
        private set

    var highlightedWinnerId by
    mutableStateOf<Int?>(null)
        private set

    var visibleCenterPile by
    mutableStateOf<List<PlayedCard>>(emptyList())
        private set

    var animateCenterCards by
    mutableStateOf(true)
        private set

    var finalResult by
    mutableStateOf<FinalResult?>(null)
        private set

    var isActionLocked by
    mutableStateOf(false)
        private set

    var hasSavedGame by
    mutableStateOf(false)
        private set

    /*
     * ========================================================
     * AI Loop
     * ========================================================
     *
     * یه job که وقتی نوبت AI می‌شه، خودکار حرکتش رو
     * انجام می‌ده.
     *
     * با viewModelScope راه‌اندازی می‌شه تا با lifecycle
     * ViewModel هماهنگ باشه و خودکار کنسل بشه.
     */

    private var aiLoopJob: Job? = null

    /*
     * Callback که وقتی نوبت AI تمام می‌شه و بازی
     * به Result رسید، از MainActivity خبر می‌ده.
     *
     * چون GameViewModel نمی‌تونه ناوبری کنه،
     * MainActivity یه lambda می‌ذاره که وقتی
     * ViewModel بگه «بازی تموم شد»، بره Result.
     */
    var onGameOver: (() -> Unit)? = null

    init {
        /*
         * اگه بازی از قبل ذخیره شده، hasSavedGame رو ست کن.
         */
        hasSavedGame = saveManager.hasSavedGame()

        /*
         * اگه بازی از قبل شروع شده (مثلاً بعد از rotation)،
         * AI Loop رو راه بنداز.
         */
        startAILoopIfNeeded()
    }

    /*
     * ========================================================
     * شروع بازی جدید
     * ========================================================
     */
    fun startNewGame(
        players: List<PlayerSetup>
    ) {
        gameState = gameEngine.startGame(players)
        message = ""
        highlightedWinnerId = null
        visibleCenterPile = emptyList()
        animateCenterCards = true
        finalResult = null
        isActionLocked = false

        saveCurrentGame()

        startAILoopIfNeeded()
    }

    /*
     * ========================================================
     * ادامه‌ی بازی ذخیره‌شده
     * ========================================================
     */
    fun continueSavedGame(): Boolean {

        try {

            val saveData =
                saveManager.load()
                    ?: run {
                        hasSavedGame = false
                        return false
                    }

            gameState =
                gameEngine.restoreFromSave(saveData)

            message = ""
            highlightedWinnerId = null

            visibleCenterPile =
                gameState
                    ?.centerPile
                    ?.toList()
                    ?: emptyList()

            animateCenterCards = false
            finalResult = null
            isActionLocked = false

            if (gameState?.gameOver == true) {

                finalResult =
                    GameResult.calculate(gameState!!)

                saveManager.delete()
                hasSavedGame = false

                startAILoopIfNeeded()

                return false
            }

            hasSavedGame = true

            startAILoopIfNeeded()

            return true

        } catch (_: Exception) {

            saveManager.delete()
            hasSavedGame = false
            gameState = null
            message = ""
            highlightedWinnerId = null
            visibleCenterPile = emptyList()
            finalResult = null
            isActionLocked = false

            return false
        }
    }

    /*
     * ========================================================
     * یک حرکت (توسط انسان یا AI)
     * ========================================================
     */
    fun performMove() {

        if (isActionLocked) return

        val state =
            gameState
                ?: return

        if (state.currentPlayer.remainingCards <= 0) {
            return
        }

        val playerId =
            state.currentPlayer.id

        isActionLocked = true

        try {

            audioManager.playCardDraw()

            gameEngine.playCard()

            gameState = gameEngine.getState()

            visibleCenterPile =
                gameEngine.getState().centerPile.toList()

            animateCenterCards = true

            viewModelScope.launch {
                delay(450)
                animateCenterCards = false
            }

            saveCurrentGame()

            val afterPlay = gameEngine.getState()

            message =
                "${afterPlay.players.first { it.id == playerId }.name} کارت انداخت! 🃏"

            audioManager.playCardPlace()

            if (!gameEngine.isRoundComplete()) {

                viewModelScope.launch {
                    delay(800)
                    isActionLocked = false
                    startAILoopIfNeeded()
                }

                return
            }

            resolveRoundAfterDelay()

        } catch (_: Exception) {

            isActionLocked = false
        }
    }

    /*
     * ========================================================
     * حل دور (بعد از تأخیر)
     * ========================================================
     */
    private fun resolveRoundAfterDelay() {

        viewModelScope.launch {

            try {

                delay(900)

                animateCenterCards = false

                val winner =
                    gameEngine.resolveRound()

                val resolved =
                    gameEngine.getState()

                gameState = resolved

                saveCurrentGame()

                if (winner != null) {

                    handleWinner(
                        winnerId = winner,
                        resolvedState = resolved
                    )

                } else {

                    handleTieOrGameOver(
                        tieState = resolved
                    )
                }

            } catch (_: Exception) {

                isActionLocked = false
            }
        }
    }

    /*
     * ========================================================
     * برنده‌ی دور
     * ========================================================
     */
    private suspend fun handleWinner(
        winnerId: Int,
        resolvedState: com.example.ktis.domain.model.GameState
    ) {

        highlightedWinnerId = winnerId

        val winnerPlayer =
            resolvedState.players.first {
                it.id == winnerId
            }

        message =
            "${winnerPlayer.name} این دست رو برد! 🏆"

        audioManager.playRoundWin()

        delay(1500)

        highlightedWinnerId = null
        visibleCenterPile = emptyList()
        animateCenterCards = true

        val updated = gameEngine.getState()
        gameState = updated

        if (updated.gameOver) {

            finalResult = GameResult.calculate(updated)

            saveManager.delete()
            hasSavedGame = false

            isActionLocked = false

            onGameOver?.invoke()

        } else {

            message = ""
            isActionLocked = false

            /*
             * اگه بازیکن بعدی AI هست، AI Loop خودش
             * راه می‌افته چون currentPlayer عوض شده.
             */
            startAILoopIfNeeded()
        }
    }

    /*
     * ========================================================
     * tie یا پایان بازی
     * ========================================================
     */
    private suspend fun handleTieOrGameOver(
        tieState: com.example.ktis.domain.model.GameState
    ) {

        if (tieState.gameOver) {

            finalResult = GameResult.calculate(tieState)

            saveManager.delete()
            hasSavedGame = false

            isActionLocked = false

            onGameOver?.invoke()

            return
        }

        message =
            "مساوی! ⚔️ فقط بازیکن‌های مساوی ادامه میدن."

        audioManager.playTie()

        visibleCenterPile =
            tieState.centerPile.toList()

        animateCenterCards = false

        delay(1000)

        message = ""
        animateCenterCards = true
        isActionLocked = false

        startAILoopIfNeeded()
    }

    /*
     * ========================================================
     * بر زدن کارت‌ها
     * ========================================================
     */
    fun shuffle() {

        if (isActionLocked) return

        audioManager.playButtonClick()

        gameEngine.shuffleBalanceDeck()

        saveCurrentGame()

        message = "کارت‌ها بر زده شدند! 🔀"
    }

    /*
     * ========================================================
     * ذخیره
     * ========================================================
     */
    fun saveCurrentGame() {

        try {

            if (gameEngineStateOrNull() == null) {
                return
            }

            saveManager.save(gameEngine.createSaveData())

            hasSavedGame = true

        } catch (_: Exception) {
        }
    }

    /*
     * ========================================================
     * پاک کردن save (مثلاً وقتی کاربر به منو برمی‌گرده)
     * ========================================================
     */
    fun onBackToMenu() {
        saveCurrentGame()
        isActionLocked = false
        aiLoopJob?.cancel()
    }

    /*
     * ========================================================
     * AI Loop
     * ========================================================
     *
     * یه coroutine که وقتی نوبت AI می‌شه، بعد از یه
     * تأخیر کوتاه، move رو انجام می‌ده.
     *
     * اگه AI Loop از قبل در حال اجرا باشه، دوباره
     * راه نمی‌افته.
     */
    private fun startAILoopIfNeeded() {

        val state = gameState ?: return

        if (!state.currentPlayer.isAI) return
        if (state.gameOver) return

        /*
         * اگه job قبلی در حال اجراست، کنسلش کن و
         * یه job جدید بساز.
         */
        aiLoopJob?.cancel()

        aiLoopJob = viewModelScope.launch {

            /*
             * تأخیر کوتاه قبل از حرکت AI.
             *
             * بعداً می‌تونیم تصادفی کنیم.
             */
            delay(GameConstants.AIDelayMillis)

            /*
             * دوباره چک کن که هنوز نوبت AI هست.
             */
            val current =
                gameState
                    ?: return@launch

            if (!current.currentPlayer.isAI) {
                return@launch
            }

            if (current.gameOver) {
                return@launch
            }

            if (isActionLocked) {
                return@launch
            }

            performMove()
        }
    }

    /*
     * ========================================================
     * دسترسی به AudioManager (برای تغییر تنظیمات صدا)
     * ========================================================
     */
    fun getAudioManager(): KtisAudioManager {
        return audioManager
    }

    /*
     * ========================================================
     * پاک‌سازی وقتی ViewModel از بین می‌ره
     * ========================================================
     */
    override fun onCleared() {
        super.onCleared()
        aiLoopJob?.cancel()
    }

    /*
     * ========================================================
     * هلپر
     * ========================================================
     */
    private fun gameEngineStateOrNull() =
        try {
            gameEngine.getState()
        } catch (_: Exception) {
            null
        }
}