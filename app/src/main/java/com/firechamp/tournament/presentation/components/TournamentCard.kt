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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firechamp.tournament.data.model.Tournament
import com.firechamp.tournament.presentation.theme.GoldCoin
import com.firechamp.tournament.presentation.theme.GreyDivider
import com.firechamp.tournament.presentation.theme.OrangeWarning
import com.firechamp.tournament.presentation.theme.PurpleDark
import com.firechamp.tournament.presentation.theme.PurplePrimary
import com.firechamp.tournament.presentation.theme.RedAccent
import com.firechamp.tournament.presentation.theme.WhiteSecondary
import com.firechamp.tournament.presentation.theme.WhiteText

/**
 * Tournament Card - Tournament list screen me har tournament ke liye ek card.
 *
 * Layout (PDF spec ke according):
 *   ┌──────────────────────────────┐
 *   │  [banner placeholder]        │  ← gradient banner area
 *   │  RULES / Mode overlay text   │
 *   └──────────────────────────────┘
 *   [Solo pill] [BERMUDA pill]    ← mode + map tags
 *   SOLO – Per Kill + Top Prize | Ryden BAN – – Match #32345  ← title
 *   ─────────────────────────────  ← divider
 *   📅 Date/Time   🏆 Prize   💀 Per Kill   ← 3-col info row
 *   ─────────────────────────────
 *   3/48 ━━━━━━━━  [JOIN 6 🪙 ›]   ← slots + progress + JOIN button
 */
@Composable
fun TournamentCard(
    tournament: Tournament,
    onJoinClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PurpleDark)
            .clickable { onClick() }
    ) {
        // Banner area (gradient placeholder - real image Task 14 me aayega)
        TournamentBanner(tournament = tournament)

        // Body
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Tags row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TagPill(
                    text = tournament.mode.displayName,
                    backgroundColor = RedAccent
                )
                TagPill(
                    text = tournament.map.displayName,
                    backgroundColor = OrangeWarning
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title + match ID
            Text(
                text = tournament.title,
                color = WhiteText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(GreyDivider)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 3-column info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoColumn(
                    icon = Icons.Filled.Schedule,
                    value = tournament.dateTime,
                    label = null,
                    modifier = Modifier.weight(1.3f)
                )
                InfoColumn(
                    icon = Icons.Filled.MonetizationOn,
                    value = tournament.prizePool.toString(),
                    label = "POOL",
                    modifier = Modifier.weight(0.85f)
                )
                InfoColumn(
                    icon = Icons.Filled.MonetizationOn,
                    value = tournament.perKill.toString(),
                    label = "PER KILL",
                    modifier = Modifier.weight(0.85f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom row: slots + progress + JOIN button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${tournament.slotsFilled}/${tournament.totalSlots}",
                        color = WhiteText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = {
                            tournament.slotsFilled.toFloat() / tournament.totalSlots.coerceAtLeast(1)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = PurplePrimary,
                        trackColor = GreyDivider
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // JOIN button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(PurplePrimary)
                        .clickable { onJoinClick() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.MonetizationOn,
                        contentDescription = null,
                        tint = GoldCoin,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "JOIN ${tournament.entryFee}",
                        color = WhiteText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = WhiteText,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TournamentBanner(tournament: Tournament) {
    // Banner with gradient - har game mode ke liye alag color
    val gradient = when (tournament.mode) {
        com.firechamp.tournament.data.model.TournamentMode.SOLO -> listOf(
            Color(0xFF1B5E20), Color(0xFFFF6F00)
        )
        com.firechamp.tournament.data.model.TournamentMode.DUO -> listOf(
            Color(0xFF4A148C), Color(0xFFD32F2F)
        )
        com.firechamp.tournament.data.model.TournamentMode.SQUAD -> listOf(
            Color(0xFF0D47A1), Color(0xFF6A1B9A)
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(Brush.horizontalGradient(gradient)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Top rules strip
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RULES",
                    color = WhiteText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "ID LVL 40+",
                color = WhiteText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "SCREEN RECORDING",
                color = WhiteText.copy(alpha = 0.9f),
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "M79",
                    color = WhiteText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "& DOUBLE VICTOR BANNER",
                    color = WhiteText.copy(alpha = 0.9f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun TagPill(text: String, backgroundColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = WhiteText,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun InfoColumn(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (label == null) WhiteSecondary else GoldCoin,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = value,
                color = WhiteText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
        if (label != null) {
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = label,
                color = WhiteSecondary,
                fontSize = 8.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
