package com.example.ktis.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ktis.R
import com.example.ktis.domain.model.GameState
import com.example.ktis.domain.model.PlayedCard
import com.example.ktis.ui.screens.game.GameBottomButtons
import com.example.ktis.ui.screens.game.GameConstants
import com.example.ktis.ui.screens.game.GameTopBar
import com.example.ktis.ui.screens.game.MessageCard
import com.example.ktis.ui.screens.game.PlayerCardStack
import com.example.ktis.ui.screens.game.TableArea
import com.example.ktis.ui.screens.game.TurnLabel
import kotlinx.coroutines.delay


@Composable
fun GameScreen(
    state: GameState,
    visibleCenterPile: List<PlayedCard>,
    animateCenterCards: Boolean,
    message: String,
    highlightedWinnerId: Int?,
    onDrawCard: () -> Unit,
    onShuffle: () -> Unit,
    onBack: () -> Unit
) {

    /*
     * ========================================================
     * ترتیب واقعی بازیکنان
     * ========================================================
     */
    val orderedPlayers =
        remember(state.players) {
            state.players.sortedBy { it.seat }
        }

    val playerCount =
        orderedPlayers.size.coerceAtLeast(1)

    val currentPlayerPosition =
        orderedPlayers.indexOfFirst {
            it.id == state.currentPlayer.id
        }.let {
            if (it >= 0) it else 0
        }

    val angleStep =
        360f / playerCount

    /*
     * ========================================================
     * چرخش میز
     * ========================================================
     */
    var lastHumanPosition by remember(playerCount) {
        mutableStateOf(
            if (state.currentPlayer.isAI) {
                currentPlayerPosition
            } else {
                currentPlayerPosition
            }
        )
    }

    var rotationTarget by remember(playerCount) {
        mutableStateOf(
            -lastHumanPosition * angleStep
        )
    }

    LaunchedEffect(
        state.currentPlayer.id,
        state.currentPlayer.isAI,
        playerCount
    ) {

        /*
         * اگه نوبت AI هست، میز رو نچرخون.
         */
        if (state.currentPlayer.isAI) {
            return@LaunchedEffect
        }

        if (currentPlayerPosition == lastHumanPosition) {
            return@LaunchedEffect
        }

        val difference =
            (
                    currentPlayerPosition -
                            lastHumanPosition +
                            playerCount
                    ) % playerCount

        delay(
            timeMillis = GameConstants.RotationDelayMillis
        )

        rotationTarget -=
            difference * angleStep

        lastHumanPosition =
            currentPlayerPosition
    }

    val tableRotation by animateFloatAsState(
        targetValue = rotationTarget,
        animationSpec =
            tween(
                durationMillis =
                    GameConstants.TableRotationDuration,
                easing = FastOutSlowInEasing
            ),
        label = "table_rotation"
    )

    /*
     * ========================================================
     * ساختار صفحه
     * ========================================================
     */
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        /*
         * فرش بازی
         */
        Image(
            painter = painterResource(
                id = R.drawable.game_persian_carpet
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        /*
         * لایه‌ی تیره‌کننده
         */
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(alpha = 0.22f)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {

            GameTopBar(
                roundNumber = state.roundNumber,
                totalCollectedCards = state.totalCollectedCards
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            MessageCard(
                message = message,
                highlightedWinnerId = highlightedWinnerId
            )

            if (message.isNotEmpty()) {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )
            }

            /*
             * زمین بازی
             */
            TableArea(
                state = state,
                visibleCenterPile = visibleCenterPile,
                animateCenterCards = animateCenterCards,
                highlightedWinnerId = highlightedWinnerId,
                tableRotation = tableRotation,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            /*
             * دسته کارت بازیکن فعلی
             */
            PlayerCardStack(
                remainingCards =
                    state.currentPlayer.remainingCards,
                animateDraw =
                    animateCenterCards,
                enabled =
                    state.currentPlayer.remainingCards > 0,
                onThrow = onDrawCard
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            TurnLabel(
                playerName = state.currentPlayer.name,
                isAI = state.currentPlayer.isAI
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            GameBottomButtons(
                onShuffle = onShuffle,
                onBack = onBack
            )
        }
    }
}