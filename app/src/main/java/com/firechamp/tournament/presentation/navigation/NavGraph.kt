package com.firechamp.tournament.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.firechamp.tournament.data.model.GameMode
import com.firechamp.tournament.presentation.screens.account.AboutUsScreen
import com.firechamp.tournament.presentation.screens.account.AnnouncementScreen
import com.firechamp.tournament.presentation.screens.account.ChangeLanguageScreen
import com.firechamp.tournament.presentation.screens.account.LeaderboardScreen
import com.firechamp.tournament.presentation.screens.account.MyMatchesScreen
import com.firechamp.tournament.presentation.screens.account.MyOrderScreen
import com.firechamp.tournament.presentation.screens.account.MyReferralsScreen
import com.firechamp.tournament.presentation.screens.account.MyRewardsScreen
import com.firechamp.tournament.presentation.screens.account.MyStatisticsScreen
import com.firechamp.tournament.presentation.screens.account.TopPlayersScreen
import com.firechamp.tournament.presentation.screens.account.profile.MyProfileScreen
import com.firechamp.tournament.presentation.screens.account.support.CustomerSupportScreen
import com.firechamp.tournament.presentation.screens.auth.ForgotPasswordScreen
import com.firechamp.tournament.presentation.screens.auth.LoginScreen
import com.firechamp.tournament.presentation.screens.auth.SignupScreen
import com.firechamp.tournament.presentation.screens.auth.SplashScreen
import com.firechamp.tournament.presentation.screens.auth.WelcomeScreen
import com.firechamp.tournament.presentation.screens.main.MainScreen
import com.firechamp.tournament.presentation.screens.match.JoinedMatchScreen
import com.firechamp.tournament.presentation.screens.notifications.NotificationsScreen
import com.firechamp.tournament.presentation.screens.account.NotificationSettingsScreen
import com.firechamp.tournament.presentation.screens.play.MatchResultScreen
import com.firechamp.tournament.presentation.screens.play.TournamentListScreen
import com.firechamp.tournament.presentation.screens.wallet.AddMoneyScreen
import com.firechamp.tournament.presentation.screens.wallet.MyWalletScreen
import com.firechamp.tournament.presentation.screens.wallet.PaymentQRScreen
import com.firechamp.tournament.presentation.screens.wallet.PaymentStatusScreen
import com.firechamp.tournament.presentation.screens.wallet.RazorpayCheckoutScreen
import com.firechamp.tournament.presentation.screens.wallet.RedeemCoinsScreen

/**
 * Top-level navigation graph for the app.
 *
 * Routes:
 * - Login (start) → Signup | Main
 * - Signup → Main
 * - Main → TournamentList → (back) → Main
 * - TournamentList → MatchDetail (Task 4) → (back) → TournamentList
 *
 * TournamentList me gameModeId optional hai - null ho to saare
 * tournaments dikhao, warna specific game mode ke.
 */
@Composable
fun FireChampNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Splash - app start me dikhti hai (Task 11)
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToMain = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToWelcome = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Welcome - splash ke baad pehli screen (Register / Login choice)
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onRegisterClick = { navController.navigate(Screen.Signup.route) },
                onLoginClick = { navController.navigate(Screen.Login.route) }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onSignupClick = {
                    navController.navigate(Screen.Signup.route)
                },
                onForgotPasswordClick = {
                    navController.navigate(Screen.ForgotPassword.route)
                }
            )
        }

        // Forgot Password flow (Task 11)
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Signup.route) {
            SignupScreen(
                onSignupSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onLoginClick = {
                    // Signup → Login (Welcome stack pe rehta hai)
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Welcome.route)
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainScreen(
                onLogout = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) // Clear entire back stack
                    }
                },
                onGameModeClick = { gameMode ->
                    navController.navigate(
                        Screen.TournamentList.createRoute(gameMode.id, gameMode.name)
                    )
                },
                onMyContestClick = { status ->
                    // Ongoing/Upcoming/Completed tile → saare tournaments, us status tab pe
                    navController.navigate(
                        Screen.TournamentList.createRoute(
                            gameModeId = null,
                            gameModeName = "My Contests",
                            initialStatus = status
                        )
                    )
                },
                onWalletClick = {
                    navController.navigate(Screen.Wallet.route)
                },
                onMyProfileClick = { navController.navigate(Screen.MyProfile.route) },
                onMyWalletClick = { navController.navigate(Screen.Wallet.route) },
                onMyMatchesClick = { navController.navigate(Screen.MyMatches.route) },
                onMyOrderClick = { navController.navigate(Screen.MyOrder.route) },
                onMyStatisticsClick = { navController.navigate(Screen.MyStatistics.route) },
                onMyRewardsClick = { navController.navigate(Screen.MyRewards.route) },
                onMyReferralsClick = { navController.navigate(Screen.MyReferrals.route) },
                onAnnouncementClick = { navController.navigate(Screen.Announcement.route) },
                onTopPlayersClick = { navController.navigate(Screen.TopPlayers.route) },
                onLeaderboardClick = { navController.navigate(Screen.Leaderboard.route) },
                onAboutUsClick = { navController.navigate(Screen.AboutUs.route) },
                onSupportClick = { navController.navigate(Screen.CustomerSupport.route) },
                onTermsClick = { navController.navigate(Screen.Terms.route) },
                onChangeLanguageClick = { navController.navigate(Screen.ChangeLanguage.route) },
                onNotificationSettingsClick = { navController.navigate(Screen.NotificationSettings.route) }
            )
        }

        composable(
            route = Screen.TournamentList.route,
            arguments = listOf(
                navArgument("gameModeId") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("gameModeName") { type = NavType.StringType; defaultValue = "Tournaments" },
                navArgument("initialStatus") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) { backStackEntry ->
            val gameModeId = backStackEntry.arguments?.getString("gameModeId")
            val gameModeName = backStackEntry.arguments?.getString("gameModeName") ?: "Tournaments"
            val initialStatus = backStackEntry.arguments?.getString("initialStatus")
            // Note: walletBalance yahan 0.0 hardcode kar raha hu - real value Task 6 me AuthRepository se aayega
            // (TournamentList screen shared ViewModel use karega proper data ke liye)
            TournamentListScreen(
                gameModeId = gameModeId,
                gameModeName = gameModeName,
                walletBalance = 0.0,    // Task 6 me proper data flow hoga
                onBack = { navController.popBackStack() },
                onTournamentClick = { tournament ->
                    // Card click → MatchResult screen (Task 4)
                    navController.navigate(Screen.MatchResult.createRoute(tournament.id))
                }
            )
        }

        // MatchResult - tournament detail + full results table
        composable(
            route = Screen.MatchResult.route,
            arguments = listOf(
                navArgument("tournamentId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tournamentId = backStackEntry.arguments?.getString("tournamentId") ?: ""
            MatchResultScreen(
                tournamentId = tournamentId,
                onBack = { navController.popBackStack() }
            )
        }

        // JoinedMatch - room ID reveal + result submission (Task 9)
        composable(
            route = Screen.JoinedMatch.route,
            arguments = listOf(
                navArgument("tournamentId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tournamentId = backStackEntry.arguments?.getString("tournamentId") ?: ""
            JoinedMatchScreen(
                tournamentId = tournamentId,
                onBack = { navController.popBackStack() }
            )
        }

        // Wallet flow (Task 5)
        composable(Screen.Wallet.route) {
            MyWalletScreen(
                onBack = { navController.popBackStack() },
                onAddMoneyClick = { navController.navigate(Screen.AddMoney.route) },
                onRedeemClick = { navController.navigate(Screen.RedeemCoins.route) }
            )
        }

        composable(Screen.RedeemCoins.route) {
            RedeemCoinsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AddMoney.route) {
            AddMoneyScreen(
                onBack = { navController.popBackStack() },
                onRazorpayCheckout = { orderId, key, amount ->
                    navController.navigate(Screen.RazorpayCheckout.createRoute(orderId, key, amount))
                },
                onQrGenerate = { amount ->
                    navController.navigate(Screen.PaymentQR.route)
                }
            )
        }

        // Razorpay checkout (WebView)
        composable(
            route = Screen.RazorpayCheckout.route,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType },
                navArgument("key") { type = NavType.StringType },
                navArgument("amount") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            val key = backStackEntry.arguments?.getString("key") ?: ""
            val amount = backStackEntry.arguments?.getInt("amount") ?: 0
            RazorpayCheckoutScreen(
                orderId = orderId,
                key = key,
                amountInPaise = amount,
                onBack = { navController.popBackStack() },
                onSuccess = { paymentId, orderId ->
                    navController.navigate(Screen.PaymentStatus.createRoute(true)) {
                        popUpTo(Screen.Wallet.route)
                    }
                },
                onCancel = { navController.popBackStack() }
            )
        }

        composable(Screen.PaymentQR.route) {
            PaymentQRScreen(
                onBack = { navController.popBackStack() },
                onPaymentComplete = {
                    navController.navigate(Screen.PaymentStatus.createRoute(true)) {
                        popUpTo(Screen.Wallet.route)
                    }
                },
                onRetry = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.PaymentStatus.route,
            arguments = listOf(
                navArgument("isSuccess") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val isSuccess = backStackEntry.arguments?.getBoolean("isSuccess") ?: true
            PaymentStatusScreen(
                isSuccess = isSuccess,
                onDone = {
                    navController.navigate(Screen.Wallet.route) {
                        popUpTo(Screen.Main.route)
                    }
                },
                onRetry = { navController.popBackStack() }
            )
        }

        // Account sub-screens (Task 6)
        composable(Screen.MyProfile.route) {
            MyProfileScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.MyMatches.route) {
            MyMatchesScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.MyOrder.route) {
            MyOrderScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.MyStatistics.route) {
            MyStatisticsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.MyRewards.route) {
            MyRewardsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.MyReferrals.route) {
            MyReferralsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Announcement.route) {
            AnnouncementScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.TopPlayers.route) {
            TopPlayersScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Leaderboard.route) {
            LeaderboardScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.AboutUs.route) {
            AboutUsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.ChangeLanguage.route) {
            ChangeLanguageScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.CustomerSupport.route) {
            CustomerSupportScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Terms.route) {
            // Reuse AboutUsScreen with title change - same long-form content
            AboutUsScreen(onBack = { navController.popBackStack() })
        }

        // Notifications (Task 10)
        composable(Screen.Notifications.route) {
            NotificationsScreen(onBack = { navController.popBackStack() })
        }

        // Notification Settings (persistent toggle - DataStore)
        composable(Screen.NotificationSettings.route) {
            NotificationSettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}

/**
 * Game modes ka list - shared between EarnScreen aur PlayScreen.
 * Task 3 me hardcoded hai, baad me admin panel se aayega.
 */
internal object GameModes {
    val all: List<GameMode> = listOf(
        GameMode(id = "FULL MAP",    name = "FULL MAP",    label = "HEAD 2V2"),
        GameMode(id = "CS 1V1",      name = "CS 1V1",      label = "FREE !!"),
        GameMode(id = "LW 1V1",      name = "LW 1V1",      label = "LW-10 RS"),
        GameMode(id = "BR SURVIVAL", name = "BR SURVIVAL", label = "LW HEAD"),
        GameMode(id = "CS 2V2",      name = "CS 2V2",      label = "CLASH"),
        GameMode(id = "LW 2V2",      name = "LW 2V2",      label = "LONE WOLF")
    )
}
