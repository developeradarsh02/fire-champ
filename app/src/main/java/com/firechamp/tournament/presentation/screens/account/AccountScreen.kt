package com.firechamp.tournament.presentation.screens.account

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Announcement
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.firechamp.tournament.presentation.components.TopHeader
import com.firechamp.tournament.presentation.theme.BlackBackground
import com.firechamp.tournament.presentation.theme.GreenSuccess
import com.firechamp.tournament.presentation.theme.PurpleDeep
import com.firechamp.tournament.presentation.theme.RedAccent
import com.firechamp.tournament.presentation.theme.WhiteSecondary
import com.firechamp.tournament.presentation.theme.WhiteText
import com.firechamp.tournament.presentation.viewmodel.account.SettingsViewModel

/**
 * Account Tab - Full proper implementation (PDF spec Task 6 ke according).
 *
 * Structure (top to bottom):
 * 1. TopHeader (reusable)
 * 2. Profile section (avatar + username + "Verified" badge)
 * 3. Stats card (3 columns: Matches Played / Total Killed / Coins Won)
 * 4. Push notification toggle
 * 5. Menu list - 17 items in EXACT order:
 *    1. My Profile
 *    2. My Wallet
 *    3. My Matches
 *    4. My Order
 *    5. My Statistics
 *    6. My Rewards
 *    7. My Referrals
 *    8. Announcement
 *    9. Top Players
 *    10. Leaderboard
 *    11. App Tutorial
 *    12. About us
 *    13. Customer Support
 *    14. Share App
 *    15. Terms & Conditions
 *    16. Change Language
 *    17. Logout (red colored)
 * 6. Footer: "Version 1.0.0" + "Developed by Fire Champ" (underlined red link)
 *
 * Sab items clickable hain aur proper navigation routes se connected hain
 * (My Profile, Customer Support, Change Language, About us, Logout - Task 6 me
 * actual screens add honge; baaki Task 7-13 me).
 */
@Composable
fun AccountScreen(
    username: String,
    walletBalance: Double,
    onLanguageClick: () -> Unit = {},
    onSupportClick: () -> Unit = {},
    onWalletClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {},
    onLogout: () -> Unit = {},
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
    onShareAppClick: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    onChangeLanguageClick: () -> Unit = {},
    onNotificationSettingsClick: () -> Unit = {},
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by settingsViewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBackground)
    ) {
        TopHeader(
            username = username,
            walletBalance = walletBalance,
            onLanguageClick = onLanguageClick,
            onSupportClick = onSupportClick,
            onWalletClick = onWalletClick,
            onAvatarClick = onAvatarClick
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // Profile section
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(PurpleDeep)
                            .clickable { onMyProfileClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = WhiteText,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = username.ifBlank { "Player" },
                        color = WhiteText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Verified,
                            contentDescription = null,
                            tint = GreenSuccess,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Verified",
                            color = GreenSuccess,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Stats card
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PurpleDeep)
                        .padding(vertical = 16.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatColumn(value = "0", label = "Matches\nPlayed")
                    VerticalDivider()
                    StatColumn(value = "0", label = "Total\nKilled")
                    VerticalDivider()
                    StatColumn(value = "0", label = "Coins\nWon", showCoin = true)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Push notification toggle - clickable to open detailed settings
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PurpleDeep)
                        .clickable { onNotificationSettingsClick() }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = null,
                        tint = WhiteText,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = "Push Notification",
                        color = WhiteText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = settings.pushEnabled,
                        onCheckedChange = { settingsViewModel.setPushEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = WhiteText,
                            checkedTrackColor = RedAccent,
                            uncheckedThumbColor = WhiteSecondary,
                            uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Menu items - 17 in order
            // Group 1: Account related
            item {
                MenuListItem(icon = Icons.Filled.Person, label = "My Profile", onClick = onMyProfileClick)
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                MenuListItem(icon = Icons.Filled.MonetizationOn, label = "My Wallet", onClick = onMyWalletClick)
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                MenuListItem(icon = Icons.Filled.SportsEsports, label = "My Matches", onClick = onMyMatchesClick)
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                MenuListItem(icon = Icons.Filled.ShoppingCart, label = "My Order", onClick = onMyOrderClick)
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                MenuListItem(icon = Icons.Filled.BarChart, label = "My Statistics", onClick = onMyStatisticsClick)
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                MenuListItem(icon = Icons.Filled.CardGiftcard, label = "My Rewards", onClick = onMyRewardsClick)
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                MenuListItem(icon = Icons.Filled.Group, label = "My Referrals", onClick = onMyReferralsClick)
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                MenuListItem(icon = Icons.Filled.Announcement, label = "Announcement", onClick = onAnnouncementClick)
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                MenuListItem(icon = Icons.Filled.Star, label = "Top Players", onClick = onTopPlayersClick)
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                MenuListItem(icon = Icons.Filled.BarChart, label = "Leaderboard", onClick = onLeaderboardClick)
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                MenuListItem(icon = Icons.Filled.Help, label = "App Tutorial", onClick = onAppTutorialClick)
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                MenuListItem(icon = Icons.Filled.Info, label = "About us", onClick = onAboutUsClick)
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                MenuListItem(icon = Icons.Filled.Settings, label = "Customer Support", onClick = onSupportClick)
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                MenuListItem(icon = Icons.Filled.Share, label = "Share App", onClick = onShareAppClick)
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                MenuListItem(icon = Icons.Filled.Info, label = "Terms & Conditions", onClick = onTermsClick)
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                MenuListItem(icon = Icons.Filled.Settings, label = "Change Language", onClick = onChangeLanguageClick)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Logout - red colored
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PurpleDeep)
                        .clickable { onLogout() }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        tint = RedAccent,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = "Logout",
                        color = RedAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = WhiteSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Version footer
                Text(
                    text = "Version 1.0.0",
                    color = WhiteSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Developed by Fire Champ",
                    color = RedAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

/**
 * Menu list item - icon + label + right chevron.
 */
@Composable
private fun MenuListItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PurpleDeep)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = WhiteText,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = label,
            color = WhiteText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = WhiteSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun StatColumn(value: String, label: String, showCoin: Boolean = false) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showCoin) {
                Text(text = "🪙", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(2.dp))
            }
            Text(
                text = value,
                color = WhiteText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = WhiteSecondary,
            fontSize = 11.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun VerticalDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(40.dp)
            .background(Color.White.copy(alpha = 0.15f))
    )
}
