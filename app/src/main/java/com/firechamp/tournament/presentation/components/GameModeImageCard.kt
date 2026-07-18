package com.firechamp.tournament.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firechamp.tournament.R
import com.firechamp.tournament.data.model.GameMode
import com.firechamp.tournament.presentation.theme.PurplePrimary
import com.firechamp.tournament.presentation.theme.RedAccent
import com.firechamp.tournament.presentation.theme.WhiteText

/**
 * Game mode card with full-bleed image + bottom label band.
 *
 * Arise Battle style: image fill, with name banner at bottom.
 *
 * @param imageRes Drawable resource ID. If 0, generates gradient placeholder.
 */
@Composable
fun GameModeImageCard(
    gameMode: GameMode,
    imageRes: Int = 0,
    height: Dp = 140.dp,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        if (imageRes != 0) {
            // Use image asset
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = gameMode.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Fallback gradient with character icon
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1A1A2E),
                                Color(0xFF16213E),
                                Color(0xFF0F3460)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = gameMode.name.firstOrNull()?.uppercase() ?: "?",
                    color = PurplePrimary,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        // Bottom label band (red with name)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .align(Alignment.BottomCenter)
                .background(RedAccent)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = gameMode.name,
                color = WhiteText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

/**
 * Maps game mode ID to local drawable resource.
 * Returns 0 if no image available (fallback gradient).
 */
fun gameModeImageRes(gameModeId: String): Int {
    return when (gameModeId.uppercase()) {
        "FULL_MAP", "FULL MAP" -> R.drawable.game_mode_full_map
        "CS_1V1", "CS 1V1", "CS 1VS1" -> R.drawable.game_mode_cs_1v1
        "LW_1V1", "LW 1V1", "LW 1VS1" -> R.drawable.game_mode_lw_1v1
        "CS_2V2", "CS 2V2", "CS 2VS2" -> R.drawable.game_mode_cs_2v2
        "CS_4V4", "CS 4V4", "CS 4VS4" -> R.drawable.game_mode_cs_4v4
        "LW_2V2", "LW 2V2", "LW 2VS2" -> R.drawable.game_mode_lw_2v2
        else -> 0
    }
}
