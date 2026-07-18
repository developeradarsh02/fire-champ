package com.firechamp.tournament.presentation.screens.play

import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.firechamp.tournament.data.model.Tournament
import com.firechamp.tournament.data.model.TournamentStatus
import com.firechamp.tournament.presentation.components.JoinConfirmationDialog
import com.firechamp.tournament.presentation.components.TournamentCard
import com.firechamp.tournament.presentation.theme.BlackBackground
import com.firechamp.tournament.presentation.theme.GoldCoin
import com.firechamp.tournament.presentation.theme.PurpleDeep
import com.firechamp.tournament.presentation.theme.PurplePrimary
import com.firechamp.tournament.presentation.theme.WhiteSecondary
import com.firechamp.tournament.presentation.theme.WhiteText
import com.firechamp.tournament.presentation.viewmodel.play.TournamentViewModel

/**
 * Tournament List Screen - specific game mode ke saare tournaments.
 *
 * Layout:
 *   Top bar: ← back | TITLE | wallet pill
 *   Tabs: ONGOING | UPCOMING | RESULTS
 *   Body: LazyColumn of TournamentCards
 *   (On JOIN tap) JoinConfirmationDialog
 */
@Composable
fun TournamentListScreen(
    gameModeId: String?,
    gameModeName: String,
    walletBalance: Double,
    onBack: () -> Unit,
    onTournamentClick: (Tournament) -> Unit = {},
    initialStatus: String? = null,
    viewModel: TournamentViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Load tournaments on first composition
    LaunchedEffect(gameModeId) {
        viewModel.loadTournaments(gameModeId, gameModeName)
        // Play screen ke Ongoing/Upcoming/Completed tile se aaye to wahi tab kholo
        when (initialStatus?.lowercase()) {
            "ongoing" -> viewModel.onTabSelected(com.firechamp.tournament.data.model.TournamentStatus.ONGOING)
            "upcoming" -> viewModel.onTabSelected(com.firechamp.tournament.data.model.TournamentStatus.UPCOMING)
            "completed" -> viewModel.onTabSelected(com.firechamp.tournament.data.model.TournamentStatus.RESULTS)
        }
    }

    // Handle successful join
    LaunchedEffect(state.lastJoinedTournamentId) {
        if (state.lastJoinedTournamentId != null) {
            Toast.makeText(
                context,
                "✅ Joined successfully! Room ID will unlock 10 min before match.",
                Toast.LENGTH_LONG
            ).show()
            viewModel.onNavigationComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBackground)
    ) {
        // Top bar
        TournamentListTopBar(
            title = state.gameModeName,
            walletBalance = walletBalance,
            onBack = onBack
        )

        // Tabs
        TabsRow(
            selectedTab = state.selectedTab,
            onTabSelected = viewModel::onTabSelected
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Content
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PurplePrimary)
                }
            }
            state.tournaments.isEmpty() -> {
                EmptyState(
                    message = when (state.selectedTab) {
                        TournamentStatus.ONGOING -> "No Ongoing Match Found."
                        TournamentStatus.UPCOMING -> "No Upcoming Match Found."
                        TournamentStatus.RESULTS -> "No Results Found."
                    }
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 0.dp,
                        top = 8.dp,
                        end = 0.dp,
                        bottom = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = state.tournaments,
                        key = { it.id }
                    ) { tournament ->
                        TournamentCard(
                            tournament = tournament,
                            onJoinClick = { viewModel.onJoinClick(tournament) },
                            onClick = { onTournamentClick(tournament) }
                        )
                    }
                }
            }
        }
    }

    // Join confirmation dialog
    if (state.showJoinDialog && state.pendingJoinTournament != null) {
        JoinConfirmationDialog(
            tournament = state.pendingJoinTournament!!,
            onConfirm = viewModel::onJoinConfirm,
            onCancel = viewModel::onJoinCancel
        )
    }
}

@Composable
private fun TournamentListTopBar(
    title: String,
    walletBalance: Double,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BlackBackground)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(20.dp))
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

        // Title
        Text(
            text = title,
            color = WhiteText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        // Wallet pill
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(androidx.compose.ui.graphics.Color.White)
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.MonetizationOn,
                contentDescription = null,
                tint = GoldCoin,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = String.format("%.2f", walletBalance),
                color = androidx.compose.ui.graphics.Color.Black,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TabsRow(
    selectedTab: TournamentStatus,
    onTabSelected: (TournamentStatus) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BlackBackground)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TournamentStatus.entries.forEach { tab ->
            val selected = tab == selectedTab
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = tab.name,
                    color = if (selected) WhiteText else WhiteSecondary,
                    fontSize = 13.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                // Underline
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(if (selected) PurplePrimary else PurpleDeep)
                )
            }
        }
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = WhiteText,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
