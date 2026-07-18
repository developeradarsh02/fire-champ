package com.firechamp.tournament.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firechamp.tournament.data.model.GameMode
import com.firechamp.tournament.presentation.theme.PurpleBright
import com.firechamp.tournament.presentation.theme.PurpleDark
import com.firechamp.tournament.presentation.theme.PurpleDeep
import com.firechamp.tournament.presentation.theme.PurplePrimary
import com.firechamp.tournament.presentation.theme.RedAccent
import com.firechamp.tournament.presentation.theme.WhiteText

/**
 * Game mode card (Earn screen grid me dikhta hai).
 *
 * Layout (screenshot jaisa):
 *   ┌─────────────┐
 *   │  [gradient] │  ← image area (placeholder gradient)
 *   │  FULL MAP   │
 *   └─────────────┘
 *   ┌─────────────┐
 *   │  HEAD 2V2   │  ← red label bar
 *   └─────────────┘
 *
 * Real images Coil se load honge jab URLs aayengi (Task 14 me).
 * Abhi different colored gradients use kar rahe hain placeholder ke liye.
 */
@Composable
fun GameModeCard(
    gameMode: GameMode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        // Image area with gradient (placeholder)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(placeholderGradient(gameMode.id)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = gameMode.name,
                color = WhiteText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Red label bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(RedAccent),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = gameMode.label,
                color = WhiteText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 6.dp)
            )
        }
    }
}

/**
 * Card ID ke basis pe stable gradient assign karta hai
 * (taaki har card ka alag color ho but rebuild pe same rahe).
 */
private fun placeholderGradient(id: String): Brush {
    val palettes = listOf(
        listOf(PurpleDark, PurplePrimary),
        listOf(Color(0xFF1B5E20), Color(0xFF388E3C)),
        listOf(Color(0xFF0D47A1), Color(0xFF1976D2)),
        listOf(Color(0xFFB71C1C), Color(0xFFD32F2F)),
        listOf(Color(0xFF4A148C), PurpleBright),
        listOf(Color(0xFFE65100), Color(0xFFFF6F00)),
        listOf(PurpleDeep, Color(0xFF311B92)),
        listOf(Color(0xFF004D40), Color(0xFF00695C))
    )
    // Hash by id to get consistent color
    val index = id.hashCode().let { if (it < 0) -it else it } % palettes.size
    val colors = palettes[index]
    return Brush.verticalGradient(colors)
}
