package com.example.recycleg.ui.article

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.recycleg.data.garbage.impl.plastic
import com.example.recycleg.model.GarbageInfoPost
import com.example.recycleg.ui.theme.RecycleGTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleScreen(
    garbageInfoPost: GarbageInfoPost,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    lazyListState: LazyListState = rememberLazyListState()
) {

    Row(
        modifier = modifier
            .fillMaxSize()
    ) {
        val context = LocalContext.current
        ArticleScreenContent(garbageInfoPost,
            navigationIconContent = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = garbageInfoPost.title,
                        modifier = Modifier
                    )
                }
            })
    }
}


@ExperimentalMaterial3Api
@Composable
private fun ArticleScreenContent(
    GarbageInfoPost: GarbageInfoPost,
    navigationIconContent: @Composable () -> Unit = {},
    bottomBarContent: @Composable () -> Unit = { },
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = GarbageInfoPost.title,
                navigationIconContent = navigationIconContent,
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = bottomBarContent
    ) { innerPadding ->
        PostContent(
            garbageInfo = GarbageInfoPost,
            modifier = Modifier.padding(innerPadding),
            state = lazyListState
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
    title: String,
    navigationIconContent: @Composable () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior?,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row() {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        },
        navigationIcon = navigationIconContent,
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}

@Preview
@Composable
fun PriviewArticleScreen() {
    RecycleGTheme() {
        Surface {
            ArticleScreen(garbageInfoPost = plastic, modifier = Modifier, {})
        }
    }
}
