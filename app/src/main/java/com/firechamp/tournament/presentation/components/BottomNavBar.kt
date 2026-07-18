package com.firechamp.tournament.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firechamp.tournament.presentation.navigation.BottomTab
import com.firechamp.tournament.presentation.theme.BlackBackground
import com.firechamp.tournament.presentation.theme.PurpleDeep
import com.firechamp.tournament.presentation.theme.GoldFire
import com.firechamp.tournament.presentation.theme.WhiteSecondary

/**
 * Bottom Navigation Bar - Material 3 NavigationBar.
 *
 * 3 tabs: Earn / Play / Account
 * - Active tab: purple icon + purple bold text
 * - Inactive: grey icon + grey text
 */
@Composable
fun BottomNavBar(
    selectedTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit
) {
    NavigationBar(
        containerColor = BlackBackground,
        contentColor = WhiteSecondary,
        tonalElevation = 0.dp
    ) {
        BottomTab.entries.forEach { tab ->
            val selected = tab == selectedTab
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title
                    )
                },
                label = {
                    Text(
                        text = tab.title,
                        fontSize = 12.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GoldFire,
                    selectedTextColor = GoldFire,
                    unselectedIconColor = WhiteSecondary,
                    unselectedTextColor = WhiteSecondary,
                    indicatorColor = PurpleDeep
                )
            )
        }
    }
}
