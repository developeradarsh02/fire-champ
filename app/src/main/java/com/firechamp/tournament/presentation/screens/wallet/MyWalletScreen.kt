package com.firechamp.tournament.presentation.screens.wallet

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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.firechamp.tournament.data.model.Transaction
import com.firechamp.tournament.presentation.theme.BlackBackground
import com.firechamp.tournament.presentation.theme.GoldCoin
import com.firechamp.tournament.presentation.theme.GreenSuccess
import com.firechamp.tournament.presentation.theme.PurpleDeep
import com.firechamp.tournament.presentation.theme.PurplePrimary
import com.firechamp.tournament.presentation.theme.RedAccent
import com.firechamp.tournament.presentation.theme.WhiteSecondary
import com.firechamp.tournament.presentation.theme.WhiteText
import com.firechamp.tournament.presentation.viewmodel.wallet.WalletViewModel

/**
 * My Wallet Screen - PDF Task 6 spec ke according.
 *
 * Card layout (single dark purple card with all balance breakdown):
 *   - TOTAL BALANCE: 0.00 Coins (large bold)
 *   - Deposited: 0.00 Coins
 *   - Winning: 0.00 Coins (withdrawable)
 *   - Earnings: 0 Coins
 *   - PAYOUTS: 0 Coins
 * + ADD COINS + REDEEM COINS buttons (right side, stacked)
 *
 * Below card: WALLET HISTORY (transaction list)
 */
@Composable
fun MyWalletScreen(
    onBack: () -> Unit,
    onAddMoneyClick: () -> Unit,
    onRedeemClick: () -> Unit,
    viewModel: WalletViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.refreshBalance() }

    Column(
        modifier = Modifier.fillMaxSize().background(BlackBackground)
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = WhiteText
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "My Wallet", color = WhiteText, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Balance breakdown card
            item {
                BalanceBreakdownCard(
                    totalBalance = state.totalBalance,
                    depositedBalance = state.depositedBalance,
                    winningBalance = state.winningBalance,
                    earnings = state.earnings,
                    payouts = state.payouts,
                    onAddMoneyClick = onAddMoneyClick,
                    onRedeemClick = onRedeemClick
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Wallet history section
            item {
                Text(
                    text = "WALLET HISTORY",
                    color = WhiteText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (state.transactions.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                        Text(text = "No history found", color = WhiteSecondary, fontSize = 13.sp)
                    }
                }
            } else {
                items(state.transactions, key = { it.id }) { transaction ->
                    TransactionRow(transaction = transaction)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun BalanceBreakdownCard(
    totalBalance: Double,
    depositedBalance: Double,
    winningBalance: Double,
    earnings: Double,
    payouts: Double,
    onAddMoneyClick: () -> Unit,
    onRedeemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(PurpleDeep)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: balance labels + values
        Column(modifier = Modifier.weight(1f)) {
            Text("TOTAL BALANCE", color = WhiteSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${String.format("%.2f", totalBalance)} Coins",
                color = WhiteText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Text("DEPOSITED", color = WhiteSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
            Text("${String.format("%.2f", depositedBalance)} Coins", color = WhiteText, fontSize = 13.sp, fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.height(6.dp))
            Text("WINNING", color = WhiteSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
            Text("${String.format("%.2f", winningBalance)} Coins", color = GoldCoin, fontSize = 13.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(6.dp))
            Text("EARNINGS", color = WhiteSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.MonetizationOn, contentDescription = null, tint = GoldCoin, modifier = Modifier.size(11.dp))
                Spacer(modifier = Modifier.width(2.dp))
                Text("${earnings.toInt()}", color = WhiteText, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text("PAYOUTS", color = WhiteSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
            Text("${payouts.toInt()} Coins", color = WhiteText, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Right: action buttons
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .width(110.dp).height(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(PurplePrimary)
                    .clickable { onAddMoneyClick() },
                contentAlignment = Alignment.Center
            ) {
                Text("ADD COINS", color = WhiteText, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            }
            Box(
                modifier = Modifier
                    .width(110.dp).height(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(RedAccent)
                    .clickable { onRedeemClick() },
                contentAlignment = Alignment.Center
            ) {
                Text("REDEEM COINS", color = WhiteText, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            }
        }
    }
}

@Composable
private fun TransactionRow(transaction: Transaction) {
    val isCredit = transaction.amount > 0
    val iconColor = if (isCredit) GreenSuccess else RedAccent
    val amountColor = if (isCredit) GreenSuccess else RedAccent
    val amountPrefix = if (isCredit) "+" else ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(PurpleDeep)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background(iconColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isCredit) Icons.Filled.ArrowDownward else Icons.Filled.ArrowUpward,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(transaction.description, color = WhiteText, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1)
            Text(transaction.type.name.replace("_", " "), color = WhiteSecondary, fontSize = 10.sp)
        }
        Text(
            text = "$amountPrefix${String.format("%.2f", kotlin.math.abs(transaction.amount))}",
            color = amountColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}