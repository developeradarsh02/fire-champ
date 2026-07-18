package com.firechamp.tournament.presentation.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firechamp.tournament.R
import com.firechamp.tournament.presentation.theme.PurpleDeep
import com.firechamp.tournament.presentation.theme.PurplePrimary
import com.firechamp.tournament.presentation.theme.RedAccent
import com.firechamp.tournament.presentation.theme.WhiteSecondary
import com.firechamp.tournament.presentation.theme.WhiteText

/**
 * Refer & Earn banner - red gradient with gift icon.
 * Used in Play screen and Account screen.
 */
@Composable
fun ReferAndEarnBanner(onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFFB71C1C), Color(0xFFFF6F00))
                )
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CardGiftcard,
                    contentDescription = null,
                    tint = WhiteText,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "REFER & EARN",
                    color = WhiteText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Share & earn ₹50 when your friend makes their first deposit",
                    color = WhiteText.copy(alpha = 0.9f),
                    fontSize = 11.sp,
                    maxLines = 2
                )
            }
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = "Share",
                tint = WhiteText,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Share buttons row - Share + Share on WhatsApp.
 */
@Composable
fun ShareRow(
    onShareClick: () -> Unit = {},
    onShareWhatsAppClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ShareButton(
            label = "Share",
            icon = Icons.Filled.Share,
            backgroundColor = PurpleDeep,
            onClick = onShareClick,
            modifier = Modifier.weight(1f)
        )
        ShareButton(
            label = "Share on WhatsApp",
            icon = Icons.Filled.Chat,
            backgroundColor = Color(0xFF128C7E),
            onClick = onShareWhatsAppClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ShareButton(
    label: String,
    icon: ImageVector,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = WhiteText,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            color = WhiteText,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Social icons row - Instagram, Telegram, WhatsApp.
 */
@Composable
fun SocialIconsRow(
    onInstagramClick: () -> Unit = {},
    onTelegramClick: () -> Unit = {},
    onWhatsAppClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        SocialIconButton(Icons.Filled.Share, "Instagram", PurplePrimary, onInstagramClick)
        Spacer(modifier = Modifier.width(16.dp))
        SocialIconButton(Icons.Filled.Chat, "Telegram", Color(0xFF0088CC), onTelegramClick)
        Spacer(modifier = Modifier.width(16.dp))
        SocialIconButton(Icons.Filled.Chat, "WhatsApp", Color(0xFF25D366), onWhatsAppClick)
    }
}

@Composable
private fun SocialIconButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(color)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = WhiteText,
            modifier = Modifier.size(22.dp)
        )
    }
}
