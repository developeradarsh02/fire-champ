package com.firechamp.tournament.presentation.screens.play

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.firechamp.tournament.data.model.Tournament
import com.firechamp.tournament.presentation.components.PlayerResultRow
import com.firechamp.tournament.presentation.components.TableSectionHeader
import com.firechamp.tournament.presentation.components.WinnerRow
import com.firechamp.tournament.presentation.theme.BlackBackground
import com.firechamp.tournament.presentation.theme.GoldCoin
import com.firechamp.tournament.presentation.theme.GreyDivider
import com.firechamp.tournament.presentation.theme.OrangeWarning
import com.firechamp.tournament.presentation.theme.PurpleDark
import com.firechamp.tournament.presentation.theme.PurpleDeep
import com.firechamp.tournament.presentation.theme.PurplePrimary
import com.firechamp.tournament.presentation.theme.WhiteSecondary
import com.firechamp.tournament.presentation.theme.WhiteText
import com.firechamp.tournament.presentation.viewmodel.play.MatchResultViewModel

/**
 * Match Result Screen - specific tournament ka detail + full results.
 *
 * Layout (PDF spec ke according):
 *   Top bar: ← back | "Match Result"
 *   Banner (gradient with rules overlay)
 *   Title + Match ID (orange)
 *   "Organised on [date] [time]" (white card)
 *   2-col cards: Winning Prize | Per Kill
 *   1-col card: Entry Fee
 *   "Winner" section (purple header) + top winner row
 *   "Match Result" section (purple header) + LazyColumn of all players
 */
@Composable
fun MatchResultScreen(
    tournamentId: String,
    onBack: () -> Unit,
    viewModel: MatchResultViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(tournamentId) {
        viewModel.loadTournament(tournamentId)
    }

    val tournament = state.tournament
    if (tournament == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BlackBackground),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = PurplePrimary)
            } else {
                Text(
                    text = state.errorMessage ?: "Tournament not found",
                    color = WhiteText,
                    fontSize = 14.sp
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBackground),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 20.dp)
    ) {
        // Top bar
        item {
            MatchResultTopBar(onBack = onBack)
        }

        // Banner
        item {
            MatchResultBanner(tournament = tournament)
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Title + match ID
        item {
            Text(
                text = tournament.title,
                color = OrangeWarning,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Organised info card
        item {
            InfoCard(
                icon = Icons.Filled.CalendarToday,
                text = "Organised on ${tournament.dateTime}",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Winning Prize + Per Kill (2 side-by-side)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CoinInfoCard(
                    title = "Winning Prize :",
                    value = tournament.prizePool,
                    modifier = Modifier.weight(1f)
                )
                CoinInfoCard(
                    title = "Per Kill :",
                    value = tournament.perKill,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Entry Fee
        item {
            CoinInfoCard(
                title = "Entry Fee :",
                value = tournament.entryFee,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Winner section
        if (tournament.winners.isNotEmpty()) {
            item {
                TableSectionHeader(text = "Winner")
            }
            item {
                WinnerRow(winner = tournament.winners.first())
            }
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // Match Result section header
        if (tournament.results.isNotEmpty()) {
            item {
                TableSectionHeader(text = "Match Result")
                // Column headers row
                PlayerResultRow(
                    rank = 0,
                    playerName = "Player Name",
                    kills = 0,
                    winning = 0,
                    isHeader = true,
                    backgroundColor = PurpleDark,
                    textColor = WhiteText
                )
            }

            // Results list
            items(
                items = tournament.results,
                key = { it.rank }
            ) { result ->
                // Alternate row colors for readability
                val bgColor = if (result.rank % 2 == 0) Color(0xFFF5F5F5) else Color.White
                PlayerResultRow(
                    result = result,
                    backgroundColor = bgColor,
                    textColor = Color.Black
                )
                // Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(GreyDivider)
                )
            }
        }
    }
}

@Composable
private fun MatchResultTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BlackBackground)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = WhiteText
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Match Result",
            color = WhiteText,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MatchResultBanner(tournament: Tournament) {
    val gradient = when (tournament.mode) {
        com.firechamp.tournament.data.model.TournamentMode.SOLO -> listOf(
            Color(0xFF1B5E20), Color(0xFFFF6F00)
        )
        com.firechamp.tournament.data.model.TournamentMode.DUO -> listOf(
            Color(0xFF4A148C), Color(0xFFD32F2F)
        )
        com.firechamp.tournament.data.model.TournamentMode.SQUAD -> listOf(
            Color(0xFF0D47A1), Color(0xFF6A1B9A)
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(Brush.horizontalGradient(gradient)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RULES",
                    color = WhiteText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "ID LVL 40+",
                color = WhiteText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "SCREEN RECORDING MANDATORY",
                color = WhiteText.copy(alpha = 0.9f),
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "M79 & DOUBLE VICTOR BANNER",
                color = WhiteText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = Color.Black,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun CoinInfoCard(
    title: String,
    value: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color.Black,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Filled.MonetizationOn,
            contentDescription = null,
            tint = GoldCoin,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value.toString(),
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
