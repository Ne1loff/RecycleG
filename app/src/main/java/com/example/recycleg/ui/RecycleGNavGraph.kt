package com.example.recycleg.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.recycleg.data.AppContainer
import com.example.recycleg.ui.home.HomeRoute
import com.example.recycleg.ui.home.HomeViewModel

@Composable
fun RecycleGNavGraph(
    appContainer: AppContainer,
    isExpandedScreen: Boolean,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = RecycleGDestinations.HOME_ROUTE
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(RecycleGDestinations.HOME_ROUTE) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.provideFactory(appContainer.garbageInfoPostsRepository)
            )
            HomeRoute(homeViewModel = homeViewModel, isExpandedScreen = isExpandedScreen)
        }
        // TODO:
    }
}