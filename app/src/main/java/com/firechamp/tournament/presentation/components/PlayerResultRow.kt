package com.firechamp.tournament.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firechamp.tournament.data.model.PlayerResult
import com.firechamp.tournament.data.model.Winner
import com.firechamp.tournament.presentation.theme.GoldCoin
import com.firechamp.tournament.presentation.theme.GreyDivider
import com.firechamp.tournament.presentation.theme.OrangeWarning
import com.firechamp.tournament.presentation.theme.WhiteText

/**
 * Reusable table row - Match Result table me har player ke liye.
 *
 * Columns: # | Player Name (with ⚠️ if flagged) | Kills | Winning
 */
@Composable
fun PlayerResultRow(
    rank: Int,
    playerName: String,
    kills: Int,
    winning: Int,
    isFlagged: Boolean = false,
    isHeader: Boolean = false,
    backgroundColor: Color = Color.White,
    textColor: Color = Color.Black,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank
        Text(
            text = if (isHeader) "#" else rank.toString(),
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(30.dp)
        )

        // Player Name (with warning icon if flagged)
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isHeader) "Player Name" else playerName,
                color = textColor,
                fontSize = 13.sp,
                fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1
            )
            if (isFlagged) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = "Reported player",
                    tint = OrangeWarning,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // Kills
        Text(
            text = if (isHeader) "Kills" else kills.toString(),
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(50.dp)
        )

        // Winning
        Row(
            modifier = Modifier.width(70.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isHeader) "Winning" else winning.toString(),
                color = if (isHeader) textColor else GoldCoin,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Helper: PlayerResult data class se row banata hai.
 */
@Composable
fun PlayerResultRow(
    result: PlayerResult,
    isHeader: Boolean = false,
    backgroundColor: Color = Color.White,
    textColor: Color = Color.Black,
    modifier: Modifier = Modifier
) {
    PlayerResultRow(
        rank = result.rank,
        playerName = result.playerName,
        kills = result.kills,
        winning = result.winningAmount,
        isFlagged = result.isFlagged,
        isHeader = isHeader,
        backgroundColor = backgroundColor,
        textColor = textColor,
        modifier = modifier
    )
}

/**
 * Helper: Winner data class se row banata hai (top winner display).
 */
@Composable
fun WinnerRow(
    winner: Winner,
    modifier: Modifier = Modifier
) {
    PlayerResultRow(
        rank = winner.rank,
        playerName = winner.playerName,
        kills = winner.kills,
        winning = winner.winningAmount,
        isFlagged = false,
        backgroundColor = Color.White,
        textColor = Color.Black,
        modifier = modifier
    )
}

/**
 * Section header row - Winner ya Match Result section ke liye.
 * Dark purple background, white bold text, centered.
 */
@Composable
fun TableSectionHeader(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .background(com.firechamp.tournament.presentation.theme.PurpleDeep)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = WhiteText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Divider line for table.
 */
@Composable
fun TableDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(GreyDivider)
            .padding(0.5.dp)
    )
}
