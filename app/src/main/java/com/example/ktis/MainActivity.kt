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

    /*
     * Snapshot of the cards that have been played
     * during the current round.
     *
     * GameEngine clears centerPile when the round
     * is resolved, so we keep a separate copy for UI.
     */
    private var visibleCenterPile by
    mutableStateOf<List<PlayedCard>>(emptyList())

    /*
     * Controls whether cards currently shown on the
     * table should play the throw animation.
     */
    private var animateCenterCards by
    mutableStateOf(true)

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

                        visibleCenterPile =
                            emptyList()

                        animateCenterCards =
                            true

                        finalResult =
                            null

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

                        centerPile =
                            visibleCenterPile,

                        onContinue = {

                            /*
                             * The cards are already on the table.
                             * Do not animate them again when the next
                             * player receives the phone.
                             */
                            animateCenterCards =
                                false

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

                        /*
                         * Use our snapshot instead of state.centerPile.
                         *
                         * This is important because GameEngine clears
                         * centerPile after resolving the round.
                         */
                        visibleCenterPile =
                            visibleCenterPile,

                        animateCenterCards =
                            animateCenterCards,

                        message =
                            message,

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

                            /*
                             * Play the card.
                             */
                            gameEngine.playCard()

                            /*
                             * Update game state.
                             */
                            gameState =
                                gameEngine.getState()

                            /*
                             * IMPORTANT:
                             * Save a snapshot before resolveRound()
                             * can clear the engine's centerPile.
                             */
                            visibleCenterPile =
                                gameEngine
                                    .getState()
                                    .centerPile
                                    .toList()

                            /*
                             * A newly played card should animate.
                             */
                            animateCenterCards =
                                true

                            val afterPlay =
                                gameEngine.getState()

                            message =
                                "${afterPlay.players.first { it.id == playerId }.name} کارت انداخت! 🃏"

                            /*
                             * If this was the last card of the round,
                             * stay on the game screen long enough to show
                             * the played cards before resolving.
                             */
                            if (
                                gameEngine
                                    .isRoundComplete()
                            ) {

                                lifecycleScope.launch {

                                    /*
                                     * Give the UI time to display the
                                     * last played card.
                                     */
                                    delay(900)

                                    /*
                                     * Stop the throw animation.
                                     * Cards are now sitting on the table.
                                     */
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

                                        /*
                                         * The snapshot still contains
                                         * all cards from the round,
                                         * even though GameEngine has
                                         * already cleared its centerPile.
                                         */
                                        message =
                                            "${winnerPlayer.name} این دست رو برد! 🏆"

                                        /*
                                         * Keep the cards visible so the
                                         * players can actually see what
                                         * the winner had.
                                         */
                                        delay(1500)

                                        highlightedWinnerId =
                                            null

                                        /*
                                         * Now clear the visual table.
                                         */
                                        visibleCenterPile =
                                            emptyList()

                                        animateCenterCards =
                                            true

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

                                        /*
                                         * Tie.
                                         */
                                        val tieState =
                                            gameEngine
                                                .getState()

                                        message =
                                            "مساوی! ⚔️ فقط بازیکن‌های مساوی ادامه میدن."

                                        gameState =
                                            tieState

                                        /*
                                         * Keep the played cards visible
                                         * while showing the tie message.
                                         */
                                        delay(1000)

                                        message = ""

                                        /*
                                         * IMPORTANT:
                                         * Do NOT clear visibleCenterPile here.
                                         *
                                         * GameEngine keeps the previous cards on centerPile
                                         * because they are part of the ongoing tie-break.
                                         */
                                        visibleCenterPile =
                                            gameEngine
                                                .getState()
                                                .centerPile
                                                .toList()

                                        animateCenterCards =
                                            true

                                        currentScreen =
                                            Screen.PASS_PHONE
                                    }
                                }

                            } else {

                                /*
                                 * Not the last player.
                                 *
                                 * Save the card in visibleCenterPile
                                 * and then pass the phone.
                                 */
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