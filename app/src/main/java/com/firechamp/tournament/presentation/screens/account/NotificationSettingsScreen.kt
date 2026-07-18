package com.firechamp.tournament.presentation.screens.account

import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.firechamp.tournament.presentation.components.SubScreenTopBar
import com.firechamp.tournament.presentation.theme.BlackBackground
import com.firechamp.tournament.presentation.theme.PurpleDeep
import com.firechamp.tournament.presentation.theme.PurplePrimary
import com.firechamp.tournament.presentation.theme.RedAccent
import com.firechamp.tournament.presentation.theme.WhiteSecondary
import com.firechamp.tournament.presentation.theme.WhiteText
import com.firechamp.tournament.presentation.viewmodel.account.SettingsViewModel

/**
 * Notification preferences screen.
 * Each toggle persists to DataStore via SettingsViewModel.
 */
@Composable
fun NotificationSettingsScreen(
    onBack: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by settingsViewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBackground)
    ) {
        SubScreenTopBar(title = "Notifications", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Hero card: master switch
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                    .background(PurpleDeep)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (settings.pushEnabled) RedAccent
                                else Color.White.copy(alpha = 0.1f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (settings.pushEnabled) Icons.Filled.NotificationsActive
                            else Icons.Filled.NotificationsOff,
                            contentDescription = null,
                            tint = WhiteText,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Push Notifications",
                            color = WhiteText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (settings.pushEnabled) "Enabled" else "All notifications paused",
                            color = if (settings.pushEnabled) PurplePrimary else WhiteSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = settings.pushEnabled,
                        onCheckedChange = { settingsViewModel.setPushEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = WhiteText,
                            checkedTrackColor = RedAccent,
                            uncheckedThumbColor = WhiteSecondary,
                            uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "WHAT NOTIFICATIONS DO WE SEND",
                color = RedAccent,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            // 4 individual notification type toggles
            NotificationToggleItem(
                icon = Icons.Filled.Schedule,
                title = "Match Reminders",
                subtitle = "15 min before your match starts",
                checked = settings.matchReminders,
                enabled = settings.pushEnabled,
                onCheckedChange = { settingsViewModel.setMatchReminders(it) }
            )
            NotificationToggleItem(
                icon = Icons.Filled.LocalFireDepartment,
                title = "Room ID Released",
                subtitle = "When room ID & password are revealed",
                checked = settings.matchReminders,  // shares with match
                enabled = settings.pushEnabled,
                onCheckedChange = { /* shares with reminders */ }
            )
            NotificationToggleItem(
                icon = Icons.Filled.EmojiEvents,
                title = "Result Alerts",
                subtitle = "When your result is verified & prize credited",
                checked = settings.resultAlerts,
                enabled = settings.pushEnabled,
                onCheckedChange = { settingsViewModel.setResultAlerts(it) }
            )
            NotificationToggleItem(
                icon = Icons.Filled.Campaign,
                title = "Promotions & Announcements",
                subtitle = "Bonus codes, offers, admin announcements",
                checked = settings.promoAlerts,
                enabled = settings.pushEnabled,
                onCheckedChange = { settingsViewModel.setPromoAlerts(it) }
            )
            NotificationToggleItem(
                icon = Icons.Filled.Paid,
                title = "Withdrawal Updates",
                subtitle = "When your withdrawal is approved/paid",
                checked = settings.resultAlerts,
                enabled = settings.pushEnabled,
                onCheckedChange = { /* always on with results */ }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Info box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(14.dp)
            ) {
                Text(
                    text = "💡 Notifications are sent via Firebase Cloud Messaging (FCM). Make sure your phone has internet connection and battery optimization is disabled for Fire Champ.",
                    color = WhiteSecondary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun NotificationToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (checked && enabled) RedAccent.copy(alpha = 0.15f)
                    else Color.White.copy(alpha = 0.05f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (checked && enabled) RedAccent else WhiteSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = if (enabled) WhiteText else WhiteSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = WhiteSecondary,
                fontSize = 11.sp
            )
        }
        Switch(
            checked = checked && enabled,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = WhiteText,
                checkedTrackColor = RedAccent,
                uncheckedThumbColor = WhiteSecondary,
                uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
            )
        )
    }
}
