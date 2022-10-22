package com.example.recycleg.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.recycleg.data.AppContainer
import com.example.recycleg.ui.components.AppBottomNavBar
import com.example.recycleg.ui.components.AppNavRail
import com.example.recycleg.ui.theme.RecycleGTheme

@Composable
fun RecycleGApp(
    appContainer: AppContainer,
    widthSizeClass: WindowWidthSizeClass
) {
    RecycleGTheme {
        val navController = rememberNavController()
        val navigationActions = remember(navController) {
            RecycleGNavigationAction(navController)
        }

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute =
            navBackStackEntry?.destination?.route ?: RecycleGDestinations.HOME_ROUTE

        val isExpandedScreen = widthSizeClass == WindowWidthSizeClass.Expanded

        Row {
            if (isExpandedScreen) {
                AppNavRail(
                    currentRoute = currentRoute,
                    navigateToHome = navigationActions.navigateToHome,
                    navigateToScanner = navigationActions.navigateToScanner,
                    navigateToProfile = navigationActions.navigateToProfile
                )
            }
            Column {
                RecycleGNavGraph(
                    appContainer = appContainer,
                    isExpandedScreen = isExpandedScreen,
                    navController = navController,
                    appBottomNavBar = {
                        if (!isExpandedScreen) {
                            AppBottomNavBar(
                                currentRoute = currentRoute,
                                navigateToHome = navigationActions.navigateToHome,
                                navigateToScanner = navigationActions.navigateToScanner,
                                navigateToProfile = navigationActions.navigateToProfile
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun rememberContentPaddingForScreen(
    additionalTop: Dp = 0.dp,
    excludeTop: Boolean = false
) =
    WindowInsets.systemBars
        .only(if (excludeTop) WindowInsetsSides.Bottom else WindowInsetsSides.Vertical)
        .add(WindowInsets(top = additionalTop))
        .asPaddingValues()
