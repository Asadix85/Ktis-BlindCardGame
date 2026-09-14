package com.example.ktis.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import com.example.ktis.R
import com.example.ktis.domain.model.Card
import com.example.ktis.domain.model.GameState
import com.example.ktis.domain.model.PlayedCard
import com.example.ktis.ui.components.CardView
import com.example.ktis.ui.theme.Gold
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private val CarpetRed = Color(0xFF6B2028)
private val CarpetBurgundy = Color(0xFF48151C)
private val CarpetBrown = Color(0xFF321A15)
private val CarpetGold = Color(0xFFC58A45)

/*
 * اندازه‌ی کارت‌های روی میز
 */
private val CenterCardBoxWidth = 84.dp
private val CenterCardBoxHeight = 122.dp
private val CenterCardWidth = 72.dp


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

    /*
     * بازیکن فعلی در ترتیب واقعی میز
     */
    val currentPlayerPosition =
        orderedPlayers.indexOfFirst {
            it.id == state.currentPlayer.id
        }.let {
            if (it >= 0) it else 0
        }

    /*
     * زاویه‌ی بین دو بازیکن
     */
    val angleStep =
        360f / playerCount

    /*
     * ========================================================
     * چرخش میز
     * ========================================================
     *
     * مختصات پایه:
     *
     * 0 درجه = پایین
     *
     * +angle = ساعتگرد
     */
    var rotationTarget by remember(playerCount) {
        mutableStateOf(
            -currentPlayerPosition * angleStep
        )
    }

    var previousPlayerPosition by remember(playerCount) {
        mutableStateOf(
            currentPlayerPosition
        )
    }

    /*
     * وقتی نوبت عوض شد، میز فقط به اندازه‌ی حرکت
     * بازیکن بعدی می‌چرخد.
     *
     * ترتیب وقایع در هر نوبت:
     *
     *  1) کارت روی میز پرتاب می‌شه.
     *  2) چون میز هنوز نچرخیده، بازیکن فعلی در
     *     پایین صفحه است و کارت جلوی خودش می‌افته.
     *  3) بعد از ۹۰۰ms که پرتاب تموم شد، میز
     *     می‌چرخه سمت بازیکن بعدی.
     *  4) نوبت نفر بعدی می‌شه.
     */
    LaunchedEffect(
        currentPlayerPosition,
        playerCount
    ) {

        if (
            currentPlayerPosition !=
            previousPlayerPosition
        ) {

            val difference =
                (
                        currentPlayerPosition -
                                previousPlayerPosition +
                                playerCount
                        ) % playerCount

            /*
             * صبر قبل از چرخش میز.
             */
            delay(
                timeMillis = 900L
            )

            rotationTarget -=
                difference * angleStep

            previousPlayerPosition =
                currentPlayerPosition
        }
    }

    /*
     * انیمیشن چرخش میز
     */
    val tableRotation by animateFloatAsState(
        targetValue = rotationTarget,
        animationSpec =
            tween(
                durationMillis = 620,
                easing = FastOutSlowInEasing
            ),
        label = "table_rotation"
    )

    /*
     * افکت برنده
     */
    val winnerPulse by animateFloatAsState(
        targetValue =
            if (highlightedWinnerId != null) {
                1f
            } else {
                0f
            },
        animationSpec =
            tween(
                durationMillis = 260,
                easing = FastOutSlowInEasing
            ),
        label = "winner_pulse"
    )

    Box(
        modifier =
            Modifier.fillMaxSize()
    ) {

        /*
         * فرش بازی
         */
        Image(
            painter =
                painterResource(
                    id =
                        R.drawable.game_persian_carpet
                ),
            contentDescription = null,
            modifier =
                Modifier.fillMaxSize(),
            contentScale =
                ContentScale.Crop
        )

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Color.Black.copy(
                            alpha = 0.22f
                        )
                    )
        )

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(12.dp)
        ) {

            /*
             * اطلاعات بالا
             */
            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text =
                        "دست ${state.roundNumber}",
                    color =
                        CarpetGold,
                    fontSize =
                        18.sp,
                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text =
                        "جمع‌شده: ${state.totalCollectedCards}",
                    color =
                        CarpetGold,
                    fontSize =
                        16.sp,
                    fontWeight =
                        FontWeight.Bold
                )
            }

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            /*
             * پیام
             */
            if (message.isNotEmpty()) {

                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .scale(
                                1f +
                                        (
                                                0.015f *
                                                        winnerPulse
                                                )
                            ),
                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                if (
                                    highlightedWinnerId !=
                                    null
                                ) {
                                    Gold.copy(
                                        alpha = 0.94f
                                    )
                                } else {
                                    CarpetBrown.copy(
                                        alpha = 0.94f
                                    )
                                }
                        ),
                    elevation =
                        CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        ),
                    shape =
                        RoundedCornerShape(
                            14.dp
                        )
                ) {

                    Text(
                        text =
                            message,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    10.dp
                                ),
                        textAlign =
                            TextAlign.Center,
                        color =
                            if (
                                highlightedWinnerId !=
                                null
                            ) {
                                Color.Black
                            } else {
                                CarpetGold
                            },
                        fontWeight =
                            FontWeight.Bold,
                        fontSize =
                            16.sp
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )
            }

            /*
             * =================================================
             * زمین بازی
             * =================================================
             */
            BoxWithConstraints(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .shadow(
                            elevation = 14.dp,
                            shape =
                                RoundedCornerShape(
                                    24.dp
                                ),
                            clip = false
                        )
                        .clip(
                            RoundedCornerShape(
                                24.dp
                            )
                        )
                        .background(
                            Brush.radialGradient(
                                colors =
                                    listOf(
                                        CarpetRed,
                                        CarpetBurgundy
                                    ),
                                radius = 1200f
                            )
                        )
                        .border(
                            2.dp,
                            CarpetGold.copy(
                                alpha = 0.7f
                            ),
                            RoundedCornerShape(
                                24.dp
                            )
                        )
                        .border(
                            6.dp,
                            CarpetGold.copy(
                                alpha = 0.15f
                            ),
                            RoundedCornerShape(
                                24.dp
                            )
                        )
            ) {

                val density =
                    LocalDensity.current

                /*
                 * شعاع میز
                 */
                val boardRadiusPx =
                    with(density) {

                        (
                                minOf(
                                    maxWidth,
                                    maxHeight
                                ) * 0.30f
                                ).toPx()
                    }

                /*
                 * محل نهایی کارت‌ها
                 */
                val cardLandingRadiusPx =
                    boardRadiusPx * 1.16f

                /*
                 * شروع پرتاب
                 */
                val throwStartRadiusPx =
                    boardRadiusPx * 1.95f

                /*
                 * =================================================
                 * خود میز
                 * =================================================
                 */
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .rotate(
                                tableRotation
                            )
                ) {

                    /*
                     * مرکز میز
                     */
                    Box(
                        modifier =
                            Modifier
                                .size(120.dp)
                                .align(
                                    Alignment.Center
                                )
                                .shadow(
                                    elevation = 6.dp,
                                    shape =
                                        RoundedCornerShape(
                                            60.dp
                                        )
                                )
                                .background(
                                    Brush.radialGradient(
                                        colors =
                                            listOf(
                                                CarpetBurgundy,
                                                CarpetBrown
                                            )
                                    ),
                                    RoundedCornerShape(
                                        60.dp
                                    )
                                )
                                .border(
                                    3.dp,
                                    CarpetGold.copy(
                                        alpha = 0.55f
                                    ),
                                    RoundedCornerShape(
                                        60.dp
                                    )
                                ),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text =
                                "KTIS",
                            color =
                                CarpetGold.copy(
                                    alpha = 0.8f
                                ),
                            fontWeight =
                                FontWeight.Bold,
                            fontSize =
                                18.sp
                        )
                    }

                    /*
                     * =================================================
                     * کارت‌های روی میز
                     * =================================================
                     */
                    visibleCenterPile.forEachIndexed {
                            index,
                            playedCard
                        ->

                        val player =
                            orderedPlayers.firstOrNull {
                                it.id ==
                                        playedCard.playerId
                            }

                        if (player != null) {

                            /*
                             * جایگاه واقعی این بازیکن
                             * در ترتیب میز
                             */
                            val playerPosition =
                                orderedPlayers
                                    .indexOfFirst {
                                        it.id ==
                                                player.id
                                    }
                                    .coerceAtLeast(0)

                            /*
                             * آخرین کارت این بازیکن
                             */
                            val isLatestCardOfPlayer =
                                visibleCenterPile
                                    .indexOfLast {
                                        it.playerId ==
                                                playedCard.playerId
                                    } == index

                            /*
                             * وضعیت مساوی
                             */
                            val isTiedCard =
                                state.tiedPlayerIds
                                    .contains(
                                        player.id
                                    ) &&
                                        isLatestCardOfPlayer

                            /*
                             * کارت را دقیقاً از جایگاه واقعی
                             * بازیکنش محاسبه می‌کنیم.
                             */
                            TableCard(
                                modifier =
                                    Modifier.align(
                                        Alignment.Center
                                    ),

                                card =
                                    playedCard.card,

                                playerId =
                                    playedCard.playerId,

                                cardIndex =
                                    index,

                                playerPosition =
                                    playerPosition,

                                playerCount =
                                    playerCount,

                                boardRadiusPx =
                                    boardRadiusPx,

                                cardLandingRadiusPx =
                                    cardLandingRadiusPx,

                                throwStartRadiusPx =
                                    throwStartRadiusPx,

                                isWinner =
                                    highlightedWinnerId ==
                                            player.id,

                                isTied =
                                    isTiedCard,

                                animateDrop =
                                    animateCenterCards
                            )
                        }
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            /*
             * دسته کارت بازیکن فعلی
             */
            PlayerCardStack(
                remainingCards =
                    state.currentPlayer
                        .remainingCards,

                animateDraw =
                    animateCenterCards,

                enabled =
                    state.currentPlayer
                        .remainingCards > 0,

                onThrow =
                    onDrawCard
            )

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            Text(
                text =
                    "نوبت: ${state.currentPlayer.name}",
                modifier =
                    Modifier.fillMaxWidth(),
                textAlign =
                    TextAlign.Center,
                color =
                    CarpetGold,
                fontWeight =
                    FontWeight.Bold,
                fontSize =
                    18.sp
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            /*
             * بر زدن
             */
            Button(
                onClick =
                    onShuffle,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                shape =
                    RoundedCornerShape(16.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            CarpetBrown,
                        contentColor =
                            CarpetGold
                    ),
                elevation =
                    ButtonDefaults.buttonElevation(
                        defaultElevation =
                            4.dp,
                        pressedElevation =
                            1.dp
                    )
            ) {

                Text(
                    text =
                        "🔀 بر زدن کارت‌ها",
                    fontWeight =
                        FontWeight.Bold
                )
            }

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            /*
             * بازگشت
             */
            Button(
                onClick =
                    onBack,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                shape =
                    RoundedCornerShape(16.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            CarpetBurgundy,
                        contentColor =
                            CarpetGold
                    ),
                elevation =
                    ButtonDefaults.buttonElevation(
                        defaultElevation =
                            4.dp,
                        pressedElevation =
                            1.dp
                    )
            ) {

                Text(
                    text =
                        "بازگشت به منوی لوکال",
                    fontWeight =
                        FontWeight.Bold
                )
            }
        }
    }
}


/*
 * ============================================================
 * دسته کارت
 * ============================================================
 */
@Composable
private fun PlayerCardStack(
    remainingCards: Int,
    animateDraw: Boolean,
    enabled: Boolean,
    onThrow: () -> Unit
) {

    if (remainingCards <= 0) {

        Spacer(
            modifier =
                Modifier.height(82.dp)
        )

        return
    }

    val cardWidth =
        62.dp

    val cardHeight =
        88.dp

    val maxStackHeight =
        92.dp

    val visibleCards =
        minOf(
            remainingCards,
            18
        )

    val spacing =
        if (visibleCards <= 1) {

            0.dp

        } else {

            (
                    (
                            maxStackHeight.value -
                                    cardHeight.value
                            ) /
                            (visibleCards - 1)
                    ).dp
        }

    val interactionSource =
        remember {
            MutableInteractionSource()
        }

    val isPressed by
    interactionSource
        .collectIsPressedAsState()

    val pressScale by
    animateFloatAsState(
        targetValue =
            if (
                isPressed &&
                enabled
            ) {
                0.94f
            } else {
                1f
            },
        animationSpec =
            tween(100),
        label =
            "stack_press"
    )

    val throwHopOffset by
    animateFloatAsState(
        targetValue =
            if (animateDraw) {
                -16f
            } else {
                0f
            },
        animationSpec =
            keyframes {

                durationMillis =
                    240

                -18f at 90

                0f at 240
            },
        label =
            "stack_throw_hop"
    )

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(
                    maxStackHeight
                )
                .alpha(
                    if (enabled) {
                        1f
                    } else {
                        0.45f
                    }
                )
                .clickable(
                    enabled =
                        enabled,
                    interactionSource =
                        interactionSource,
                    indication =
                        ripple(
                            bounded = false,
                            color =
                                CarpetGold
                        ),
                    onClick =
                        onThrow
                ),
        contentAlignment =
            Alignment.BottomCenter
    ) {

        Box(
            modifier =
                Modifier
                    .width(
                        cardWidth
                    )
                    .height(
                        cardHeight
                    )
                    .scale(
                        pressScale
                    )
                    .offset(
                        y =
                            throwHopOffset.dp
                    )
                    .shadow(
                        elevation = 8.dp,
                        shape =
                            RoundedCornerShape(
                                8.dp
                            ),
                        clip = false
                    )
        ) {

            repeat(
                visibleCards
            ) { index ->

                Image(
                    painter =
                        painterResource(
                            id =
                                R.drawable.card_back
                        ),

                    contentDescription =
                        "دسته کارت",

                    contentScale =
                        ContentScale.Fit,

                    modifier =
                        Modifier
                            .width(
                                cardWidth
                            )
                            .height(
                                cardHeight
                            )
                            .offset(
                                y =
                                    -(
                                            spacing *
                                                    index
                                            )
                            )
                )
            }
        }

        CardCountLabel(
            count =
                remainingCards
        )
    }
}


/*
 * ============================================================
 * کارت روی میز
 * ============================================================
 *
 * کارت‌ها همیشه رو به مرکز میز قرار می‌گیرن:
 *
 * position 0 (بازیکن پایین):
 *       زاویه 0 = صاف، سر رو به بالا (مرکز)
 *
 * position 1 (بازیکن پایین-راست):
 *       زاویه 72 = سر رو به بالا-چپ (مرکز)
 *
 * position 2 (بازیکن راست):
 *       زاویه 144 = سر رو به چپ (مرکز)
 *
 * ...
 *
 * این یعنی هر کارت همیشه سرش به سمت مرکز KTIS
 * است و ته کارت به سمت بازیکنی که انداخته.
 */
@Composable
private fun TableCard(
    modifier: Modifier = Modifier,
    card: Card,
    playerId: Int,
    cardIndex: Int,
    playerPosition: Int,
    playerCount: Int,
    boardRadiusPx: Float,
    cardLandingRadiusPx: Float,
    throwStartRadiusPx: Float,
    isWinner: Boolean,
    isTied: Boolean,
    animateDrop: Boolean
) {

    /*
     * کلید انیمیشن
     */
    val animationKey =
        "$playerId-" +
                "${card.suit.name}-" +
                "${card.rank.name}-" +
                "$cardIndex"

    /*
     * انیمیشن پرتاب
     */
    val dropProgress =
        remember(animationKey) {

            Animatable(
                if (animateDrop) {
                    0f
                } else {
                    1f
                }
            )
        }

    LaunchedEffect(
        animationKey,
        animateDrop
    ) {

        if (animateDrop) {

            dropProgress.snapTo(
                0f
            )

            dropProgress.animateTo(
                targetValue =
                    1f,
                animationSpec =
                    tween(
                        durationMillis =
                            420,
                        easing =
                            FastOutSlowInEasing
                    )
            )

        } else {

            dropProgress.snapTo(
                1f
            )
        }
    }

    val progress =
        dropProgress.value

    /*
     * ========================================================
     * زاویه‌ی واقعی بازیکن
     * ========================================================
     *
     * بازیکن فعلی همیشه position = 0 است.
     *
     * position 0:
     *       ↓
     *     پایین
     *
     * position 1:
     *       ↘
     *   پایین راست
     *
     * position 2:
     *       →
     *      راست
     *
     * position 3:
     *       ↗
     *   بالا راست
     *
     * ...
     *
     * این دقیقاً ترتیب ساعتگرد است.
     */
    val angleStep =
        360f / playerCount

    val playerAngleDeg =
        playerPosition *
                angleStep

    val angleRad =
        playerAngleDeg *
                (
                        PI.toFloat() /
                                180f
                        )

    /*
     * ========================================================
     * زاویه‌ی نهایی کارت
     * ========================================================
     *
     * کارت باید سرش به سمت مرکز میز باشه.
     *
     * چون playerAngleDeg موقعیت بازیکن رو نشون می‌ده،
     * چرخوندن کارت به همون اندازه باعث می‌شه سر کارت
     * رو به مرکز قرار بگیره.
     *
     * position 0 (پایین، 0°):
     *       کارت صاف، سر بالا → رو به مرکز ✓
     *
     * position 1 (پایین-راست، 72°):
     *       کارت 72° چرخیده → سر رو به بالا-چپ (مرکز) ✓
     *
     * position 2 (راست، 144°):
     *       کارت 144° چرخیده → سر رو به چپ (مرکز) ✓
     *
     * و الی آخر.
     */
    val finalRotationDeg =
        playerAngleDeg

    /*
     * شعاع نهایی کارت
     *
     * همه‌ی کارت‌های یک بازیکن دقیقاً روی هم می‌شینن.
     */
    val finalRadius =
        cardLandingRadiusPx

    /*
     * حرکت کارت از سمت بازیکن به سمت محل خودش
     */
    val currentRadius =
        throwStartRadiusPx +
                (
                        finalRadius -
                                throwStartRadiusPx
                        ) * progress

    /*
     * مختصات نسبت به مرکز میز
     *
     * برای چیدمان ساعتگرد از -sin استفاده می‌کنیم.
     */
    val x =
        -currentRadius *
                sin(angleRad)

    val y =
        currentRadius *
                cos(angleRad)

    /*
     * چرخش کارت هنگام پرتاب.
     *
     * از صفر شروع می‌شه (کارت صاف) و به زاویه‌ی
     * نهایی می‌رسه (رو به مرکز).
     */
    val throwSpinStart =
        0f

    val rotation =
        throwSpinStart +
                (
                        finalRotationDeg -
                                throwSpinStart
                        ) * progress

    /*
     * کارت از دور کوچک است و هنگام فرود
     * به اندازه واقعی می‌رسد.
     */
    val landingScale =
        0.55f +
                0.45f * progress

    /*
     * افکت برنده
     */
    val winnerScale by
    animateFloatAsState(
        targetValue =
            if (isWinner) {
                1.08f
            } else {
                1f
            },
        animationSpec =
            keyframes {

                durationMillis =
                    520

                1f at 0

                1.08f at 180

                1.03f at 330

                1.08f at 430

                1f at 520
            },
        label =
            "winner_scale"
    )

    /*
     * افکت مساوی
     */
    val tieScale by
    animateFloatAsState(
        targetValue =
            if (isTied) {
                1.06f
            } else {
                1f
            },
        animationSpec =
            keyframes {

                durationMillis =
                    500

                1f at 0

                1.06f at 130

                1f at 250

                1.06f at 370

                1f at 500
            },
        label =
            "tie_scale"
    )

    val finalScale =
        landingScale *
                winnerScale *
                tieScale

    /*
     * کارت ابتدا در مرکز قرار می‌گیرد.
     *
     * سپس offset نسبت به مرکز میز اعمال می‌شود.
     */
    Box(
        modifier =
            modifier
                .size(
                    CenterCardBoxWidth,
                    CenterCardBoxHeight
                )
                .offset {

                    IntOffset(
                        x =
                            x.roundToInt(),
                        y =
                            y.roundToInt()
                    )
                },
        contentAlignment =
            Alignment.Center
    ) {

        /*
         * هاله‌ی برنده
         */
        if (isWinner) {

            Box(
                modifier =
                    Modifier
                        .width(
                            80.dp
                        )
                        .aspectRatio(
                            0.69f
                        )
                        .scale(
                            finalScale *
                                    1.08f
                        )
                        .background(
                            Gold.copy(
                                alpha =
                                    0.18f
                            ),
                            RoundedCornerShape(
                                10.dp
                            )
                        )
            )
        }

        /*
         * خود کارت
         *
         * چون می‌خوایم کارت رو به مرکز باشه،
         * از throwAngle صفر استفاده می‌کنیم و خودمون
         * با .rotate(finalRotationDeg) می‌چرخونیمش.
         *
         * این باعث می‌شه CardView هیچ چرخش اضافه‌ای
         * اعمال نکنه و فقط Modifier.rotate کار کنه.
         */
        CardView(
            card =
                card,

            isWinner =
                isWinner,

            isTied =
                isTied,

            throwAngle =
                0f,

            animateThrow =
                false,

            modifier =
                Modifier
                    .width(
                        CenterCardWidth
                    )
                    .scale(
                        finalScale
                    )
                    .rotate(
                        rotation
                    )
                    .aspectRatio(
                        0.69f
                    )
                    .shadow(
                        elevation =
                            if (
                                progress >
                                0.05f
                            ) {
                                4.dp
                            } else {
                                0.dp
                            },
                        shape =
                            RoundedCornerShape(
                                8.dp
                            ),
                        clip = false
                    )
        )
    }
}


/*
 * ============================================================
 * شمارنده کارت
 * ============================================================
 */
@Composable
private fun CardCountLabel(
    count: Int
) {

    if (count <= 0) {
        return
    }

    Box(
        modifier =
            Modifier
                .padding(
                    bottom = 4.dp
                )
                .background(
                    color =
                        Color.Black.copy(
                            alpha = 0.72f
                        ),
                    shape =
                        RoundedCornerShape(
                            12.dp
                        )
                )
                .padding(
                    horizontal = 9.dp,
                    vertical = 4.dp
                ),
        contentAlignment =
            Alignment.Center
    ) {

        Text(
            text =
                count.toString(),

            color =
                Color.White,

            fontSize =
                13.sp,

            fontWeight =
                FontWeight.Bold
        )
    }
}