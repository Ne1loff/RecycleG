package com.example.recycleg.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import com.example.recycleg.model.GarbageType

@Composable
fun HomeRoute(
    homeViewModel: HomeViewModel,
    isExpandedScreen: Boolean,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {

    val uiState by homeViewModel.uiState.collectAsState()

    HomeRoute(
        uiState = uiState,
        isExpandedScreen = isExpandedScreen,
        onSelectPost = { homeViewModel.selectArticle(it) },
        onRefreshPosts = { homeViewModel.refreshPosts() },
        onErrorDismiss = { homeViewModel.errorShown(it) },
        onInteractWithFeed = { homeViewModel.interactedWithFeed() },
        onInteractWithArticleDetails = { homeViewModel.interactedWithArticleDetails(it) },
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun HomeRoute(
    uiState: HomeUiState,
    isExpandedScreen: Boolean,
    onSelectPost: (GarbageType) -> Unit,
    onRefreshPosts: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    onInteractWithFeed: () -> Unit,
    onInteractWithArticleDetails: (GarbageType) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val homeListLazyListState = rememberLazyListState()
    val articleDetailsLazyListState = when (uiState) {
        is HomeUiState.HasGarbageInfoPosts -> uiState.garbagePostsFeed.allInfoPosts
        is HomeUiState.NoGarbageInfoPosts -> emptyList()
    }.associate { post ->
        key(post.type) {
            post.type to rememberLazyListState()
        }
    }

    when (getHomeScreenType(isExpandedScreen = isExpandedScreen, uiState = uiState)) {
        HomeScreenType.ArticleDetails -> {
            check(uiState is HomeUiState.HasGarbageInfoPosts)
            //TODO: ArticleScreen

            BackHandler {
                onInteractWithFeed()
            }
        }
        HomeScreenType.Feed -> {

        }
        HomeScreenType.FeedWithArticleDetails -> {
            //TODO: ArticleScreen
        }
    }
}

private enum class HomeScreenType {
    FeedWithArticleDetails,
    Feed,
    ArticleDetails
}

@Composable
private fun getHomeScreenType(
    isExpandedScreen: Boolean,
    uiState: HomeUiState
): HomeScreenType = when (isExpandedScreen) {
    false -> {
        when (uiState) {
            is HomeUiState.HasGarbageInfoPosts -> {
                if (uiState.isArticleOpen) {
                    HomeScreenType.ArticleDetails
                } else {
                    HomeScreenType.Feed
                }
            }
            is HomeUiState.NoGarbageInfoPosts -> HomeScreenType.Feed
        }
    }
    true -> HomeScreenType.FeedWithArticleDetails
}