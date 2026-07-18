package com.firechamp.tournament.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firechamp.tournament.presentation.theme.GoldCoin
import com.firechamp.tournament.presentation.theme.PurpleDeep
import com.firechamp.tournament.presentation.theme.WhiteText

/**
 * Auto-scrolling marquee text - "WITHDRAWAL COMPLETE IN 30 MIN ✅" jaisa.
 *
 * Implementation: text ko repeat karke horizontally scroll karte hain
 * using infinite animation. Seamless loop ki koshish - kuch edge cases
 * me thoda jump dikh sakta hai but looks like a real marquee.
 */
@Composable
fun MarqueeText(
    text: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "marquee")
    val translateX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "translate"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .clip(RoundedCornerShape(0.dp))
            .background(PurpleDeep)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterStart)
                .graphicsLayer { translationX = translateX * 1200f },
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Megaphone icon at start
            Icon(
                imageVector = Icons.Filled.Campaign,
                contentDescription = null,
                tint = GoldCoin,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .height(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))

            // Repeat text 4 times to ensure continuous appearance
            repeat(4) { i ->
                Text(
                    text = text,
                    color = WhiteText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                if (i < 3) {
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }
        }
    }
}
