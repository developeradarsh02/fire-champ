package com.firechamp.tournament.presentation.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firechamp.tournament.presentation.theme.BlackBackground
import com.firechamp.tournament.presentation.theme.ErrorRed
import com.firechamp.tournament.presentation.theme.OrangeWarning
import com.firechamp.tournament.presentation.theme.PurpleDeep
import com.firechamp.tournament.presentation.theme.PurplePrimary
import com.firechamp.tournament.presentation.theme.RedAccent
import com.firechamp.tournament.presentation.theme.WhiteSecondary
import com.firechamp.tournament.presentation.theme.WhiteText

/**
 * Reusable UI components for Loading/Error/Empty states (Task 12).
 *
 * Sab screens me use hote hain jab data fetch ho raha ho, fail ho ya empty ho.
 */

/**
 * Loading indicator - full screen ke liye.
 */
@Composable
fun FullScreenLoading(message: String = "Loading...") {
    Box(modifier = Modifier.fillMaxSize().background(BlackBackground), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = PurplePrimary, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = message, color = WhiteSecondary, fontSize = 13.sp)
        }
    }
}

/**
 * Skeleton loader - better UX than plain spinner for lists.
 */
@Composable
fun ShimmerCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(PurpleDeep.copy(alpha = 0.4f))
    )
}

/**
 * Error state with retry button.
 */
@Composable
fun ErrorStateView(
    title: String = "Something went wrong",
    message: String = "Please check your connection and try again",
    onRetry: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize().background(BlackBackground), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Box(
                modifier = Modifier.size(80.dp).clip(CircleShape).background(ErrorRed.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Filled.ErrorOutline, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(40.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = title, color = WhiteText, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = message, color = WhiteSecondary, fontSize = 12.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(PurplePrimary)
                    .clickable { onRetry() }
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = null, tint = WhiteText, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "RETRY", color = WhiteText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * No internet state.
 */
@Composable
fun NoInternetView(onRetry: () -> Unit = {}) {
    ErrorStateView(
        title = "No Internet Connection",
        message = "Please check your WiFi or mobile data and try again",
        onRetry = onRetry
    )
}

/**
 * Generic empty state with custom icon + message.
 */
@Composable
fun GenericEmptyState(
    icon: ImageVector = Icons.Filled.Inbox,
    title: String,
    message: String = "",
    iconTint: Color = WhiteSecondary,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize().background(BlackBackground), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Box(
                modifier = Modifier.size(80.dp).clip(CircleShape).background(PurpleDeep.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(40.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = title, color = WhiteText, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            if (message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = message, color = WhiteSecondary, fontSize = 12.sp, textAlign = TextAlign.Center)
            }
        }
    }
}

// Bottom of file - redundant extension removed (using real import now)