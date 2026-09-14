package com.example.ktis.ui.screens.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.ktis.domain.model.Card
import com.example.ktis.ui.components.CardView
import com.example.ktis.ui.theme.Gold
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin


/*
 * ============================================================
 * کارت روی میز
 * ============================================================
 *
 * هر کارت رو به مرکز میز قرار می‌گیره:
 *
 * position 0 (بازیکن پایین، 0°):
 *       کارت صاف، سر به بالا (مرکز)
 *
 * position 1 (بازیکن پایین-راست، 72°):
 *       کارت 72 درجه چرخیده، سر رو به بالا-چپ (مرکز)
 *
 * position 2 (بازیکن راست، 144°):
 *       کارت 144 درجه چرخیده، سر رو به چپ (مرکز)
 *
 * و الی آخر.
 *
 * چرخش فقط با Modifier.rotate اعمال می‌شه.
 * CardView همیشه throwAngle = 0f و animateThrow = false
 * می‌گیره تا چرخش داخلی خودش رو اضافه نکنه.
 */
@Composable
fun TableCard(
    modifier: Modifier = Modifier,
    card: Card,
    playerId: Int,
    cardIndex: Int,
    playerPosition: Int,
    playerCount: Int,
    cardLandingRadiusPx: Float,
    throwStartRadiusPx: Float,
    isWinner: Boolean,
    isTied: Boolean,
    animateDrop: Boolean
) {

    val animationKey =
        "$playerId-" +
                "${card.suit.name}-" +
                "${card.rank.name}-" +
                "$cardIndex"

    val dropProgress =
        remember(animationKey) {
            Animatable(
                if (animateDrop) 0f else 1f
            )
        }

    LaunchedEffect(animationKey, animateDrop) {

        if (animateDrop) {

            dropProgress.snapTo(0f)

            dropProgress.animateTo(
                targetValue = 1f,
                animationSpec =
                    tween(
                        durationMillis =
                            GameConstants.DropAnimationDuration,
                        easing = FastOutSlowInEasing
                    )
            )

        } else {

            dropProgress.snapTo(1f)
        }
    }

    val progress = dropProgress.value

    val angleStep =
        360f / playerCount

    val playerAngleDeg =
        playerPosition * angleStep

    val angleRad =
        playerAngleDeg *
                (PI.toFloat() / 180f)

    val currentRadius =
        throwStartRadiusPx +
                (cardLandingRadiusPx - throwStartRadiusPx) * progress

    /*
     * چیدمان ساعتگرد.
     */
    val x =
        -currentRadius * sin(angleRad)

    val y =
        currentRadius * cos(angleRad)

    /*
     * زاویه‌ی نهایی کارت.
     *
     * برابر با زاویه‌ی موقعیت بازیکن، تا سر کارت
     * رو به مرکز میز بشینه.
     */
    val finalRotationDeg =
        playerAngleDeg

    /*
     * چرخش اولیه‌ی پرتاب.
     *
     * صفر است تا کارت صاف شروع کنه و در حین پرتاب
     * به زاویه‌ی نهایی بچرخه.
     */
    val throwSpinStart =
        0f

    val rotation =
        throwSpinStart +
                (finalRotationDeg - throwSpinStart) * progress

    val landingScale =
        GameConstants.DropStartScale +
                (GameConstants.DropEndScale - GameConstants.DropStartScale) *
                progress

    val winnerScale by
    animateFloatAsState(
        targetValue =
            if (isWinner) {
                GameConstants.WinnerScale
            } else {
                1f
            },
        animationSpec =
            keyframes {

                durationMillis =
                    GameConstants.WinnerScaleDuration

                1f at 0

                GameConstants.WinnerScale at 180

                1.03f at 330

                GameConstants.WinnerScale at 430

                1f at GameConstants.WinnerScaleDuration
            },
        label = "winner_scale"
    )

    val tieScale by
    animateFloatAsState(
        targetValue =
            if (isTied) {
                GameConstants.TieScale
            } else {
                1f
            },
        animationSpec =
            keyframes {

                durationMillis =
                    GameConstants.TieScaleDuration

                1f at 0

                GameConstants.TieScale at 130

                1f at 250

                GameConstants.TieScale at 370

                1f at GameConstants.TieScaleDuration
            },
        label = "tie_scale"
    )

    val finalScale =
        landingScale * winnerScale * tieScale

    Box(
        modifier = modifier
            .size(
                GameConstants.CenterCardBoxWidth,
                GameConstants.CenterCardBoxHeight
            )
            .offset {

                IntOffset(
                    x = x.roundToInt(),
                    y = y.roundToInt()
                )
            },
        contentAlignment = Alignment.Center
    ) {

        if (isWinner) {

            Box(
                modifier = Modifier
                    .width(80.dp)
                    .aspectRatio(0.69f)
                    .scale(finalScale * 1.08f)
                    .background(
                        Gold.copy(alpha = 0.18f),
                        RoundedCornerShape(10.dp)
                    )
            )
        }

        CardView(
            card = card,
            isWinner = isWinner,
            isTied = isTied,

            /*
             * CardView هیچ چرخش داخلی نگیره.
             * چرخش رو خودمون با Modifier.rotate می‌دیم.
             */
            throwAngle = 0f,
            animateThrow = false,

            modifier = Modifier
                .width(GameConstants.CenterCardWidth)
                .scale(finalScale)
                .rotate(rotation)
                .aspectRatio(0.69f)
                .shadow(
                    elevation =
                        if (progress > 0.05f) 4.dp else 0.dp,
                    shape = RoundedCornerShape(8.dp),
                    clip = false
                )
        )
    }
}