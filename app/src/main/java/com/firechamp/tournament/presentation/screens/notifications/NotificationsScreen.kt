package com.firechamp.tournament.presentation.screens.notifications

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.firechamp.tournament.data.model.notification.Notification
import com.firechamp.tournament.data.model.notification.NotificationType
import com.firechamp.tournament.presentation.components.SubScreenTopBar
import com.firechamp.tournament.presentation.theme.BlackBackground
import com.firechamp.tournament.presentation.theme.GoldCoin
import com.firechamp.tournament.presentation.theme.GreenSuccess
import com.firechamp.tournament.presentation.theme.OrangeWarning
import com.firechamp.tournament.presentation.theme.PurpleDeep
import com.firechamp.tournament.presentation.theme.PurplePrimary
import com.firechamp.tournament.presentation.theme.RedAccent
import com.firechamp.tournament.presentation.theme.WhiteSecondary
import com.firechamp.tournament.presentation.theme.WhiteText
import com.firechamp.tournament.presentation.viewmodel.notifications.NotificationsViewModel

/**
 * In-app Notification Center - Task 10.
 *
 * Real me ye FCM se populate hoga, abhi mock data.
 * Bell icon click se open hota hai (TopHeader me add karenge).
 */
@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { /* auto-loaded via Flow in init */ }

    Column(modifier = Modifier.fillMaxSize().background(BlackBackground)) {
        SubScreenTopBar(
            title = "Notifications",
            onBack = onBack,
            trailing = {
                if (state.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(PurplePrimary.copy(alpha = 0.2f))
                            .clickable { viewModel.markAllAsRead() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.DoneAll, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Mark all read", color = PurplePrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        )

        if (state.notifications.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Filled.Notifications, contentDescription = null, tint = WhiteSecondary, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "No notifications yet", color = WhiteText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.notifications, key = { it.id }) { notification ->
                    NotificationItem(
                        notification = notification,
                        onClick = { viewModel.markAsRead(notification.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(notification: Notification, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (notification.isRead) PurpleDeep.copy(alpha = 0.5f) else PurpleDeep)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Icon
        Box(
            modifier = Modifier.size(38.dp).clip(CircleShape).background(notificationColor(notification.type).copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = notificationIcon(notification.type), contentDescription = null, tint = notificationColor(notification.type), modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = notification.title, color = WhiteText, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                if (!notification.isRead) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(RedAccent))
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = notification.body, color = WhiteSecondary, fontSize = 12.sp, lineHeight = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = formatTimeAgo(notification.timestamp), color = WhiteSecondary.copy(alpha = 0.6f), fontSize = 10.sp)
        }
    }
}

private fun notificationIcon(type: NotificationType): ImageVector = when (type) {
    NotificationType.MATCH_REMINDER -> Icons.Filled.Schedule
    NotificationType.ROOM_ID_RELEASED -> Icons.Filled.Star
    NotificationType.RESULT_DECLARED -> Icons.Filled.EmojiEvents
    NotificationType.WITHDRAWAL_UPDATE -> Icons.Filled.MonetizationOn
    NotificationType.GENERAL_ANNOUNCEMENT -> Icons.Filled.Notifications
}

private fun notificationColor(type: NotificationType): Color = when (type) {
    NotificationType.MATCH_REMINDER -> OrangeWarning
    NotificationType.ROOM_ID_RELEASED -> PurplePrimary
    NotificationType.RESULT_DECLARED -> GreenSuccess
    NotificationType.WITHDRAWAL_UPDATE -> GoldCoin
    NotificationType.GENERAL_ANNOUNCEMENT -> WhiteSecondary
}

private fun formatTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val mins = diff / 60_000
    val hours = diff / 3_600_000
    val days = diff / 86_400_000
    return when {
        mins < 1 -> "Just now"
        mins < 60 -> "$mins min ago"
        hours < 24 -> "$hours hr ago"
        else -> "$days day ago"
    }
}