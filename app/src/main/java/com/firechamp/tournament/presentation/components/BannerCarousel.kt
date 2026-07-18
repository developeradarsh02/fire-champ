package com.firechamp.tournament.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firechamp.tournament.data.model.Banner
import com.firechamp.tournament.data.model.BannerType
import com.firechamp.tournament.presentation.theme.GoldCoin
import com.firechamp.tournament.presentation.theme.GreenSuccess
import com.firechamp.tournament.presentation.theme.OrangeWarning
import com.firechamp.tournament.presentation.theme.PurpleBright
import com.firechamp.tournament.presentation.theme.PurpleDark
import com.firechamp.tournament.presentation.theme.PurplePrimary
import com.firechamp.tournament.presentation.theme.RedAccent
import com.firechamp.tournament.presentation.theme.WhiteText
import kotlinx.coroutines.delay

/**
 * Auto-scrolling horizontal banner carousel.
 * Har 3.5 second me next banner pe scroll hota hai.
 * Page indicator dots niche dikhte hain.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BannerCarousel(
    banners: List<Banner>,
    onBannerClick: (Banner) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (banners.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { banners.size })

    // Auto-scroll every 3.5 seconds
    LaunchedEffect(pagerState) {
        while (true) {
            delay(3500)
            val next = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(next)
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            pageSpacing = 8.dp,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
        ) { page ->
            BannerCard(
                banner = banners[page],
                onClick = { onBannerClick(banners[page]) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Page indicator dots
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            banners.indices.forEach { idx ->
                val isSelected = pagerState.currentPage == idx
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(if (isSelected) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) PurplePrimary else WhiteText.copy(alpha = 0.3f))
                )
            }
        }
    }
}

/**
 * Single banner card - type ke hisaab se styling change hoti hai.
 */
@Composable
private fun BannerCard(
    banner: Banner,
    onClick: () -> Unit
) {
    val gradient = when (banner.type) {
        BannerType.HOW_TO_ADD_COINS -> Brush.horizontalGradient(
            listOf(PurpleDark, PurplePrimary)
        )
        BannerType.DEPOSIT_BONUS -> Brush.horizontalGradient(
            listOf(Color(0xFF1B5E20), Color(0xFF2E7D32))
        )
        BannerType.SUPPORT_TIMING -> Brush.horizontalGradient(
            listOf(Color(0xFF0D47A1), Color(0xFF1976D2))
        )
        BannerType.WITHDRAWAL_COMPLETE -> Brush.horizontalGradient(
            listOf(Color(0xFF1B5E20), Color(0xFF388E3C))
        )
        BannerType.FOLLOW_WHATSAPP -> Brush.horizontalGradient(
            listOf(Color(0xFF1A3A1A), Color(0xFF25D366))
        )
        BannerType.WEEKLY_LEADERBOARD -> Brush.horizontalGradient(
            listOf(Color(0xFFB71C1C), Color(0xFFFF6F00))
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(gradient)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        when (banner.type) {
            BannerType.WITHDRAWAL_COMPLETE -> {
                // Special layout: amount card with checkmark
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "✅ ${banner.title}",
                            color = WhiteText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = WhiteText,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = banner.amount ?: "",
                                color = GoldCoin,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = "View details →",
                        color = WhiteText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            else -> {
                // Default layout
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = banner.title,
                        color = WhiteText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    if (!banner.subtitle.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = banner.subtitle,
                            color = WhiteText.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            maxLines = 2
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                            tint = WhiteText,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Tap to view",
                            color = WhiteText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
