package com.example.recycleg.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.recycleg.ui.theme.Elevation
import com.example.recycleg.ui.theme.RecycleGTheme
import kotlinx.coroutines.launch

@Composable
fun BottomSheetContent(content: @Composable ColumnScope.() -> Unit) {
    val config = LocalConfiguration.current

    Column(
        modifier = Modifier
            .heightIn(max = (config.screenHeightDp * .75f).dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(8.dp)
                .size(width = 32.dp, height = 4.dp)
                .background(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .4f),
                    shape = MaterialTheme.shapes.extraLarge
                ),
        )
        content()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewBottomSheet() {
    val state = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

    RecycleGTheme {
        BottomSheetScaffold(
            backgroundColor = MaterialTheme.colorScheme.secondary,
            scaffoldState = state,
            sheetContent = {
                BottomSheetContent {
                    Text("Haalo", modifier = Modifier.padding(16.dp))
                }
            },
            sheetPeekHeight = 0.dp,
            sheetBackgroundColor = MaterialTheme.colorScheme.surface,
            sheetShape = MaterialTheme.shapes.extraLarge.copy(
                bottomStart = CornerSize(0.dp),
                bottomEnd = CornerSize(0.dp),
            ),
            sheetElevation = Elevation.lvl1,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ) {
            val sheetState = state.bottomSheetState
            Text("isExpanded - ${sheetState.isExpanded}")
            Text("isCollapsed - ${sheetState.isCollapsed}")
            Text(sheetState.progress.fraction.toString())
            Button(onClick = {
                scope.launch {
                    if (sheetState.isExpanded) sheetState.collapse()
                    else sheetState.expand()
                }
            }) {
                Text("CLICK")
            }
        }
    }
}