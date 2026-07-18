package com.firechamp.tournament.presentation.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firechamp.tournament.presentation.components.EmptyStateScreen
import com.firechamp.tournament.presentation.components.SectionHeader
import com.firechamp.tournament.presentation.components.SubScreenTopBar
import com.firechamp.tournament.presentation.theme.BlackBackground
import com.firechamp.tournament.presentation.theme.GoldCoin
import com.firechamp.tournament.presentation.theme.PurpleDeep
import com.firechamp.tournament.presentation.theme.WhiteSecondary
import com.firechamp.tournament.presentation.theme.WhiteText

/**
 * My Matches Screen - 3 tabs (ONGOING / UPCOMING / RESULTS) with empty state.
 */
@Composable
fun MyMatchesScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) } // 0=Ongoing, 1=Upcoming, 2=Results
    val messages = listOf(
        "No Ongoing Match Found.",
        "No Upcoming Match Found.",
        "No Results Found."
    )

    Column(modifier = Modifier.fillMaxSize().background(BlackBackground)) {
        SubScreenTopBar(title = "My Matches", onBack = onBack)
        Row(
            modifier = Modifier.fillMaxWidth().background(BlackBackground).padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("ONGOING", "UPCOMING", "RESULTS").forEachIndexed { idx, tab ->
                Column(
                    modifier = Modifier.weight(1f).clickable { selectedTab = idx }.padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = tab,
                        color = if (idx == selectedTab) WhiteText else WhiteSecondary,
                        fontSize = 13.sp,
                        fontWeight = if (idx == selectedTab) FontWeight.Bold else FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(if (idx == selectedTab) com.firechamp.tournament.presentation.theme.PurplePrimary else PurpleDeep)
                    )
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = messages[selectedTab],
                color = WhiteText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * My Order Screen - empty state.
 */
@Composable
fun MyOrderScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(BlackBackground)) {
        SubScreenTopBar(title = "My Order", onBack = onBack)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No Order Found.",
                color = WhiteText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * My Statistics Screen - empty state.
 * (Jab data ho tab match-wise kills/wins/matches-played chart/list dikhega)
 */
@Composable
fun MyStatisticsScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(BlackBackground)) {
        SubScreenTopBar(title = "My Statistics", onBack = onBack)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No Data Found.",
                color = WhiteText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * My Rewards Screen - summary + list (empty by default).
 */
@Composable
fun MyRewardsScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(BlackBackground)) {
        SubScreenTopBar(title = "My Rewards", onBack = onBack)
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // Summary section
            item {
                SectionHeader(text = "MY REWARDS SUMMARY")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(vertical = 18.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SummaryStatCol(value = "0", label = "Rewards", showCoin = false)
                    VerticalLine()
                    SummaryStatCol(value = "0", label = "Earnings", showCoin = true)
                }
            }
            // List section
            item {
                SectionHeader(text = "MY REWARDS LIST")
                // Table header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE0E0E0))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(text = "Date", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text(text = "Rewards", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text(text = "Earnings", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                }
            }
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp).background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Rewards Yet.",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * My Referrals Screen - summary + list (empty by default).
 * Task 13 me proper referral logic add hoga.
 */
@Composable
fun MyReferralsScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(BlackBackground)) {
        SubScreenTopBar(title = "My Referrals", onBack = onBack)
        // Referral code banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(PurpleDeep)
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "YOUR REFERRAL CODE",
                    color = WhiteSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "FIREFIX-XXXXX",
                    color = WhiteText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Share & earn ₹50 when your friend makes their first deposit",
                    color = WhiteSecondary,
                    fontSize = 11.sp
                )
            }
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                SectionHeader(text = "MY REFERRALS SUMMARY")
                Row(
                    modifier = Modifier.fillMaxWidth().background(Color.White).padding(vertical = 18.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SummaryStatCol(value = "0", label = "Referrals", showCoin = false)
                    VerticalLine()
                    SummaryStatCol(value = "0", label = "Earnings", showCoin = true)
                }
            }
            item {
                SectionHeader(text = "MY REFERRALS LIST")
                Row(
                    modifier = Modifier.fillMaxWidth().background(Color(0xFFE0E0E0)).padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(text = "Date", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text(text = "Player Name", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
                    Text(text = "Status", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                }
            }
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp).background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Referrals Found.",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Top Players Screen - tabs for game modes + leaderboard.
 */
@Composable
fun TopPlayersScreen(onBack: () -> Unit) {
    val modes = listOf("FULL MAP", "CS 1 VS 1", "LW 1 VS 1", "HEAD 1v1")
    var selected by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize().background(BlackBackground)) {
        SubScreenTopBar(title = "Top Players", onBack = onBack)

        // Mode selector
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            modes.forEachIndexed { idx, mode ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (idx == selected) com.firechamp.tournament.presentation.theme.PurplePrimary else PurpleDeep)
                        .clickable { selected = idx },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mode,
                        color = WhiteText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                }
            }
        }

        // Section header
        Box(
            modifier = Modifier.fillMaxWidth().background(com.firechamp.tournament.presentation.theme.PurpleDark).padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = modes[selected], color = WhiteText, fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        }

        // Table header
        Row(
            modifier = Modifier.fillMaxWidth().background(Color(0xFF1A237E)).padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(text = "User Name", color = WhiteText, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(text = "Winning", color = WhiteText, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
        }

        // Empty state
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No players yet for ${modes[selected]}", color = WhiteSecondary, fontSize = 13.sp)
        }
    }
}

/**
 * Leaderboard Screen - referral-count based.
 */
@Composable
fun LeaderboardScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(BlackBackground)) {
        SubScreenTopBar(title = "Leaderboard", onBack = onBack)

        // Table header
        Row(
            modifier = Modifier.fillMaxWidth().background(com.firechamp.tournament.presentation.theme.PurpleDeep).padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(text = "Username", color = WhiteText, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(text = "Total Referral", color = WhiteText, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
        }

        // Empty state
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Leaderboard coming soon.\nRefer friends to climb up!",
                color = WhiteSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * About us / Terms & Conditions Screen - long-form scrollable text.
 */
@Composable
fun AboutUsScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(BlackBackground)) {
        SubScreenTopBar(title = "About us", onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            AboutSection("About us", "Fire Champ is India's fastest-growing esports tournament platform. We host Free Fire, BGMI and other mobile esports tournaments with real cash prizes. Our mission is to give every skilled mobile gamer a fair chance to win money by doing what they love — playing games.")
            AboutSection("Our Mission", "To build India's most trusted and transparent skill-based esports ecosystem where every player gets a fair shot at winning real rewards for their gaming skills.")
            AboutSection("What We Offer", "") {
                BulletItem("Diverse Tournaments", "Solo, Duo, Squad formats across multiple game modes — Full Map, Clash Squad, Lone Wolf, and more.")
                BulletItem("Seamless Experience", "Easy deposit, instant join, transparent results and quick withdrawals via UPI.")
                BulletItem("Real-Time Updates", "Live match notifications, slot counts, room ID reveals — all in real-time via push notifications.")
                BulletItem("Rewards and Recognition", "Daily, weekly, and monthly leaderboards. Referral bonuses. Special tournaments for top players.")
                BulletItem("Community Engagement", "Active WhatsApp & Telegram community. Direct support from real humans, not bots.")
            }
            AboutSection("Why Choose Fire Champ?", "") {
                BulletItem("Fair Play", "Anti-cheat detection, manual result verification, screenshot-based proof for every match.")
                BulletItem("Expert Support", "24/7 customer support via Telegram and WhatsApp. Real human agents, quick resolution.")
                BulletItem("Continuous Improvement", "Weekly app updates, new game modes, better rewards based on community feedback.")
            }
            AboutSection("Contact Us", "📧 Email: firechampcustomerservice@gmail.com\n📱 Telegram: @firechampp\n📷 Instagram: @firechamp.app\n💬 WhatsApp: +91-9522079569")
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Download Fire Champ today and become part of our global gaming community! 🏆",
                color = WhiteText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun AboutSection(title: String, body: String) {
    Spacer(modifier = Modifier.height(12.dp))
    Text(text = title, color = com.firechamp.tournament.presentation.theme.PurplePrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    if (body.isNotEmpty()) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = body, color = WhiteText, fontSize = 12.sp, lineHeight = 18.sp)
    }
}

@Composable
private fun AboutSection(title: String, body: String, content: @Composable () -> Unit) {
    Spacer(modifier = Modifier.height(12.dp))
    Text(text = title, color = com.firechamp.tournament.presentation.theme.PurplePrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(6.dp))
    content()
}

@Composable
private fun BulletItem(label: String, description: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(text = "• ", color = com.firechamp.tournament.presentation.theme.PurplePrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Column {
            Text(text = label, color = WhiteText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(text = description, color = WhiteSecondary, fontSize = 12.sp, lineHeight = 17.sp)
        }
    }
}

/**
 * Change Language Screen - English / Hindi selection.
 */
@Composable
fun ChangeLanguageScreen(onBack: () -> Unit) {
    var selected by remember { mutableStateOf("English") }
    val languages = listOf(
        "English" to "en",
        "हिन्दी (Hindi)" to "hi"
    )

    Column(modifier = Modifier.fillMaxSize().background(BlackBackground)) {
        SubScreenTopBar(title = "Choose Language", onBack = onBack)
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            languages.forEach { (name, code) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .clickable { selected = name }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Radio
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(if (selected == name) com.firechamp.tournament.presentation.theme.PurplePrimary else Color.White)
                            .border(width = 2.dp, color = if (selected == name) com.firechamp.tournament.presentation.theme.PurplePrimary else Color(0xFFBDBDBD), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selected == name) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.White))
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = name, color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

/**
 * Helper composables.
 */
@Composable
private fun SummaryStatCol(value: String, label: String, showCoin: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showCoin) {
                Text(text = "🪙", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(2.dp))
            }
            Text(text = value, color = Color.Black, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, color = Color(0xFF757575), fontSize = 12.sp)
    }
}

@Composable
private fun VerticalLine() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(40.dp)
            .background(Color(0xFFE0E0E0))
    )
}
