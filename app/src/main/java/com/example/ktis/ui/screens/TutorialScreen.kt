package com.example.ktis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktis.R

private val NazaninFont = FontFamily(
    Font(R.font.nazanin, FontWeight.Normal)
)

private val Caramel = Color(0xFFD29A62)
private val WoodDark = Color(0xFF4A2B18)
private val WoodMedium = Color(0xFF6B3F22)
private val WoodLight = Color(0xFF8A5835)

private data class TutorialPage(
    val title: String,
    val text: String
)

private val tutorialPages = listOf(
    TutorialPage(
        title = "هدف بازی",
        text = """
            در KTIS قرار نیست تصمیم بگیری کدام کارت را بازی کنی.

            همه چیز به شانس بستگی دارد!

            وقتی نوبتت می‌شود، یک کارت را بدون اینکه ببینی برمی‌داری و بازی می‌کنی.

            در پایان بازی، کسی که کارت‌های بیشتری جمع کرده باشد، برنده است.
        """.trimIndent()
    ),

    TutorialPage(
        title = "کارت‌ها",
        text = """
            بازی با یک دسته ۵۲ کارتی انجام می‌شود.

            کارت‌ها چهار نوع دارند:
            دل، خشت، گشنیز و پیک.

            اما نوع کارت مهم نیست.

            فقط قدرت کارت مهم است.

            قدرت کارت‌ها از قوی‌ترین تا ضعیف‌ترین:

            آس ← شاه ← بی‌بی ← سرباز ← ۱۰ ← ۹ ← ۸ ← ۷ ← ۶ ← ۵ ← ۴ ← ۳ ← ۲

            پس آس قوی‌ترین کارت و ۲ ضعیف‌ترین کارت است.
        """.trimIndent()
    ),

    TutorialPage(
        title = "شروع بازی",
        text = """
            اول کارت‌ها خوب بر زده می‌شوند.

            بعد کارت‌ها بین بازیکن‌ها تقسیم می‌شوند.

            کارت‌های هر بازیکن به صورت یک دسته جلوی خودش قرار می‌گیرد.

            حالا بازی شروع می‌شود!
        """.trimIndent()
    ),

    TutorialPage(
        title = "نوبت تو",
        text = """
            وقتی نوبتت شد، بالاترین کارت دسته‌ات را بردار.

            اما یک قانون خیلی مهم وجود دارد:

            قبل از بازی کردن، نباید کارتت را ببینی!

            کارت را همان‌طور که هست روی زمین بگذار.

            بازیکن بعدی هم همین کار را انجام می‌دهد.
        """.trimIndent()
    ),

    TutorialPage(
        title = "کارت‌ها را ببینیم!",
        text = """
            وقتی همه بازیکن‌هایی که در این دست هستند کارتشان را گذاشتند، کارت‌ها را می‌بینیم.

            حالا قدرت کارت‌ها را با هم مقایسه می‌کنیم.

            هرکس کارت قوی‌تری داشته باشد، برنده این دست می‌شود.

            مثال:

            بازیکن ۱ → ۷
            بازیکن ۲ → شاه
            بازیکن ۳ → ۴
            بازیکن ۴ → آس

            آس از همه قوی‌تر است.

            پس بازیکن ۴ برنده می‌شود.
        """.trimIndent()
    ),

    TutorialPage(
        title = "جایزه برنده",
        text = """
            برنده، تمام کارت‌هایی را که وسط زمین هستند برای خودش جمع می‌کند.

            این کارت‌ها در پایان بازی برای حساب کردن امتیاز استفاده می‌شوند.

            پس هر دست که ببری، کارت‌های بیشتری برایت جمع می‌شود.
        """.trimIndent()
    ),

    TutorialPage(
        title = "اگر مساوی شد؟",
        text = """
            اگر قوی‌ترین کارت‌ها مساوی باشند، فقط همان بازیکن‌های مساوی ادامه می‌دهند.

            مثال:

            بازیکن ۱ → ۸
            بازیکن ۲ → ۸
            بازیکن ۳ → ۵
            بازیکن ۴ → ۳

            بازیکن ۱ و ۲ مساوی شده‌اند.

            پس فقط آن دو نفر ادامه می‌دهند.

            بازیکن‌های دیگر دیگر در این مقایسه کارت جدیدی بازی نمی‌کنند.
        """.trimIndent()
    ),

    TutorialPage(
        title = "دوباره کارت بکش!",
        text = """
            بازیکن‌هایی که مساوی شده‌اند، یک کارت جدید برمی‌دارند.

            این کارت را هم بدون دیدن بازی می‌کنند.

            مثلاً:

            بازیکن ۱ → ۴
            بازیکن ۲ → آس

            آس قوی‌تر است.

            پس بازیکن ۲ برنده می‌شود.

            برنده تمام کارت‌های وسط زمین را جمع می‌کند؛ حتی کارت‌هایی که از دور قبلی باقی مانده‌اند.
        """.trimIndent()
    ),

    TutorialPage(
        title = "ممکن است دوباره مساوی شود!",
        text = """
            اگر در مرحله بعد هم کارت‌ها مساوی شوند، دوباره ادامه می‌دهیم.

            مثلاً:

            ۸ = ۸
            ↓
            ۵ = ۵
            ↓
            بی‌بی = بی‌بی
            ↓
            شاه > ۷

            تا وقتی یک نفر کارت قوی‌تری نیاورد، این کار ادامه پیدا می‌کند.

            پس ممکن است یک دست چند بار پشت سر هم مساوی شود!
        """.trimIndent()
    ),

    TutorialPage(
        title = "پایان بازی",
        text = """
            وقتی دیگر کارت قابل بازی باقی نماند، بازی تمام می‌شود.

            هر بازیکن کارت‌هایی را که در طول بازی برده می‌شمارد.

            کسی که بیشترین کارت را جمع کرده باشد، برنده نهایی KTIS است!

            مثال:

            بازیکن ۱ → ۲۰ کارت
            بازیکن ۲ → ۱۴ کارت
            بازیکن ۳ → ۱۸ کارت

            بازیکن ۱ بیشترین کارت را دارد.

            پس بازیکن ۱ برنده بازی است.
        """.trimIndent()
    ),

    TutorialPage(
        title = "اگر امتیازها مساوی شدند؟",
        text = """
            اگر در پایان بازی دو یا چند بازیکن تعداد کارت یکسانی داشته باشند، یک مسابقه نهایی انجام می‌شود.

            فقط بازیکن‌هایی که مساوی شده‌اند در این مرحله شرکت می‌کنند.

            هر بازیکن یک کارت می‌کشد.

            کسی که کارت قوی‌تری بیاورد، برنده نهایی می‌شود.

            اگر دوباره مساوی شد، دوباره کارت می‌کشند.

            این کار ادامه پیدا می‌کند تا یک برنده مشخص شود.

            حالا برنده واقعی KTIS مشخص شده است! 🏆
        """.trimIndent()
    )
)

@Composable
fun TutorialScreen(
    onBack: () -> Unit
) {
    var currentPage by remember {
        mutableIntStateOf(0)
    }

    val page = tutorialPages[currentPage]

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
                    Color.Black.copy(alpha = 0.2f)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 24.dp,
                    vertical = 24.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "آموزش KTIS",
                color = Caramel,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = NazaninFont
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "مرحله ${currentPage + 1} از ${tutorialPages.size}",
                color = Caramel.copy(alpha = 0.85f),
                fontFamily = NazaninFont,
                fontSize = 17.sp
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        WoodDark.copy(alpha = 0.88f),
                        RoundedCornerShape(18.dp)
                    )
                    .border(
                        2.dp,
                        Caramel.copy(alpha = 0.35f),
                        RoundedCornerShape(18.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(
                            rememberScrollState()
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = page.title,
                        color = Caramel,
                        fontFamily = NazaninFont,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    Text(
                        text = page.text,
                        color = Color(0xFFE0B887),
                        fontFamily = NazaninFont,
                        fontSize = 20.sp,
                        lineHeight = 32.sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TutorialButton(
                    text = "قبلی",
                    enabled = currentPage > 0,
                    onClick = {
                        if (currentPage > 0) {
                            currentPage--
                        }
                    },
                    modifier = Modifier.weight(1f)
                )

                TutorialButton(
                    text = "بعدی",
                    enabled = currentPage < tutorialPages.lastIndex,
                    onClick = {
                        if (currentPage < tutorialPages.lastIndex) {
                            currentPage++
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            TutorialButton(
                text = "بازگشت",
                enabled = true,
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TutorialButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) {
            0.96f
        } else {
            1f
        },
        animationSpec = tween(80),
        label = "tutorial_button_press"
    )

    Box(
        modifier = modifier
            .height(54.dp)
            .scale(scale)
            .background(
                if (enabled) {
                    WoodMedium
                } else {
                    WoodDark.copy(alpha = 0.5f)
                },
                RoundedCornerShape(12.dp)
            )
            .border(
                1.dp,
                if (enabled) {
                    Caramel.copy(alpha = 0.35f)
                } else {
                    Caramel.copy(alpha = 0.12f)
                },
                RoundedCornerShape(12.dp)
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (enabled) {
                Caramel
            } else {
                Caramel.copy(alpha = 0.3f)
            },
            fontFamily = NazaninFont,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}