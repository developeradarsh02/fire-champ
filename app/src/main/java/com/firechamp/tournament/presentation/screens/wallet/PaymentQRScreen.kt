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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.firechamp.tournament.presentation.theme.BlackBackground
import com.firechamp.tournament.presentation.theme.ErrorRed
import com.firechamp.tournament.presentation.theme.GoldCoin
import com.firechamp.tournament.presentation.theme.GreenSuccess
import com.firechamp.tournament.presentation.theme.OrangeWarning
import com.firechamp.tournament.presentation.theme.PurpleDeep
import com.firechamp.tournament.presentation.theme.PurplePrimary
import com.firechamp.tournament.presentation.theme.RedAccent
import com.firechamp.tournament.presentation.theme.WhiteSecondary
import com.firechamp.tournament.presentation.theme.WhiteText
import com.firechamp.tournament.presentation.utils.QRCodeGenerator
import com.firechamp.tournament.presentation.viewmodel.wallet.PaymentStatus
import com.firechamp.tournament.presentation.viewmodel.wallet.WalletViewModel

/**
 * Payment QR Screen - UPI payment ke liye QR code dikhata hai.
 *
 * Real me ye Razorpay/Cashfree/PayU integration hoga. Abhi mock UPI URL generate
 * karke QR code show karta hai (ZXing library se).
 *
 * UI:
 *   - Top: app logo + "Paying to Fire Champ"
 *   - Amount display
 *   - QR code image
 *   - "Scan QR Code to Pay" + subtitle
 *   - Countdown timer (red)
 *   - "Secure Payment" + lock icon
 *   - "I've Paid" button (green)
 *   - Footer: "Powered by UPI"
 *
 * State:
 *   - PROCESSING: QR shown, timer running
 *   - EXPIRED: timer 0, retry option
 *   - SUCCESS/FAILED: navigate to PaymentStatusScreen
 */
@Composable
fun PaymentQRScreen(
    onBack: () -> Unit,
    onPaymentComplete: () -> Unit,
    onRetry: () -> Unit,
    viewModel: WalletViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // QR code generate karte hain (remember - sirf ek baar)
    val qrBitmap = remember(state.pendingAmount) {
        val upiUrl = QRCodeGenerator.buildUpiPaymentUrl(
            upiId = "firechamp@upi",     // TODO: Task 14 me real UPI ID
            payeeName = "Fire Champ",
            amount = state.pendingAmount
        )
        QRCodeGenerator.generate(text = upiUrl, sizePx = 512)
    }

    // Auto-navigate on success
    LaunchedEffect(state.paymentStatus) {
        if (state.paymentStatus == PaymentStatus.SUCCESS) {
            onPaymentComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable {
                        viewModel.onCancelPayment()
                        onBack()
                    },
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
                text = "Payment",
                color = WhiteText,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // App logo (placeholder - fire icon)
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(PurplePrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🔥", fontSize = 30.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Paying to",
                color = WhiteSecondary,
                fontSize = 12.sp
            )
            Text(
                text = "Fire Champ",
                color = WhiteText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Amount display
            Text(
                text = "AMOUNT",
                color = WhiteSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )
            Text(
                text = "₹${String.format("%.2f", state.pendingAmount)}",
                color = GoldCoin,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // QR code
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                if (state.paymentStatus == PaymentStatus.EXPIRED) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "⏰", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "QR Expired",
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "Payment QR Code",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scan instruction
            Text(
                text = "Scan QR Code to Pay",
                color = WhiteText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Use any UPI enabled application",
                color = WhiteSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Countdown timer
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(PurpleDeep)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Schedule,
                    contentDescription = null,
                    tint = if (state.countdownSeconds < 60) ErrorRed else OrangeWarning,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = formatCountdown(state.countdownSeconds),
                    color = if (state.countdownSeconds < 60) ErrorRed else WhiteText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "remaining",
                    color = WhiteSecondary,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Secure payment
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = GreenSuccess,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Secure Payment",
                    color = GreenSuccess,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action buttons
            if (state.paymentStatus == PaymentStatus.EXPIRED) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(RedAccent)
                        .clickable { onRetry() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "GENERATE NEW QR",
                        color = WhiteText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else if (state.isPaymentProcessing) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(PurplePrimary.copy(alpha = 0.5f)),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        color = WhiteText,
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Verifying payment...",
                        color = WhiteText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(GreenSuccess)
                        .clickable { viewModel.onPaymentConfirmed() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "I'VE PAID",
                        color = WhiteText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer
            Text(
                text = "Powered by UPI",
                color = WhiteSecondary,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "© 2026 Fire Champ. All rights reserved.",
                color = WhiteSecondary,
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

private fun formatCountdown(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", mins, secs)
}
