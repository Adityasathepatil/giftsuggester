package com.techtool.giftsuggester.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.techtool.giftsuggester.GiftSuggesterViewModel
import com.techtool.giftsuggester.screens.InfoScreen
import com.techtool.giftsuggester.screens.ResultScreen
import com.techtool.giftsuggester.screens.ScanScreen

sealed class NavigationItem(val route: String) {
    object Info : NavigationItem("info")
    object Scan : NavigationItem("scan")
    object Result : NavigationItem("result")
}

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val viewModel: GiftSuggesterViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = NavigationItem.Info.route
    ) {
        composable(NavigationItem.Info.route) {
            InfoScreen(navController = navController)
        }

        composable(NavigationItem.Scan.route) {
            ScanScreen(navController = navController, viewModel = viewModel)
        }

        composable(NavigationItem.Result.route) {
            ResultScreen(navController = navController, viewModel = viewModel)
        }
    }
}
