package com.firechamp.tournament.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Social media ke liye circular icon button.
 * Instagram/Telegram/WhatsApp ke liye use hoga (Earn screen ke bottom me).
 */
@Composable
fun SocialIconButton(
    icon: ImageVector,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    iconTint: Color = Color.White
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(size * 0.55f)
        )
    }
}

/**
 * Pre-defined social icons (jab tak real brand icons nahi aate).
 * Real brand assets (Instagram gradient, WhatsApp green, Telegram blue)
 * later me replace kiye ja sakte hain.
 */
object SocialIcons {
    val Instagram: ImageVector = Icons.Filled.CameraAlt
    val Telegram: ImageVector = Icons.AutoMirrored.Filled.Send
    val WhatsApp: ImageVector = Icons.AutoMirrored.Filled.Chat

    // Brand colors
    val InstagramColor = Color(0xFFE4405F)
    val TelegramColor = Color(0xFF0088CC)
    val WhatsAppColor = Color(0xFF25D366)
}
