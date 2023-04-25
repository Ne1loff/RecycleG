package com.example.recycleg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import com.example.recycleg.data.garbage.impl.garbagePostsFeed
import com.example.recycleg.model.GarbageType
import com.example.recycleg.ui.RecycleGApp

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val appContainer = (application as RecycleGApplication).container
        setContent {
            UpdateData()
            val widthSizeClass = calculateWindowSizeClass(this).widthSizeClass
            RecycleGApp(appContainer, widthSizeClass)
        }
    }

    @Composable
    private fun UpdateData() {
        garbagePostsFeed.allInfoPosts.forEach {
            it.title = when (it.type) {
                GarbageType.Paper -> stringResource(id = R.string.gt_paper)
                GarbageType.Glass -> stringResource(id = R.string.gt_glass)
                GarbageType.Metal -> stringResource(id = R.string.gt_metal)
                GarbageType.Organic -> stringResource(id = R.string.gt_organic)
                GarbageType.Plastic -> stringResource(id = R.string.gt_plastic)
            }
        }
    }
}