package com.firechamp.tournament.presentation.screens.play

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firechamp.tournament.R
import androidx.hilt.navigation.compose.hiltViewModel
import com.firechamp.tournament.data.model.GameMode
import com.firechamp.tournament.presentation.components.*
import com.firechamp.tournament.presentation.theme.*
import com.firechamp.tournament.presentation.viewmodel.earn.EarnViewModel
import com.firechamp.tournament.presentation.viewmodel.earn.EarnUiState
import kotlinx.coroutines.delay


@Composable
fun PlayScreen(
    username: String,
    walletBalance: Double,
    onLanguageClick: () -> Unit = {},
    onSupportClick: () -> Unit = {},
    onWalletClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {},
    onMyContestClick: (String) -> Unit = {},
    onGameModeClick: (GameMode) -> Unit = {},
    earnViewModel: EarnViewModel = hiltViewModel()
) {
    val earnState by earnViewModel.uiState.collectAsState()
    PlayScreenContent(
        username = username,
        walletBalance = walletBalance,
        earnState = earnState,
        onLanguageClick = onLanguageClick,
        onSupportClick = onSupportClick,
        onWalletClick = onWalletClick,
        onAvatarClick = onAvatarClick,
        onMyContestClick = onMyContestClick,
        onGameModeClick = onGameModeClick
    )
}

@Composable
fun PlayScreen(
    username: String,
    walletBalance: Double,
    gameModes: List<GameMode>,
    marqueeText: String = "",
    onLanguageClick: () -> Unit = {},
    onSupportClick: () -> Unit = {},
    onWalletClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {},
    onMyContestClick: (String) -> Unit = {},
    onGameModeClick: (GameMode) -> Unit = {}
) {
    PlayScreenContent(
        username = username,
        walletBalance = walletBalance,
        earnState = EarnUiState(gameModes = gameModes, marqueeText = marqueeText),
        onLanguageClick = onLanguageClick,
        onSupportClick = onSupportClick,
        onWalletClick = onWalletClick,
        onAvatarClick = onAvatarClick,
        onMyContestClick = onMyContestClick,
        onGameModeClick = onGameModeClick
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayScreenContent(
    username: String,
    walletBalance: Double,
    earnState: EarnUiState,
    onLanguageClick: () -> Unit,
    onSupportClick: () -> Unit,
    onWalletClick: () -> Unit,
    onAvatarClick: () -> Unit,
    onMyContestClick: (String) -> Unit,
    onGameModeClick: (GameMode) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 6 })
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % 6
            pagerState.animateScrollToPage(nextPage)
        }
    }

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

        // Announcement Section
        if (earnState.marqueeText.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(PurpleDark, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📢", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                // Dynamic announcement from ViewModel (Firebase)
                Text(
                    text = earnState.marqueeText,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Auto-Sliding Banner
            item {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp) // Adjusted height
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) { page ->
                    val imageRes = when (page) {
                        0 -> R.drawable.banner_1
                        1 -> R.drawable.banner_2
                        2 -> R.drawable.banner_3
                        3 -> R.drawable.banner_4
                        4 -> R.drawable.banner_5
                        else -> R.drawable.banner_6
                    }
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = "Banner ${page + 1}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // My Contests
            item {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "MY CONTESTS", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("✅", fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(10.dp))
                ContestTilesRow(onClick = onMyContestClick)
                Spacer(modifier = Modifier.height(22.dp))
            }

            // EXCLUSIVE
            item {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "EXCLUSIVE", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("✅", fontSize = 14.sp)
                }
                Text(
                    text = "Big Winnings For All",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Exclusive grid - 3 columns (Arise Battle screenshot jaisa).
            // Naye banners me name ka red band already baked hai, isliye full image hi dikhate hain.
            items(earnState.gameModes.chunked(3).size) { rowIdx ->
                val rowModes = earnState.gameModes.chunked(3)[rowIdx]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowModes.forEach { mode ->
                        GameModeBannerCard(
                            gameMode = mode,
                            imageRes = gameModeImageRes(mode.name),
                            onClick = { onGameModeClick(mode) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    repeat(3 - rowModes.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

/**
 * EXCLUSIVE grid card - uploaded banner full image (label band image me hi hai).
 * Fallback: agar image nahi mili to name ke saath dark card.
 */
@Composable
private fun GameModeBannerCard(
    gameMode: GameMode,
    imageRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(0.9f)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF14081F))
            .clickable { onClick() }
    ) {
        if (imageRes != 0) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = gameMode.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = gameMode.name,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = gameMode.label,
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun ContestTilesRow(onClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ContestTile("Ongoing", Icons.Filled.PlayArrow, { onClick("ongoing") }, Modifier.weight(1f))
        ContestTile("Upcoming", Icons.Filled.Schedule, { onClick("upcoming") }, Modifier.weight(1f))
        ContestTile("Completed", Icons.Filled.CheckCircle, { onClick("completed") }, Modifier.weight(1f))
    }
}

@Composable
private fun ContestTile(label: String, icon: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color(0xFF1E1E2E), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = Color.Red, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, color = Color.White, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlayScreenContentPreview() {
    val sampleGameModes = listOf(
        GameMode(id = "1", name = "Battle Royale", label = "Classic"),
        GameMode(id = "2", name = "Team Deathmatch", label = "Fast Paced")
    )
    FireChampTheme {
        PlayScreenContent(
            username = "PlayerOne",
            walletBalance = 150.0,
            earnState = EarnUiState(gameModes = sampleGameModes),
            onLanguageClick = {},
            onSupportClick = {},
            onWalletClick = {},
            onAvatarClick = {},
            onMyContestClick = {},
            onGameModeClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlayScreenPreview() {
    val sampleGameModes = listOf(
        GameMode(id = "1", name = "Battle Royale", label = "Classic"),
        GameMode(id = "2", name = "Team Deathmatch", label = "Fast Paced")
    )
    FireChampTheme {
        PlayScreen(
            username = "PlayerOne",
            walletBalance = 150.0,
            gameModes = sampleGameModes
        )
    }
}
