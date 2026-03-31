package com.pocketledger.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StickyNote2
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    data object Home : AppDestination("home", "首页", Icons.Outlined.Home)
    data object Record : AppDestination("record", "记录", Icons.Outlined.StickyNote2)
    data object Calendar : AppDestination("calendar", "日历", Icons.Outlined.CalendarMonth)
    data object Savings : AppDestination("savings", "存钱", Icons.Outlined.Savings)
    data object Settings : AppDestination("settings", "设置", Icons.Outlined.Settings)
}

val bottomNavDestinations = listOf(
    AppDestination.Home,
    AppDestination.Record,
    AppDestination.Calendar,
    AppDestination.Savings,
    AppDestination.Settings,
)

