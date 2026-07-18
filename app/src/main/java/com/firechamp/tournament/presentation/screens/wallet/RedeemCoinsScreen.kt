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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.firechamp.tournament.presentation.components.PillTextField
import com.firechamp.tournament.presentation.components.PrimaryButton
import com.firechamp.tournament.presentation.theme.BlackBackground
import com.firechamp.tournament.presentation.theme.ErrorRed
import com.firechamp.tournament.presentation.theme.GoldCoin
import com.firechamp.tournament.presentation.theme.GreyHint
import com.firechamp.tournament.presentation.theme.GreenSuccess
import com.firechamp.tournament.presentation.theme.PurpleDeep
import com.firechamp.tournament.presentation.theme.PurplePrimary
import com.firechamp.tournament.presentation.theme.WhiteSecondary
import com.firechamp.tournament.presentation.theme.WhiteText
import com.firechamp.tournament.presentation.viewmodel.wallet.WalletViewModel
import com.firechamp.tournament.presentation.viewmodel.wallet.WithdrawalMethod

/**
 * Redeem Coins Screen (Task 8) - Winning balance withdraw karta hai.
 *
 * Important: Sirf WINNING balance withdraw ho sakta hai (deposited nahi).
 *
 * Layout:
 *   - Winning balance display (prominent)
 *   - Min withdrawal info
 *   - Amount input
 *   - Method tabs: UPI / Bank Account
 *   - KYC details (jo bhi method select kiya)
 *   - Confirm button
 *   - Processing info
 */
@Composable
fun RedeemCoinsScreen(
    onBack: () -> Unit,
    viewModel: WalletViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

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
            Text(
                text = "Redeem Coins",
                color = WhiteText,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Winning balance card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PurpleDeep)
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "WITHDRAWABLE BALANCE",
                        color = WhiteSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.MonetizationOn,
                            contentDescription = null,
                            tint = GoldCoin,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = String.format("%.2f", state.winningBalance),
                            color = WhiteText,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Coins",
                            color = WhiteSecondary,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Note: Only winning balance can be withdrawn. Deposited amount is non-withdrawable.",
                        color = WhiteSecondary,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Min withdrawal info
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                    .background(PurpleDeep.copy(alpha = 0.5f)).padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Schedule,
                    contentDescription = null,
                    tint = GreenSuccess,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Minimum withdrawal: ₹100  •  Processing time: 30 minutes",
                    color = WhiteSecondary,
                    fontSize = 11.sp
                )
            }

            // Success message
            state.lastWithdrawalMessage?.let { msg ->
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                        .background(GreenSuccess.copy(alpha = 0.15f))
                        .padding(12.dp)
                ) {
                    Text(text = msg, color = GreenSuccess, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Amount input
            Text(
                text = "WITHDRAWAL AMOUNT",
                color = WhiteSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.withdrawalAmount,
                onValueChange = viewModel::onWithdrawalAmountChange,
                placeholder = { Text(text = "₹ Amount", color = GreyHint, fontSize = 15.sp) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = if (state.withdrawalError != null) ErrorRed else Color.Transparent,
                    unfocusedBorderColor = if (state.withdrawalError != null) ErrorRed else Color.Transparent,
                    cursorColor = Color.Black
                ),
                textStyle = TextStyle(color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Medium),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            if (state.withdrawalError != null) {
                Text(
                    text = state.withdrawalError!!,
                    color = ErrorRed,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Method tabs
            Text(
                text = "WITHDRAW VIA",
                color = WhiteSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MethodTab(
                    label = "UPI ID",
                    icon = Icons.Filled.AttachMoney,
                    selected = state.withdrawalMethod == WithdrawalMethod.UPI,
                    onClick = { viewModel.onWithdrawalMethodSelect(WithdrawalMethod.UPI) },
                    modifier = Modifier.weight(1f)
                )
                MethodTab(
                    label = "Bank Account",
                    icon = Icons.Filled.AccountBalance,
                    selected = state.withdrawalMethod == WithdrawalMethod.BANK,
                    onClick = { viewModel.onWithdrawalMethodSelect(WithdrawalMethod.BANK) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // KYC details based on method
            if (state.withdrawalMethod == WithdrawalMethod.UPI) {
                PillTextField(
                    value = state.upiId,
                    onValueChange = viewModel::onUpiIdChange,
                    placeholder = "UPI ID (e.g. name@upi)"
                )
            } else {
                Column {
                    PillTextField(
                        value = state.accountHolderName,
                        onValueChange = viewModel::onAccountHolderChange,
                        placeholder = "Account Holder Name (as per bank)"
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    PillTextField(
                        value = state.accountNumber,
                        onValueChange = viewModel::onAccountNumberChange,
                        placeholder = "Bank Account Number"
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    PillTextField(
                        value = state.ifscCode,
                        onValueChange = viewModel::onIfscChange,
                        placeholder = "IFSC Code"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit button
            PrimaryButton(
                text = "CONFIRM WITHDRAWAL",
                onClick = { viewModel.submitWithdrawal() },
                enabled = state.winningBalance >= 100
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun MethodTab(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(if (selected) PurplePrimary else PurpleDeep)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = WhiteText,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = WhiteText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}