package com.firechamp.tournament.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Bottom navigation ke 3 tabs.
 * State hoisting ke liye - MainScreen ownership rakhta hai selected tab.
 */
enum class BottomTab(
    val title: String,
    val icon: ImageVector
) {
    EARN("Earn", Icons.Filled.MonetizationOn),
    PLAY("Play", Icons.Filled.SportsEsports),
    ACCOUNT("Account", Icons.Filled.Person)
}
