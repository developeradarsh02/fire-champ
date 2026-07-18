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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.firechamp.tournament.R

/**
 * Full-bleed banner image card - 16:9 image with rounded corners.
 * Used in Earn + Play screens. Each banner has a local image asset.
 *
 * @param imageRes Drawable resource ID (e.g. R.drawable.banner_1)
 * @param height Fixed card height (default 200dp)
 * @param onClick Click handler
 */
@Composable
fun FullBleedBanner(
    imageRes: Int,
    height: Dp = 200.dp,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

/**
 * Maps banner ID (1-6) to local drawable resource.
 */
fun bannerImageRes(bannerId: String): Int {
    // Try to parse "1" to "6" from banner ID (e.g. "b1", "1", "banner_1")
    val num = bannerId.filter { it.isDigit() }.toIntOrNull() ?: 0
    return when (num) {
        1 -> R.drawable.banner_1
        2 -> R.drawable.banner_2
        3 -> R.drawable.banner_3
        4 -> R.drawable.banner_4
        5 -> R.drawable.banner_5
        6 -> R.drawable.banner_6
        else -> R.drawable.banner_1
    }
}
