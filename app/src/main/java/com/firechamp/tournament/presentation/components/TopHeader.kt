package com.firechamp.tournament.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firechamp.tournament.R
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Brush
import com.firechamp.tournament.presentation.theme.GoldFire
import com.firechamp.tournament.presentation.theme.OrangeFire
import com.firechamp.tournament.presentation.theme.PurpleDeep
import com.firechamp.tournament.presentation.theme.WhiteText

/**
 * Reusable Top Header - Earn/Play/Account teeno tabs me use hoga.
 *
 * Layout (Arise Battle style):
 *   [Logo Avatar] Welcome Back,            [Share] [Globe] [Support] [Wallet+]
 *                [Username red bold]
 *
 * Wallet pill: red rounded with gold coin icon + amount + small + button
 * Clicking wallet navigates to wallet screen.
 */
@Composable
fun TopHeader(
    username: String,
    walletBalance: Double,
    onLanguageClick: () -> Unit = {},
    onSupportClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onWebsiteClick: () -> Unit = {},
    onWalletClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar (circular) - shows Fire Champ logo with gold ring
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(PurpleDeep)
                .border(width = 1.5.dp, color = GoldFire, shape = CircleShape)
                .clickable { onAvatarClick() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.fire_champ_logo),
                contentDescription = "Fire Champ Logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(42.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Welcome text
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome Back,",
                color = WhiteText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            )
            Text(
                text = username.ifBlank { "Player" },
                color = GoldFire,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Right side icons: Share, Globe, Support
        IconButton(onClick = onShareClick) {
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = "Share",
                tint = WhiteText,
                modifier = Modifier.size(22.dp)
            )
        }

        IconButton(onClick = onWebsiteClick) {
            Icon(
                imageVector = Icons.Filled.Language,
                contentDescription = "Website",
                tint = WhiteText,
                modifier = Modifier.size(22.dp)
            )
        }

        IconButton(onClick = onSupportClick) {
            Icon(
                imageVector = Icons.Filled.HeadsetMic,
                contentDescription = "Support",
                tint = WhiteText,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Live Wallet pill - fire gradient with black text (mockup style)
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.horizontalGradient(listOf(OrangeFire, GoldFire)))
                .clickable { onWalletClick() }
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.MonetizationOn,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = String.format("%.2f", walletBalance),
                color = Color.Black,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Money",
                    tint = GoldFire,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
