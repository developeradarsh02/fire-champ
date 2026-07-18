package com.firechamp.tournament.presentation.screens.earn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.firechamp.tournament.R
import com.firechamp.tournament.data.model.Banner
import com.firechamp.tournament.data.model.BannerType
import com.firechamp.tournament.presentation.components.FullBleedBanner
import com.firechamp.tournament.presentation.components.MarqueeText
import com.firechamp.tournament.presentation.components.TopHeader
import com.firechamp.tournament.presentation.components.bannerImageRes
import com.firechamp.tournament.presentation.theme.BlackBackground
import com.firechamp.tournament.presentation.viewmodel.earn.EarnViewModel
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

/**
 * Earn Tab - Home screen ka main content.
 *
 * NEW LAYOUT (Arise Battle style):
 *  1. TopHeader (avatar, welcome, language, support, wallet)
 *  2. Marquee text (running banner)
 *  3. 6 Full-bleed banner images (no carousel, vertical scroll)
 *  4. (No game modes, no share, no social icons on this screen)
 *
 * Game modes/Share/Social moved to Play tab.
 */
@Composable
fun EarnScreen(
    username: String,
    walletBalance: Double,
    onLanguageClick: () -> Unit = {},
    onSupportClick: () -> Unit = {},
    onWalletClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {},
    viewModel: EarnViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBackground)
    ) {
        // 1. Top header
        TopHeader(
            username = username,
            walletBalance = walletBalance,
            onLanguageClick = onLanguageClick,
            onSupportClick = onSupportClick,
            onWalletClick = onWalletClick,
            onAvatarClick = onAvatarClick
        )

        // 2-3. Scrollable: marquee + 6 full-bleed banners
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 16.dp)
        ) {
            // Marquee strip
            item {
                Spacer(modifier = Modifier.height(8.dp))
                MarqueeText(text = state.marqueeText)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 6 full-bleed banners (vertical list)
            items(state.banners.size) { idx ->
                val banner = state.banners[idx]
                FullBleedBanner(
                    imageRes = bannerImageRes(banner.id),
                    onClick = {
                        val msg = when (banner.type) {
                            BannerType.HOW_TO_ADD_COINS, BannerType.DEPOSIT_BONUS ->
                                "Add Money - coming soon"
                            BannerType.SUPPORT_TIMING -> "Support - tap to open"
                            BannerType.WITHDRAWAL_COMPLETE -> "Withdraw - coming soon"
                            BannerType.FOLLOW_WHATSAPP -> "WhatsApp channel"
                            BannerType.WEEKLY_LEADERBOARD -> "Leaderboard - coming soon"
                        }
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
