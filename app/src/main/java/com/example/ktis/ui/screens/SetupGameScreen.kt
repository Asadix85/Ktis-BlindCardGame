package com.example.ktis.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktis.R
import com.example.ktis.domain.model.PlayerSetup
import com.example.ktis.ui.components.WoodenButton
import com.example.ktis.ui.theme.Caramel
import com.example.ktis.ui.theme.NazaninFont
import com.example.ktis.ui.theme.WoodDark
import com.example.ktis.ui.theme.WoodMedium


/*
 * ============================================================
 * مدل داخلی برای هر ردیف بازیکن
 * ============================================================
 *
 * اسم و وضعیت AI رو با هم نگه می‌داره تا وقتی
 * کاربر toggle رو می‌زنه، اسم قبلیش حفظ بشه.
 */
private data class PlayerEntry(
    val name: String,
    val isAI: Boolean
)


@Composable
fun SetupGameScreen(
    onStartGame: (List<PlayerSetup>) -> Unit,
    onBack: () -> Unit
) {

    var playerCount by remember {
        mutableStateOf(2)
    }

    /*
     * لیست بازیکنان.
     *
     * هر آیتم هم اسم و هم isAI رو نگه می‌داره.
     */
    val players = remember {
        mutableStateListOf(
            PlayerEntry(name = "بازیکن ۱", isAI = false),
            PlayerEntry(name = "بازیکن ۲", isAI = false)
        )
    }

    fun updatePlayerCount(count: Int) {
        playerCount = count

        while (players.size < count) {
            players.add(
                PlayerEntry(
                    name = "بازیکن ${players.size + 1}",
                    isAI = false
                )
            )
        }

        while (players.size > count) {
            players.removeAt(players.lastIndex)
        }
    }

    /*
     * ========================================================
     * اعتبارسنجی
     * ========================================================
     *
     * قوانین:
     *  - حداقل ۱ بازیکن انسان (نه همه AI)
     *  - همه‌ی بازیکن‌های انسان باید اسم غیرخالی داشته باشن
     *  - اسم بازیکنان انسان باید یکتا باشه
     */
    val hasAtLeastOneHuman =
        players.any { !it.isAI }

    val allHumanNamesValid =
        players
            .filter { !it.isAI }
            .all { it.name.trim().isNotEmpty() }

    val humanNames =
        players
            .filter { !it.isAI }
            .map { it.name.trim() }

    val humanNamesUnique =
        humanNames.distinct().size == humanNames.size

    val canStart =
        hasAtLeastOneHuman &&
                allHumanNamesValid &&
                humanNamesUnique

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Image(
            painter = painterResource(
                id = R.drawable.menu_wood_background
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.45f),
                            Color.Black.copy(alpha = 0.12f),
                            Color.Black.copy(alpha = 0.5f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = 32.dp,
                    vertical = 28.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "KTIS",
                color = Caramel,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 5.sp
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "تنظیم بازی",
                color = Caramel,
                fontFamily = NazaninFont,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            Text(
                text = "تعداد بازیکنان",
                color = Caramel,
                fontFamily = NazaninFont,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                CounterButton(
                    text = "−",
                    enabled = playerCount > 2,
                    onClick = {
                        if (playerCount > 2) {
                            updatePlayerCount(playerCount - 1)
                        }
                    }
                )

                Text(
                    text = playerCount.toString(),
                    color = Caramel,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold
                )

                CounterButton(
                    text = "+",
                    enabled = playerCount < 8,
                    onClick = {
                        if (playerCount < 8) {
                            updatePlayerCount(playerCount + 1)
                        }
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            Text(
                text = "نام بازیکنان",
                color = Caramel,
                fontFamily = NazaninFont,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "روی دکمه 🤖 بزن تا بازیکن به هوش مصنوعی تبدیل بشه",
                color = Caramel.copy(alpha = 0.7f),
                fontFamily = NazaninFont,
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            players.forEachIndexed { index, entry ->

                PlayerSetupRow(
                    index = index,
                    entry = entry,
                    onNameChange = { newName ->
                        players[index] =
                            players[index].copy(
                                name = newName
                            )
                    },
                    onAIToggle = { isAI ->
                        players[index] =
                            players[index].copy(
                                isAI = isAI
                            )
                    }
                )
            }

            /*
             * اگه کاربر همه رو AI کرده باشه،
             * یه پیام هشدار نشون بده.
             */
            if (!hasAtLeastOneHuman) {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text =
                        "حداقل یک بازیکن باید انسان باشه",
                    color = Color(0xFFE57373),
                    fontFamily = NazaninFont,
                    fontSize = 15.sp
                )
            }

            if (
                hasAtLeastOneHuman &&
                !allHumanNamesValid
            ) {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text =
                        "نام همه‌ی بازیکنان انسان باید پر بشه",
                    color = Color(0xFFE57373),
                    fontFamily = NazaninFont,
                    fontSize = 15.sp
                )
            }

            if (
                hasAtLeastOneHuman &&
                allHumanNamesValid &&
                !humanNamesUnique
            ) {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text =
                        "نام بازیکنان انسان نباید تکراری باشه",
                    color = Color(0xFFE57373),
                    fontFamily = NazaninFont,
                    fontSize = 15.sp
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            WoodenButton(
                text = "شروع بازی",
                enabled = canStart,
                highlighted = true,
                onClick = {

                    val setups =
                        players.mapIndexed { index, p ->

                            /*
                             * اسم نهایی:
                             *  - اگه AI هست: «ربات (N+1)»
                             *  - اگه انسان هست: اسم تایپ‌شده
                             */
                            val finalName =
                                if (p.isAI) {
                                    "ربات ${index + 1}"
                                } else {
                                    p.name.trim()
                                }

                            PlayerSetup(
                                name = finalName,
                                seat = index,
                                isAI = p.isAI
                            )
                        }

                    onStartGame(setups)
                }
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            WoodenButton(
                text = "بازگشت",
                onClick = onBack
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )
        }
    }
}


/*
 * ============================================================
 * ردیف تنظیم یک بازیکن
 * ============================================================
 *
 * شامل:
 *  - فیلد اسم (وقتی AI هست، غیرفعال و «ربات N»)
 *  - دکمه‌ی toggle با 🤖 یا 👤
 */
@Composable
private fun PlayerSetupRow(
    index: Int,
    entry: PlayerEntry,
    onNameChange: (String) -> Unit,
    onAIToggle: (Boolean) -> Unit
) {

    /*
     * اسم نمایشی:
     *  - اگه AI هست: «ربات (N+1)»
     *  - وگرنه: اسم ذخیره‌شده
     */
    val displayedName =
        if (entry.isAI) {
            "ربات ${index + 1}"
        } else {
            entry.name
        }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        OutlinedTextField(
            value = displayedName,
            onValueChange = onNameChange,
            enabled = !entry.isAI,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            label = {
                Text(
                    text = "بازیکن ${index + 1}",
                    fontFamily = NazaninFont
                )
            },
            singleLine = true,
            isError =
                !entry.isAI &&
                        entry.name.trim().isEmpty(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Caramel,
                unfocusedTextColor = Caramel.copy(alpha = 0.85f),
                disabledTextColor = Caramel.copy(alpha = 0.6f),
                focusedBorderColor = Caramel,
                unfocusedBorderColor = Caramel.copy(alpha = 0.4f),
                disabledBorderColor = Caramel.copy(alpha = 0.25f),
                focusedLabelColor = Caramel,
                unfocusedLabelColor = Caramel.copy(alpha = 0.6f),
                disabledLabelColor = Caramel.copy(alpha = 0.4f),
                cursorColor = Caramel,
                focusedContainerColor = WoodDark.copy(alpha = 0.25f),
                unfocusedContainerColor = WoodDark.copy(alpha = 0.18f),
                disabledContainerColor = WoodDark.copy(alpha = 0.12f)
            )
        )

        AIToggleButton(
            isAI = entry.isAI,
            onToggle = onAIToggle
        )
    }
}


/*
 * ============================================================
 * دکمه‌ی toggle AI
 * ============================================================
 */
@Composable
private fun AIToggleButton(
    isAI: Boolean,
    onToggle: (Boolean) -> Unit
) {

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val isPressed by interactionSource
        .collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = tween(100),
        label = "ai_toggle_press"
    )

    val shape = RoundedCornerShape(14.dp)

    val gradient = if (isAI) {
        Brush.verticalGradient(
            colors = listOf(Caramel, WoodMedium)
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(WoodMedium, WoodDark)
        )
    }

    val borderColor = if (isAI) {
        Caramel.copy(alpha = 0.75f)
    } else {
        Caramel.copy(alpha = 0.35f)
    }

    Box(
        modifier = Modifier
            .size(64.dp)
            .scale(scale)
            .shadow(
                elevation = 4.dp,
                shape = shape
            )
            .clip(shape)
            .background(gradient)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Caramel),
                onClick = { onToggle(!isAI) }
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = if (isAI) "🤖" else "👤",
            fontSize = 26.sp
        )
    }
}


/*
 * ============================================================
 * دکمه‌ی کوچیک شمارنده (+ / −)
 * ============================================================
 */
@Composable
private fun CounterButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val isPressed by interactionSource
        .collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue =
            if (isPressed && enabled) 0.92f else 1f,
        animationSpec = tween(100),
        label = "counter_button_press"
    )

    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .width(64.dp)
            .height(56.dp)
            .scale(scale)
            .shadow(
                elevation = if (enabled) 5.dp else 0.dp,
                shape = shape
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors =
                        if (enabled) {
                            listOf(WoodMedium, WoodDark)
                        } else {
                            listOf(
                                WoodDark.copy(alpha = 0.45f),
                                WoodDark.copy(alpha = 0.55f)
                            )
                        }
                )
            )
            .border(
                width = 1.dp,
                color = Caramel.copy(
                    alpha = if (enabled) 0.35f else 0.1f
                ),
                shape = shape
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(color = Caramel),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,
            color =
                if (enabled) {
                    Caramel
                } else {
                    Caramel.copy(alpha = 0.3f)
                },
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}