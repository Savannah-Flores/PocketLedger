package com.pocketledger.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pocketledger.app.ui.screen.AddRecordScreen
import com.pocketledger.app.ui.screen.CalendarScreen
import com.pocketledger.app.ui.screen.HomeScreen
import com.pocketledger.app.ui.screen.RecordScreen
import com.pocketledger.app.ui.screen.SavingsScreen
import com.pocketledger.app.ui.screen.SettingsScreen
import com.pocketledger.app.viewmodel.AddRecordViewModel
import com.pocketledger.app.viewmodel.HomeViewModel

@Composable
fun PocketLedgerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Home.route,
        modifier = modifier,
    ) {
        composable(AppDestination.Home.route) {
            val viewModel: HomeViewModel = viewModel()
            HomeScreen(viewModel = viewModel)
        }
        composable(AppDestination.Record.route) {
            RecordScreen()
        }
        composable(AppDestination.Calendar.route) {
            CalendarScreen()
        }
        composable(AppDestination.Savings.route) {
            SavingsScreen()
        }
        composable(AppDestination.Settings.route) {
            SettingsScreen()
        }
        composable(AppDestination.AddRecord.route) {
            val viewModel: AddRecordViewModel = viewModel()
            AddRecordScreen(
                viewModel = viewModel,
                onBackClick = { navController.navigateUp() },
            )
        }
    }
}
