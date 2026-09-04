package com.example.ktis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktis.domain.model.PlayerSetup

@Composable
fun SetupGameScreen(
    onStartGame: (List<PlayerSetup>) -> Unit,
    onBack: () -> Unit
) {

    var playerCount by remember {
        mutableStateOf(2)
    }

    val names = remember {
        mutableStateListOf(
            "بازیکن ۱",
            "بازیکن ۲"
        )
    }

    /*
     * صندلی هر بازیکن
     *
     * مقدار اولیه:
     * بازیکن ۱ → پایین
     * بازیکن ۲ → بالا
     */
    val seats = remember {
        mutableStateListOf(
            7,
            3
        )
    }

    var selectedPlayer by remember {
        mutableStateOf(0)
    }

    fun updatePlayerCount(count: Int) {

        playerCount = count

        while (names.size < count) {
            names.add(
                "بازیکن ${names.size + 1}"
            )
        }

        while (names.size > count) {
            names.removeAt(names.lastIndex)
        }

        while (seats.size < count) {

            val availableSeat =
                (0..7).firstOrNull {
                    it !in seats
                } ?: 0

            seats.add(availableSeat)
        }

        while (seats.size > count) {
            seats.removeAt(seats.lastIndex)
        }

        if (selectedPlayer >= count) {
            selectedPlayer = count - 1
        }
    }

    fun seatName(seat: Int): String? {

        val index =
            seats.indexOf(seat)

        return if (index >= 0) {
            names.getOrNull(index)
        } else {
            null
        }
    }

    fun selectSeat(seat: Int) {

        val occupiedBy =
            seats.indexOf(seat)

        /*
         * اگر صندلی متعلق به همین بازیکن است،
         * انتخابش را حفظ می‌کنیم.
         */
        if (occupiedBy == selectedPlayer) {
            return
        }

        /*
         * صندلی اشغال‌شده قابل انتخاب نیست.
         */
        if (occupiedBy >= 0) {
            return
        }

        seats[selectedPlayer] = seat
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(20.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text = "تنظیم بازی",
            style =
                MaterialTheme
                    .typography
                    .headlineMedium
        )

        Spacer(
            Modifier.height(20.dp)
        )

        /*
         * =========================
         * تعداد بازیکنان
         * =========================
         */

        Text(
            text = "تعداد بازیکنان",
            style =
                MaterialTheme
                    .typography
                    .titleMedium
        )

        Spacer(
            Modifier.height(10.dp)
        )

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween,

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Button(
                onClick = {

                    if (playerCount > 2) {
                        updatePlayerCount(
                            playerCount - 1
                        )
                    }
                }
            ) {
                Text("−")
            }

            Text(
                text =
                    playerCount.toString(),

                style =
                    MaterialTheme
                        .typography
                        .headlineMedium
            )

            Button(
                onClick = {

                    if (playerCount < 8) {
                        updatePlayerCount(
                            playerCount + 1
                        )
                    }
                }
            ) {
                Text("+")
            }
        }

        Spacer(
            Modifier.height(20.dp)
        )

        /*
         * =========================
         * اسم بازیکنان
         * =========================
         */

        names.forEachIndexed { index, name ->

            OutlinedTextField(

                value = name,

                onValueChange = {
                    names[index] = it
                },

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = 8.dp
                        ),

                label = {
                    Text(
                        "بازیکن ${index + 1}"
                    )
                },

                singleLine = true,

                supportingText = {

                    Text(
                        if (
                            selectedPlayer == index
                        ) {
                            "صندلی را انتخاب کنید"
                        } else {
                            "صندلی ${seats[index] + 1}"
                        }
                    )
                },

                isError =
                    name.trim().isEmpty(),

                maxLines = 1
            )
        }

        Spacer(
            Modifier.height(12.dp)
        )

        /*
         * =========================
         * انتخاب بازیکن
         * =========================
         */

        Text(
            text =
                "انتخاب محل نشستن",

            style =
                MaterialTheme
                    .typography
                    .titleMedium,

            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            Modifier.height(4.dp)
        )

        Text(
            text =
                "ابتدا بازیکن را انتخاب کنید، سپس جای او را روی زمین انتخاب کنید.",

            textAlign =
                TextAlign.Center,

            style =
                MaterialTheme
                    .typography
                    .bodySmall
        )

        Spacer(
            Modifier.height(14.dp)
        )

        /*
         * انتخاب بازیکن
         */

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(6.dp)
        ) {

            names.forEachIndexed { index, name ->

                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .border(
                                width =
                                    if (
                                        selectedPlayer ==
                                        index
                                    ) {
                                        2.dp
                                    } else {
                                        1.dp
                                    },

                                color =
                                    if (
                                        selectedPlayer ==
                                        index
                                    ) {
                                        MaterialTheme
                                            .colorScheme
                                            .primary
                                    } else {
                                        Color.Gray
                                    },

                                shape =
                                    RoundedCornerShape(
                                        10.dp
                                    )
                            )
                            .clickable {
                                selectedPlayer =
                                    index
                            }
                            .padding(7.dp),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            "${index + 1}",

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }

        Spacer(
            Modifier.height(16.dp)
        )

        /*
         * =========================
         * زمین فرضی
         * =========================
         */

        Box(
            modifier =
                Modifier
                    .size(
                        width = 300.dp,
                        height = 300.dp
                    )
                    .background(
                        Color(0xFF315C3A),
                        RoundedCornerShape(150.dp)
                    )
                    .border(
                        2.dp,
                        Color.White.copy(
                            alpha = 0.25f
                        ),
                        RoundedCornerShape(
                            150.dp
                        )
                    )
        ) {

            /*
             * مرکز
             */

            Box(
                modifier =
                    Modifier
                        .size(90.dp)
                        .align(
                            Alignment.Center
                        )
                        .background(
                            Color(0xFF24482D),
                            CircleShape
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = "زمین",
                    color = Color.White,
                    fontWeight =
                        FontWeight.Bold
                )
            }

            /*
             * صندلی 1 — بالا
             */

            SeatButton(
                seat = 3,
                name = seatName(3),
                modifier =
                    Modifier
                        .align(
                            Alignment.TopCenter
                        )
                        .padding(top = 8.dp),

                selected =
                    seats[selectedPlayer] == 3,

                onClick = {
                    selectSeat(3)
                }
            )

            /*
             * صندلی 2 — بالا راست
             */

            SeatButton(
                seat = 4,
                name = seatName(4),
                modifier =
                    Modifier
                        .align(
                            Alignment.TopEnd
                        )
                        .padding(
                            top = 65.dp,
                            end = 12.dp
                        ),

                selected =
                    seats[selectedPlayer] == 4,

                onClick = {
                    selectSeat(4)
                }
            )

            /*
             * صندلی 3 — راست
             */

            SeatButton(
                seat = 5,
                name = seatName(5),
                modifier =
                    Modifier
                        .align(
                            Alignment.CenterEnd
                        )
                        .padding(end = 5.dp),

                selected =
                    seats[selectedPlayer] == 5,

                onClick = {
                    selectSeat(5)
                }
            )

            /*
             * صندلی 4 — پایین راست
             */

            SeatButton(
                seat = 6,
                name = seatName(6),
                modifier =
                    Modifier
                        .align(
                            Alignment.BottomEnd
                        )
                        .padding(
                            bottom = 65.dp,
                            end = 12.dp
                        ),

                selected =
                    seats[selectedPlayer] == 6,

                onClick = {
                    selectSeat(6)
                }
            )

            /*
             * صندلی 5 — پایین
             */

            SeatButton(
                seat = 7,
                name = seatName(7),
                modifier =
                    Modifier
                        .align(
                            Alignment.BottomCenter
                        )
                        .padding(bottom = 8.dp),

                selected =
                    seats[selectedPlayer] == 7,

                onClick = {
                    selectSeat(7)
                }
            )

            /*
             * صندلی 6 — پایین چپ
             */

            SeatButton(
                seat = 0,
                name = seatName(0),
                modifier =
                    Modifier
                        .align(
                            Alignment.BottomStart
                        )
                        .padding(
                            bottom = 65.dp,
                            start = 12.dp
                        ),

                selected =
                    seats[selectedPlayer] == 0,

                onClick = {
                    selectSeat(0)
                }
            )

            /*
             * صندلی 7 — چپ
             */

            SeatButton(
                seat = 1,
                name = seatName(1),
                modifier =
                    Modifier
                        .align(
                            Alignment.CenterStart
                        )
                        .padding(start = 5.dp),

                selected =
                    seats[selectedPlayer] == 1,

                onClick = {
                    selectSeat(1)
                }
            )

            /*
             * صندلی 8 — بالا چپ
             */

            SeatButton(
                seat = 2,
                name = seatName(2),
                modifier =
                    Modifier
                        .align(
                            Alignment.TopStart
                        )
                        .padding(
                            top = 65.dp,
                            start = 12.dp
                        ),

                selected =
                    seats[selectedPlayer] == 2,

                onClick = {
                    selectSeat(2)
                }
            )
        }

        Spacer(
            Modifier.height(10.dp)
        )

        Text(
            text =
                "${names[selectedPlayer]} انتخاب شده",

            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            Modifier.height(18.dp)
        )

        /*
         * =========================
         * شروع بازی
         * =========================
         */

        Button(
            onClick = {

                val players =
                    names.mapIndexed { index, name ->

                        PlayerSetup(
                            name =
                                name.trim(),

                            seat =
                                seats[index]
                        )
                    }

                onStartGame(players)
            },

            modifier =
                Modifier.fillMaxWidth(),

            enabled =
                names.all {
                    it.trim().isNotEmpty()
                }
        ) {

            Text("شروع بازی")
        }

        Spacer(
            Modifier.height(8.dp)
        )

        Button(
            onClick = onBack,

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text("بازگشت")
        }
    }
}

@Composable
private fun SeatButton(
    seat: Int,
    name: String?,
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {

    val occupied =
        name != null

    Box(
        modifier =
            modifier
                .size(
                    width = 72.dp,
                    height = 48.dp
                )
                .background(
                    if (selected) {
                        MaterialTheme
                            .colorScheme
                            .primary
                    } else if (occupied) {
                        Color.Black.copy(
                            alpha = 0.45f
                        )
                    } else {
                        Color.White.copy(
                            alpha = 0.12f
                        )
                    },

                    RoundedCornerShape(12.dp)
                )
                .border(
                    width =
                        if (selected) {
                            2.dp
                        } else {
                            1.dp
                        },

                    color =
                        if (selected) {
                            MaterialTheme
                                .colorScheme
                                .primary
                        } else {
                            Color.White.copy(
                                alpha = 0.25f
                            )
                        },

                    shape =
                        RoundedCornerShape(12.dp)
                )
                .clickable(
                    enabled = !occupied || selected,
                    onClick = onClick
                ),

        contentAlignment =
            Alignment.Center
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text =
                    name ?: "خالی",

                fontSize = 11.sp,

                fontWeight =
                    if (selected) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    },

                color =
                    Color.White,

                maxLines = 1
            )

            Text(
                text =
                    "جای ${seat + 1}",

                fontSize = 9.sp,

                color =
                    Color.White.copy(
                        alpha = 0.7f
                    )
            )
        }
    }
}