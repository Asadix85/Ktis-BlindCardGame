package com.example.ktis.ui.components

import kotlin.math.cos
import kotlin.math.sin

data class TablePlayerPosition(
    val playerId: Int,
    val x: Float,
    val y: Float
)

object TableLayout {

    /*
     * شعاع فرضی جایگاه بازیکنان
     *
     * x و y بین 0 و 1 هستند.
     */
    private const val RADIUS_X = 0.40f
    private const val RADIUS_Y = 0.36f

    /*
     * محاسبه‌ی جایگاه ثابت بازیکنان
     *
     * بازیکن شماره 0 در پایین قرار می‌گیرد.
     * بازیکنان دیگر به صورت دایره‌ای اطراف زمین قرار می‌گیرند.
     */
    fun calculatePositions(
        playerIds: List<Int>
    ): List<TablePlayerPosition> {

        if (playerIds.isEmpty()) {
            return emptyList()
        }

        val count = playerIds.size

        return playerIds.mapIndexed { index, playerId ->

            val angle =
                Math.toRadians(
                    90.0 +
                            index *
                            (360.0 / count)
                )

            val x =
                0.5f +
                        RADIUS_X *
                        cos(angle).toFloat()

            val y =
                0.5f +
                        RADIUS_Y *
                        sin(angle).toFloat()

            TablePlayerPosition(
                playerId = playerId,
                x = x,
                y = y
            )
        }
    }

    /*
     * مقدار چرخش زمین برای بازیکن فعلی
     *
     * هدف:
     * بازیکن فعلی همیشه پایین صفحه باشد.
     */
    fun rotationForPlayer(
        currentPlayerIndex: Int,
        playerCount: Int
    ): Float {

        if (playerCount <= 1) {
            return 0f
        }

        val step =
            360f / playerCount

        return -currentPlayerIndex * step
    }
}