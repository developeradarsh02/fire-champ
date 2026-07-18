package com.firechamp.tournament.presentation.screens.account.support

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firechamp.tournament.presentation.components.SubScreenTopBar
import com.firechamp.tournament.presentation.theme.BlackBackground
import com.firechamp.tournament.presentation.theme.PurplePrimary
import com.firechamp.tournament.presentation.theme.RedAccent
import com.firechamp.tournament.presentation.theme.WhiteSecondary
import com.firechamp.tournament.presentation.theme.WhiteText

/**
 * Customer Support Screen - PDF Task 7 ke according.
 *
 * Real values abhi placeholder hain - jab user actual data dega tab
 * Firebase Remote Config se yahan update ho jayega.
 *
 * Currently using:
 *  - Email: support@firechamp.app
 *  - Instagram: @firechamp.app
 *  - Telegram: @firechamp_support
 *  - Time: 24/7 on Telegram
 *  - Street: India
 *  - WhatsApp: +91-XXXXXXXXXX (placeholder)
 */
@Composable
fun CustomerSupportScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    val supportInfo = SupportInfo(
        address = "India",
        email = "firechampcustomerservice@gmail.com",
        instagram = "@firechamp.app",
        street = "India",
        time = "Available: 10 AM - 10 PM (Daily)",
        whatsappNumber = "919522079569",
        telegramUsername = "firechampp"
    )

    Column(modifier = Modifier.fillMaxSize().background(BlackBackground)) {
        SubScreenTopBar(title = "Customer Support", onBack = onBack)

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)) {
            SupportRow(label = "Address :", value = supportInfo.address) { /* no action */ }
            DividerLine()
            SupportRowWithIcon(
                label = "Email :",
                value = supportInfo.email,
                icon = Icons.Filled.Email,
                onClick = {
                    openEmailIntent(context, supportInfo.email)
                }
            )
            DividerLine()
            SupportRowWithIcon(
                label = "Instagram :",
                value = supportInfo.instagram,
                icon = Icons.Filled.CameraAlt,
                onClick = {
                    openInstagramIntent(context, supportInfo.instagram)
                }
            )
            DividerLine()
            SupportRow(label = "Street :", value = supportInfo.street) { /* no action */ }
            DividerLine()
            SupportRow(
                label = "Time :",
                value = supportInfo.time,
                valueColor = RedAccent
            ) { /* no action */ }
        }

        Spacer(modifier = Modifier.weight(1f))

        // CHAT WITH AGENT button (purple pill, full width, bold white text)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .height(50.dp)
                .clip(RoundedCornerShape(25.dp))
                .background(PurplePrimary)
                .clickable {
                    openTelegramIntent(context, supportInfo.telegramUsername)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "CHAT WITH AGENT",
                color = WhiteText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
private fun SupportRow(
    label: String,
    value: String,
    valueColor: Color = WhiteText,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = WhiteSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = value,
            color = valueColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SupportRowWithIcon(
    label: String,
    value: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = WhiteSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = value,
            color = WhiteText,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(PurplePrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFE91E63),  // Instagram pink for Instagram icon
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun DividerLine() {
    Box(
        modifier = Modifier.fillMaxWidth().height(0.5.dp).background(Color.White.copy(alpha = 0.15f))
    )
}

private data class SupportInfo(
    val address: String,
    val email: String,
    val instagram: String,
    val street: String,
    val time: String,
    val whatsappNumber: String,
    val telegramUsername: String
)

// ============== Intent Handlers ==============

private fun openEmailIntent(context: android.content.Context, email: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
        }
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No email app installed", Toast.LENGTH_SHORT).show()
    }
}

private fun openInstagramIntent(context: android.content.Context, username: String) {
    val cleanUsername = username.removePrefix("@")
    val uri = Uri.parse("http://instagram.com/_u/$cleanUsername")
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.instagram.android")
    }
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        // Fallback to browser
        val browserIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("http://instagram.com/$cleanUsername")
        )
        context.startActivity(browserIntent)
    }
}

private fun openTelegramIntent(context: android.content.Context, username: String) {
    val cleanUsername = username.removePrefix("@")
    val uri = Uri.parse("https://t.me/$cleanUsername")
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("org.telegram.messenger")
    }
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        val browserIntent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(browserIntent)
    }
}
