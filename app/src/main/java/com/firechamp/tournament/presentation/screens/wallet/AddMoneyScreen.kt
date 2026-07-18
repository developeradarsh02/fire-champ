package com.firechamp.tournament.presentation.screens.wallet

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.firechamp.tournament.R
import com.firechamp.tournament.presentation.components.PrimaryButton
import com.firechamp.tournament.presentation.theme.BlackBackground
import com.firechamp.tournament.presentation.theme.ErrorRed
import com.firechamp.tournament.presentation.theme.GoldCoin
import com.firechamp.tournament.presentation.theme.GreenSuccess
import com.firechamp.tournament.presentation.theme.GreyHint
import com.firechamp.tournament.presentation.theme.PurpleDeep
import com.firechamp.tournament.presentation.theme.PurplePrimary
import com.firechamp.tournament.presentation.theme.RedAccent
import com.firechamp.tournament.presentation.theme.WhiteSecondary
import com.firechamp.tournament.presentation.theme.WhiteText
import com.firechamp.tournament.presentation.viewmodel.wallet.WalletViewModel

/**
 * Add Money Screen - 3 payment options:
 *  1. **Razorpay** (recommended, instant) - opens UPI/Card/NetBanking/Wallet
 *  2. **Direct QR scan** - user's static QR (manual verification by admin)
 *  3. **Generate QR** - app creates dynamic UPI QR (instant auto-credit)
 */
@Composable
fun AddMoneyScreen(
    onBack: () -> Unit,
    onRazorpayCheckout: (orderId: String, key: String, amount: Int) -> Unit,
    onQrGenerate: (amount: Int) -> Unit,
    viewModel: WalletViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBackground)
            .verticalScroll(rememberScrollState())
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
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = WhiteText)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Add Money", color = WhiteText, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(16.dp))

            // Current balance card
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    .background(PurpleDeep).padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Current Balance", color = WhiteSecondary, fontSize = 13.sp, modifier = Modifier.weight(1f))
                Text(
                    text = "${String.format("%.2f", state.totalBalance)} coins",
                    color = GoldCoin, fontSize = 14.sp, fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Amount input
            Text(text = "ENTER AMOUNT", color = WhiteSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.selectedAmount,
                onValueChange = viewModel::onAmountChange,
                placeholder = { Text(text = "₹ Amount (Min ₹10)", color = GreyHint, fontSize = 15.sp) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black, unfocusedTextColor = Color.Black,
                    focusedBorderColor = if (state.amountError != null) ErrorRed else Color.Transparent,
                    unfocusedBorderColor = if (state.amountError != null) ErrorRed else Color.Transparent,
                    cursorColor = Color.Black
                ),
                textStyle = TextStyle(color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Medium),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            if (state.amountError != null) {
                Text(text = state.amountError!!, color = ErrorRed, fontSize = 12.sp, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick amount chips
            Text(text = "QUICK SELECT", color = WhiteSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(50, 100, 200, 500).forEach { amount ->
                    AmountChip(
                        amount = amount,
                        isSelected = state.selectedAmount == amount.toString(),
                        onClick = { viewModel.onPresetAmountSelect(amount) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bonus table
            Text(text = "DEPOSIT BONUS", color = RedAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            Spacer(modifier = Modifier.height(8.dp))
            BonusTable()

            Spacer(modifier = Modifier.height(24.dp))

            // ============== OPTION 1: Razorpay (instant) ==============
            PaymentMethodCard(
                icon = Icons.Filled.MonetizationOn,
                title = "Pay with Razorpay ⚡",
                description = "UPI, Card, NetBanking, Wallet - Instant credit",
                badge = "RECOMMENDED",
                badgeColor = GreenSuccess,
                onClick = {
                    val amount = state.selectedAmount.toIntOrNull()
                    if (amount != null && viewModel.validateAmount()) {
                        viewModel.processPayment(amount) { orderId, key ->
                            onRazorpayCheckout(orderId, key, amount)
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ============== OPTION 2: Direct QR scan (user's static QR) ==============
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    .background(PurpleDeep).padding(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF5F5F5)).padding(4.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.payment_qr_placeholder),
                            contentDescription = "Payment QR Code",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.QrCode2, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Scan & Pay (Manual)", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Scan QR with any UPI app → Upload screenshot for verification (1-2 hrs)",
                            color = Color(0xFF555555), fontSize = 11.sp, lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ============== OPTION 3: Generate dynamic QR (instant) ==============
            Box(
                modifier = Modifier.fillMaxWidth().height(46.dp)
                    .clip(RoundedCornerShape(23.dp)).background(PurpleDeep).clickable {
                        val amount = state.selectedAmount.toIntOrNull()
                        if (amount != null && viewModel.validateAmount()) {
                            onQrGenerate(amount)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.QrCode2, contentDescription = null, tint = WhiteText, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "GENERATE DYNAMIC QR", color = WhiteText, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun AmountChip(
    amount: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) PurplePrimary else PurpleDeep
    Box(
        modifier = modifier.height(48.dp).clip(RoundedCornerShape(24.dp)).background(bgColor).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = "₹$amount", color = WhiteText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun BonusTable() {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(PurpleDeep)) {
        Row(modifier = Modifier.fillMaxWidth().background(PurplePrimary).padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(text = "Deposit", color = WhiteText, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(text = "Bonus", color = WhiteText, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(text = "Total", color = WhiteText, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        }
        listOf(
            Triple("₹50", "+5", "₹55"),
            Triple("₹100", "+15", "₹115"),
            Triple("₹200", "+30", "₹230"),
            Triple("₹500", "+100", "₹600"),
            Triple("₹1000", "+200", "₹1200")
        ).forEach { (dep, bonus, total) ->
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 7.dp)) {
                Text(text = dep, color = WhiteText, fontSize = 12.sp, modifier = Modifier.weight(1f))
                Text(text = bonus, color = GreenSuccess, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(text = total, color = GoldCoin, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun PaymentMethodCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    badge: String,
    badgeColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PurplePrimary)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(WhiteText.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = WhiteText, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = title, color = WhiteText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(4.dp))
                            .background(badgeColor).padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = badge, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = description, color = WhiteText.copy(alpha = 0.85f), fontSize = 11.sp)
            }
        }
    }
}