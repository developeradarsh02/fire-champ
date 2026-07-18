package com.firechamp.tournament.data.repository

import com.firechamp.tournament.data.model.Banner
import com.firechamp.tournament.data.model.BannerClickAction
import com.firechamp.tournament.data.model.BannerType
import com.firechamp.tournament.data.model.GameMode
import com.firechamp.tournament.data.remote.FirebaseDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Earn tab ke data ke liye repository.
 * Real Firestore se banners + game modes live stream karta hai.
 *
 * Fallback: agar Firestore empty/error, hardcoded default list return karta hai
 * (offline-first UX).
 */
@Singleton
class EarnRepository @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) {

    /**
     * 6 banners - carousel me auto-scroll honge.
     * Firestore `banners` collection se live aate hain.
     */
    fun observeBanners(): Flow<List<Banner>> =
        firebaseDataSource.observeBanners()

    /**
     * Default hardcoded banners (offline fallback / first-launch).
     * Order: HOW TO ADD COINS → GET YOUR BONUS → CUSTOMER SUPPORT →
     *        WITHDRAWAL COMPLETE → FOLLOW WHATSAPP → FOLLOW TELEGRAM
     */
    fun getDefaultBanners(): List<Banner> = listOf(
        // 1. How to Add Coins - opens YouTube tutorial
        Banner(
            id = "b1",
            type = BannerType.HOW_TO_ADD_COINS,
            title = "How to Add Coins",
            subtitle = "Step-by-step guide to deposit",
            clickAction = BannerClickAction.OpenYouTube("dQw4w9WgXcQ")  // placeholder video ID
        ),
        // 2. Get Your Bonus - opens Telegram
        Banner(
            id = "b2",
            type = BannerType.DEPOSIT_BONUS,
            title = "Get Your Bonus",
            subtitle = "Claim your daily bonus on Telegram",
            clickAction = BannerClickAction.OpenTelegramChannel
        ),
        // 3. Customer Support - opens Telegram (support)
        Banner(
            id = "b3",
            type = BannerType.SUPPORT_TIMING,
            title = "Customer Support",
            subtitle = "Get help on Telegram 24/7",
            clickAction = BannerClickAction.OpenTelegramChannel
        ),
        // 4. Withdrawal Complete - opens YouTube
        Banner(
            id = "b4",
            type = BannerType.WITHDRAWAL_COMPLETE,
            title = "Withdrawal Complete",
            subtitle = "Watch tutorial: how to withdraw",
            clickAction = BannerClickAction.OpenYouTube("dQw4w9WgXcQ")
        ),
        // 5. Follow WhatsApp - opens Telegram channel (whatsapp not supported via intent, use TG)
        Banner(
            id = "b5",
            type = BannerType.FOLLOW_WHATSAPP,
            title = "Follow WhatsApp",
            subtitle = "Get instant updates on WhatsApp",
            clickAction = BannerClickAction.OpenTelegramChannel
        ),
        // 6. Follow Telegram - opens Telegram channel
        Banner(
            id = "b6",
            type = BannerType.WEEKLY_LEADERBOARD,
            title = "Follow Telegram",
            subtitle = "Join our official Telegram channel",
            clickAction = BannerClickAction.OpenTelegramChannel
        )
    )

    /**
     * Game modes - 2-column grid me dikhenge.
     * Firestore `gameModes` collection se live aate hain.
     */
    fun observeGameModes(): Flow<List<GameMode>> =
        firebaseDataSource.observeGameModes()

    /**
     * Default hardcoded game modes (offline fallback).
     */
    // EXCLUSIVE section ke 5 game modes - inke banners drawable me hain
    // (game_mode_full_map, game_mode_cs_1v1, game_mode_lw_1v1, game_mode_lw_2v2, game_mode_cs_4v4)
    fun getDefaultGameModes(): List<GameMode> = listOf(
        GameMode(id = "gm1", name = "FULL MAP", label = "BATTLE ROYALE"),
        GameMode(id = "gm2", name = "CS 1V1",   label = "CLASH SQUAD"),
        GameMode(id = "gm3", name = "LW 1V1",   label = "LONE WOLF"),
        GameMode(id = "gm4", name = "LW 2V2",   label = "LONE WOLF"),
        GameMode(id = "gm5", name = "CS 4V4",   label = "CLASH SQUAD")
    )

    fun observeAnnouncements(): Flow<List<com.firechamp.tournament.data.model.Announcement>> =
        firebaseDataSource.observeAnnouncements()

    /**
     * Marquee strip ke liye continuous scrolling text.
     */
    fun getMarqueeText(): String = "🔥 WITHDRAWAL COMPLETE IN 30 MIN ✅  |  💰 JOIN DAILY TOURNAMENTS & WIN REAL CASH  |  📱 FOLLOW OUR WHATSAPP CHANNEL FOR BONUS CODES"
}
