package com.example.recycleg.ui

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

object RecycleGDestinations {
    const val HOME_ROUTE = "home"
    const val SCANNER_ROUTE = "scanner"
    const val PROFILE_ROUTE = "profile"
}

class RecycleGNavigationAction(navController: NavHostController) {
    val navigateToHome: () -> Unit = {
        navController.navigate(RecycleGDestinations.HOME_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToScanner: () -> Unit = {
        navController.navigate(RecycleGDestinations.SCANNER_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToProfile: () -> Unit = {
        navController.navigate(RecycleGDestinations.PROFILE_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}