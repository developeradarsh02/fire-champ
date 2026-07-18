package com.firechamp.tournament.presentation.navigation

/**
 * Navigation routes - sealed class for type safety.
 * Har screen ka route + optional helper params yahan define hote hain.
 */
sealed class Screen(val route: String) {
    // Auth flow
    object Splash : Screen("splash")
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Signup : Screen("signup")
    object ForgotPassword : Screen("forgot_password")

    // Main app (after login) - hosts bottom nav with 3 tabs
    object Main : Screen("main")

    /**
     * TournamentList - specific game mode ke tournaments dikhata hai.
     * gameModeId nullable hai (null = saare tournaments, across all modes).
     *
     * Route format: tournament_list?gameModeId=...&gameModeName=...
     */
    object TournamentList : Screen("tournament_list?gameModeId={gameModeId}&gameModeName={gameModeName}&initialStatus={initialStatus}") {
        fun createRoute(gameModeId: String?, gameModeName: String, initialStatus: String? = null): String {
            // null values query se omit karo, warna "null" string ban kar filter tod deta hai
            val params = buildList {
                if (gameModeId != null) add("gameModeId=$gameModeId")
                add("gameModeName=$gameModeName")
                if (initialStatus != null) add("initialStatus=$initialStatus")
            }
            return "tournament_list?" + params.joinToString("&")
        }
    }

    /**
     * MatchResult - specific tournament ka detail + results table.
     * TournamentList me card click karne par yaha navigate hota hai.
     */
    object MatchResult : Screen("match_result/{tournamentId}") {
        fun createRoute(tournamentId: String): String = "match_result/$tournamentId"
    }

    /**
     * JoinedMatch - jab user JOIN karta hai to yaha aata hai.
     * Room ID reveal + result submission flow.
     */
    object JoinedMatch : Screen("joined_match/{tournamentId}") {
        fun createRoute(tournamentId: String): String = "joined_match/$tournamentId"
    }

    // Wallet flow (Task 5)
    object Wallet : Screen("wallet")
    object AddMoney : Screen("add_money")
    object PaymentQR : Screen("payment_qr")
    object PaymentStatus : Screen("payment_status/{isSuccess}") {
        fun createRoute(isSuccess: Boolean): String = "payment_status/$isSuccess"
    }

    // Razorpay checkout
    object RazorpayCheckout : Screen("razorpay_checkout/{orderId}/{key}/{amount}") {
        fun createRoute(orderId: String, key: String, amount: Int): String = "razorpay_checkout/$orderId/$key/$amount"
    }

    // Withdrawal flow (Task 8)
    object RedeemCoins : Screen("redeem_coins")

    // Notifications (Task 10)
    object Notifications : Screen("notifications")
    object NotificationSettings : Screen("notification_settings")

    // Account sub-screens (Task 6)
    object MyProfile : Screen("my_profile")
    object MyMatches : Screen("my_matches")
    object MyOrder : Screen("my_order")
    object MyStatistics : Screen("my_statistics")
    object MyRewards : Screen("my_rewards")
    object MyReferrals : Screen("my_referrals")
    object Announcement : Screen("announcement")
    object TopPlayers : Screen("top_players")
    object Leaderboard : Screen("leaderboard")
    object AboutUs : Screen("about_us")
    object ChangeLanguage : Screen("change_language")
    object CustomerSupport : Screen("customer_support")
    object Terms : Screen("terms")
}
