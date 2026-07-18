package com.firechamp.tournament.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Pill-shaped components (28-30dp corner radius as per spec)
val Shapes = Shapes(
    // Very small components
    extraSmall = RoundedCornerShape(4.dp),

    // Small tags/chips
    small = RoundedCornerShape(8.dp),

    // Cards, text fields - use pill shape (rounded)
    medium = RoundedCornerShape(16.dp),

    // Buttons, large inputs - pill shape
    large = RoundedCornerShape(28.dp),

    // Full pill - for primary buttons
    extraLarge = RoundedCornerShape(30.dp)
)
