package com.example.recycleg.ui.article

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.recycleg.R
import com.example.recycleg.data.garbage.impl.plastic
import com.example.recycleg.model.GarbageInfoPost
import com.example.recycleg.ui.theme.RecycleGTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleScreen(
    GarbageInfoPost: GarbageInfoPost,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState()
) {

    Row(modifier = modifier.fillMaxSize()) {
        val context = LocalContext.current
        ArticleScreenContent(GarbageInfoPost,
            navigationIconContent = {
                Icons.Filled.ArrowLeft
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
    CenterAlignedTopAppBar(
        title = {
            Row() {
                Image(
                    painter = painterResource(id = R.drawable.icon_article_background),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(36.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(start = 8.dp)
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
fun PriviewArticleScreen(){
    RecycleGTheme(){
        Surface {
            ArticleScreen(GarbageInfoPost = plastic)
        }
    }
}
