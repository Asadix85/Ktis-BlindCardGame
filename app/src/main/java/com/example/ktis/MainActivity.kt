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
import com.example.ktis.ui.screens.MainMenuScreen
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

    private var visibleCenterPile by
    mutableStateOf<List<PlayedCard>>(
        emptyList()
    )

    private var animateCenterCards by
    mutableStateOf(true)

    private var finalResult by
    mutableStateOf<FinalResult?>(null)

    /*
     * =====================================================
     * قفل دکمه انداختن کارت
     * =====================================================
     *
     * تا زمانی که انیمیشن و تغییر نوبت تمام نشده،
     * کلیک بعدی نادیده گرفته می‌شود.
     */
    private var isActionLocked by
    mutableStateOf(false)

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

                        /*
                         * اطمینان از باز بودن قفل
                         * برای شروع بازی جدید
                         */
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

                val state =
                    gameState

                if (state != null) {

                    /*
                     * ID بازیکنی که همین الان نوبتش است.
                     */
                    val playerId =
                        state.currentPlayer.id

                    GameScreen(

                        state =
                            state,

                        visibleCenterPile =
                            visibleCenterPile,

                        animateCenterCards =
                            animateCenterCards,

                        message =
                            message,

                        highlightedWinnerId =
                            highlightedWinnerId,

                        onDrawCard = {

                            /*
                             * =================================================
                             * قفل اصلی
                             * =================================================
                             *
                             * اگر قبلاً کلیک شده،
                             * هیچ کاری نکن.
                             */
                            if (isActionLocked) {
                                return@GameScreen
                            }

                            /*
                             * همین لحظه قفل می‌کنیم.
                             */
                            isActionLocked = true

                            /*
                             * اگر کارت تمام شده باشد،
                             * قفل را آزاد کن.
                             */
                            if (
                                state.currentPlayer
                                    .remainingCards <= 0
                            ) {
                                isActionLocked = false
                                return@GameScreen
                            }

                            try {

                                /*
                                 * بازیکن کارت را می‌اندازد.
                                 */
                                gameEngine.playCard()

                                /*
                                 * وضعیت جدید بازی.
                                 */
                                gameState =
                                    gameEngine.getState()

                                /*
                                 * کارت‌های روی زمین.
                                 */
                                visibleCenterPile =
                                    gameEngine
                                        .getState()
                                        .centerPile
                                        .toList()

                                /*
                                 * انیمیشن پرتاب کارت.
                                 */
                                animateCenterCards = true

                                val afterPlay =
                                    gameEngine.getState()

                                message =
                                    "${afterPlay.players.first { it.id == playerId }.name} کارت انداخت! 🃏"

                                /*
                                 * =================================================
                                 * دست هنوز تمام نشده
                                 * =================================================
                                 */
                                if (
                                    !gameEngine.isRoundComplete()
                                ) {

                                    /*
                                     * حدوداً برابر با زمان
                                     * انیمیشن چرخش زمین.
                                     */
                                    lifecycleScope.launch {

                                        delay(800)

                                        /*
                                         * حالا اجازه کلیک بعدی.
                                         */
                                        isActionLocked =
                                            false
                                    }

                                    return@GameScreen
                                }

                                /*
                                 * =================================================
                                 * دست تمام شده
                                 * =================================================
                                 *
                                 * فعلاً قفل می‌ماند تا نتیجه مشخص شود.
                                 */
                                lifecycleScope.launch {

                                    try {

                                        /*
                                         * کارت‌ها ۹۰۰ میلی‌ثانیه
                                         * روی زمین دیده شوند.
                                         */
                                        delay(900)

                                        animateCenterCards =
                                            false

                                        val winner =
                                            gameEngine.resolveRound()

                                        val resolved =
                                            gameEngine.getState()

                                        gameState =
                                            resolved

                                        /*
                                         * =================================================
                                         * برنده مشخص شده
                                         * =================================================
                                         */
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

                                            /*
                                             * نتیجه مدتی دیده شود.
                                             */
                                            delay(1500)

                                            highlightedWinnerId =
                                                null

                                            /*
                                             * پاک کردن زمین.
                                             */
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

                                                /*
                                                 * آماده برای نوبت بعد.
                                                 */
                                                isActionLocked =
                                                    false
                                            }

                                        } else {

                                            /*
                                             * =================================================
                                             * مساوی
                                             * =================================================
                                             */

                                            val tieState =
                                                gameEngine.getState()

                                            gameState =
                                                tieState

                                            message =
                                                "مساوی! ⚔️ فقط بازیکن‌های مساوی ادامه میدن."

                                            /*
                                             * کارت‌های مساوی
                                             * همچنان روی زمین.
                                             */
                                            visibleCenterPile =
                                                tieState.centerPile
                                                    .toList()

                                            animateCenterCards =
                                                false

                                            /*
                                             * کمی زمان برای دیدن نتیجه.
                                             */
                                            delay(1000)

                                            message = ""

                                            animateCenterCards =
                                                true

                                            currentScreen =
                                                Screen.GAME

                                            /*
                                             * قفل آزاد می‌شود.
                                             */
                                            isActionLocked =
                                                false
                                        }

                                    } catch (_: Exception) {

                                        /*
                                         * اگر در پردازش نتیجه خطایی رخ داد،
                                         * بازی برای همیشه قفل نشود.
                                         */
                                        isActionLocked =
                                            false
                                    }
                                }

                            } catch (_: Exception) {

                                /*
                                 * اگر خود playCard خطا داد،
                                 * قفل را آزاد می‌کنیم.
                                 */
                                isActionLocked =
                                    false
                            }
                        },

                        /*
                         * =================================================
                         * بر زدن دسته پشتیبان
                         * =================================================
                         */
                        onShuffle = {

                            /*
                             * هنگام انیمیشن/پردازش دست،
                             * بر زدن انجام نشود.
                             */
                            if (isActionLocked) {
                                return@GameScreen
                            }

                            gameEngine.shuffleBalanceDeck()

                            message =
                                "کارت‌ها بر زده شدند! 🔀"
                        },

                        onBack = {

                            /*
                             * خروج از بازی قفل را آزاد می‌کند.
                             */
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

                        result =
                            result,

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

    private fun gameEngineStateOrNull() =
        try {
            gameEngine.getState()
        } catch (_: Exception) {
            null
        }

    private enum class Screen {
        MENU,
        SETUP,
        GAME,
        RESULT
    }
}