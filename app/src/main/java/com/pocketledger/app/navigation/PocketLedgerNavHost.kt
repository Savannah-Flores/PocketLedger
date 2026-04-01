package com.pocketledger.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pocketledger.app.ui.screen.AddRecordScreen
import com.pocketledger.app.ui.screen.CalendarScreen
import com.pocketledger.app.ui.screen.HomeScreen
import com.pocketledger.app.ui.screen.RecordScreen
import com.pocketledger.app.ui.screen.SavingsScreen
import com.pocketledger.app.ui.screen.SettingsScreen
import com.pocketledger.app.viewmodel.AddRecordViewModel
import com.pocketledger.app.viewmodel.CalendarViewModel
import com.pocketledger.app.viewmodel.HomeViewModel
import com.pocketledger.app.viewmodel.RecordViewModel
import com.pocketledger.app.viewmodel.SavingsViewModel

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
            val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
            HomeScreen(viewModel = viewModel)
        }
        composable(AppDestination.Record.route) {
            val viewModel: RecordViewModel = viewModel(factory = RecordViewModel.Factory)
            RecordScreen(
                viewModel = viewModel,
                onEditRecord = { recordId ->
                    navController.navigate(AppDestination.EditRecord.createRoute(recordId))
                },
            )
        }
        composable(AppDestination.Calendar.route) {
            val viewModel: CalendarViewModel = viewModel(factory = CalendarViewModel.Factory)
            CalendarScreen(viewModel = viewModel)
        }
        composable(AppDestination.Savings.route) {
            val viewModel: SavingsViewModel = viewModel(factory = SavingsViewModel.Factory)
            SavingsScreen(viewModel = viewModel)
        }
        composable(AppDestination.Settings.route) {
            SettingsScreen()
        }
        composable(AppDestination.AddRecord.route) {
            val viewModel: AddRecordViewModel = viewModel(factory = AddRecordViewModel.factory())
            AddRecordScreen(
                viewModel = viewModel,
                onBackClick = { navController.navigateUp() },
                onSaved = { navController.navigateUp() },
            )
        }
        composable(
            route = AppDestination.EditRecord.route,
            arguments = listOf(navArgument("recordId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val recordId = backStackEntry.arguments?.getLong("recordId")
            val viewModel: AddRecordViewModel = viewModel(factory = AddRecordViewModel.factory(recordId))
            AddRecordScreen(
                viewModel = viewModel,
                onBackClick = { navController.navigateUp() },
                onSaved = { navController.navigateUp() },
            )
        }
    }
}
