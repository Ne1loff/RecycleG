package com.example.recycleg.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.recycleg.R
import com.example.recycleg.ui.RecycleGDestinations
import com.example.recycleg.ui.theme.RecycleGTheme


@Composable
fun AppBottomNavBar(
    currentRoute: String,
    navigateToHome: () -> Unit,
    navigateToScanner: () -> Unit,
    navigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = if (currentRoute == RecycleGDestinations.HOME_ROUTE)
                        Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "home"
                )
            },
            label = { Text("Home") },
            selected = currentRoute == RecycleGDestinations.HOME_ROUTE,
            onClick = navigateToHome
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.barcode_scanner_fill0_wght300_grad0_opsz48),
                    contentDescription = "barcode",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Scanner") },
            selected = currentRoute == RecycleGDestinations.SCANNER_ROUTE,
            onClick = navigateToScanner
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = if (currentRoute == RecycleGDestinations.PROFILE_ROUTE)
                        Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Profile"
                )
            },
            label = { Text("Profile") },
            selected = currentRoute == RecycleGDestinations.PROFILE_ROUTE,
            onClick = navigateToProfile
        )
    }
}

@Preview("Drawer contents")
@Preview("Drawer contents (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewHomeBottomAppBar() {
    RecycleGTheme {
        AppBottomNavBar(
            currentRoute = RecycleGDestinations.SCANNER_ROUTE,
            navigateToHome = {},
            navigateToScanner = {},
            navigateToProfile = {}
        )
    }
}