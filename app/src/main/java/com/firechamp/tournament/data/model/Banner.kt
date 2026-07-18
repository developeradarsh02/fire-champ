package com.firechamp.tournament.data.model

/**
 * Banner types - Earn screen ke carousel me dikhne wale saare banners.
 * Real app me ye Firebase se aayenge (Task 14 me), abhi hardcoded hain.
 */
enum class BannerType {
    HOW_TO_ADD_COINS,        // Banner 1: tutorial-style
    DEPOSIT_BONUS,           // Banner 2: table
    SUPPORT_TIMING,          // Banner 3: timing info
    WITHDRAWAL_COMPLETE,     // Banner 4: success card
    FOLLOW_WHATSAPP,         // Banner 5: follow CTA
    WEEKLY_LEADERBOARD       // Banner 6: prize banner
}

/**
 * Click action - har banner pe click karne par kya hoga.
 */
sealed class BannerClickAction {
    object OpenDeposit : BannerClickAction()
    object OpenWithdraw : BannerClickAction()
    object OpenSupport : BannerClickAction()
    object OpenWhatsAppChannel : BannerClickAction()
    object OpenTelegramChannel : BannerClickAction()
    object OpenLeaderboard : BannerClickAction()
    /** YouTube video (deep link or web URL) */
    data class OpenYouTube(val videoId: String) : BannerClickAction()
    data class OpenUrl(val url: String) : BannerClickAction()
}

/**
 * Banner data model.
 * - imageUrl optional - agar null hai to placeholder/gradient render hoga
 * - clickAction optional - agar null hai to banner non-clickable
 */
data class Banner(
    val id: String,
    val type: BannerType,
    val title: String,
    val subtitle: String? = null,
    val amount: String? = null,         // e.g. "₹500" for withdrawal banner
    val imageUrl: String? = null,
    val clickAction: BannerClickAction? = null
)
