package com.example.recycleg.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.recycleg.R
import com.example.recycleg.ui.RecycleGDestinations
import com.example.recycleg.ui.theme.RecycleGTheme

@Composable
fun AppNavRail(
    currentRoute: String,
    navigateToHome: () -> Unit,
    navigateToScanner: () -> Unit,
    navigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        header = {
            Icon(
                painter = painterResource(id = R.drawable.recycle_logo_fixed),
                contentDescription = null,
                Modifier.padding(12.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        modifier = modifier
    ) {
        Spacer(Modifier.weight(1f))
        NavigationRailItem(
            selected = currentRoute == RecycleGDestinations.HOME_ROUTE,
            onClick = navigateToHome,
            icon = {
                Icon(
                    if (currentRoute == RecycleGDestinations.HOME_ROUTE) Icons.Filled.Home else Icons.Outlined.Home,
                    stringResource(R.string.home_title)
                )
            },
            label = { Text(stringResource(R.string.home_title)) },
            alwaysShowLabel = false
        )
        NavigationRailItem(
            selected = currentRoute == RecycleGDestinations.SCANNER_ROUTE,
            onClick = navigateToScanner,
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.barcode_scanner_fill0_wght300_grad0_opsz48),
                    stringResource(R.string.scanner_name),
                    Modifier.size(24.dp)
                )
            },
            label = { Text(stringResource(R.string.scanner_name)) },
            alwaysShowLabel = false
        )
        NavigationRailItem(
            selected = currentRoute == RecycleGDestinations.PROFILE_ROUTE,
            onClick = navigateToProfile,
            icon = {
                Icon(
                    if (currentRoute == RecycleGDestinations.PROFILE_ROUTE) Icons.Filled.Person else Icons.Outlined.Person,
                    stringResource(R.string.profile_title)
                )
            },
            label = { Text(stringResource(R.string.profile_title)) },
            alwaysShowLabel = false
        )
        Spacer(Modifier.weight(1f))
    }
}

@Preview("Drawer contents")
@Preview("Drawer contents (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewAppNavRail() {
    RecycleGTheme {
        AppNavRail(
            currentRoute = RecycleGDestinations.SCANNER_ROUTE,
            navigateToHome = {},
            navigateToScanner = {},
            navigateToProfile = {}
        )
    }
}
