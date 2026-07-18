package com.firechamp.tournament.presentation.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.firechamp.tournament.data.model.GameMode
import com.firechamp.tournament.presentation.components.BottomNavBar
import com.firechamp.tournament.presentation.navigation.BottomTab
import com.firechamp.tournament.presentation.navigation.GameModes
import com.firechamp.tournament.presentation.screens.account.AccountScreen
import com.firechamp.tournament.presentation.screens.earn.EarnScreen
import com.firechamp.tournament.presentation.screens.play.PlayScreen
import com.firechamp.tournament.presentation.theme.BlackBackground
import com.firechamp.tournament.presentation.viewmodel.MainViewModel

/**
 * MainScreen - Login/Signup ke baad yahi screen dikhti hai.
 *
 * Hosts bottom nav with 3 tabs (Earn/Play/Account).
 * Saare callbacks yahan se parent NavGraph ko propagate hote hain.
 */
@Composable
fun MainScreen(
    onLogout: () -> Unit,
    onGameModeClick: (GameMode) -> Unit,
    onMyContestClick: (String) -> Unit = {},
    onWalletClick: () -> Unit = {},
    onMyProfileClick: () -> Unit = {},
    onMyWalletClick: () -> Unit = {},
    onMyMatchesClick: () -> Unit = {},
    onMyOrderClick: () -> Unit = {},
    onMyStatisticsClick: () -> Unit = {},
    onMyRewardsClick: () -> Unit = {},
    onMyReferralsClick: () -> Unit = {},
    onAnnouncementClick: () -> Unit = {},
    onTopPlayersClick: () -> Unit = {},
    onLeaderboardClick: () -> Unit = {},
    onAppTutorialClick: () -> Unit = {},
    onAboutUsClick: () -> Unit = {},
    onSupportClick: () -> Unit = {},
    onShareAppClick: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    onChangeLanguageClick: () -> Unit = {},
    onNotificationSettingsClick: () -> Unit = {},
    viewModel: MainViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableStateOf(BottomTab.EARN) }

    val username = currentUser?.username.orEmpty()
    val walletBalance = currentUser?.walletBalance ?: 0.0
    val gameModes = GameModes.all

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBackground),
        containerColor = BlackBackground,
        contentColor = Color.White,
        bottomBar = {
            BottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BlackBackground)
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                BottomTab.EARN -> EarnScreen(
                    username = username,
                    walletBalance = walletBalance,
                    onLanguageClick = { },
                    onSupportClick = { },
                    onWalletClick = onWalletClick,
                    onAvatarClick = { }
                )
                BottomTab.PLAY -> PlayScreen(
                    username = username,
                    walletBalance = walletBalance,
                    onLanguageClick = { },
                    onSupportClick = { },
                    onWalletClick = onWalletClick,
                    onAvatarClick = { },
                    onMyContestClick = { /* TODO: navigate to contest list */ },
                    onGameModeClick = onGameModeClick
                )
                BottomTab.ACCOUNT -> AccountScreen(
                    username = username,
                    walletBalance = walletBalance,
                    onLogout = {
                        viewModel.logout()
                        onLogout()
                    },
                    onWalletClick = onWalletClick,
                    onMyProfileClick = onMyProfileClick,
                    onMyWalletClick = onMyWalletClick,
                    onMyMatchesClick = onMyMatchesClick,
                    onMyOrderClick = onMyOrderClick,
                    onMyStatisticsClick = onMyStatisticsClick,
                    onMyRewardsClick = onMyRewardsClick,
                    onMyReferralsClick = onMyReferralsClick,
                    onAnnouncementClick = onAnnouncementClick,
                    onTopPlayersClick = onTopPlayersClick,
                    onLeaderboardClick = onLeaderboardClick,
                    onAboutUsClick = onAboutUsClick,
                    onSupportClick = onSupportClick,
                    onShareAppClick = onShareAppClick,
                    onTermsClick = onTermsClick,
                    onChangeLanguageClick = onChangeLanguageClick,
                    onNotificationSettingsClick = onNotificationSettingsClick
                )
            }
        }
    }
}
