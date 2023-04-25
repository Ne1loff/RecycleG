package com.example.recycleg.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import com.example.recycleg.model.GarbageType
import com.example.recycleg.ui.article.ArticleScreen

@Composable
fun HomeRoute(
    homeViewModel: HomeViewModel,
    isExpandedScreen: Boolean,
    bottomBar: @Composable () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {

    val uiState by homeViewModel.uiState.collectAsState()

    HomeRoute(
        uiState = uiState,
        isExpandedScreen = isExpandedScreen,
        onSelectPost = homeViewModel::selectArticle,
        onRefreshPosts = homeViewModel::refreshPosts,
        onErrorDismiss = homeViewModel::errorShown,
        onInteractWithFeed = homeViewModel::interactedWithFeed,
        onInteractWithArticleDetails = homeViewModel::interactedWithArticleDetails,
        bottomBar = bottomBar,
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
    bottomBar: @Composable () -> Unit,
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
            ArticleScreen(garbageInfoPost = uiState.selectedPost, onBack = onInteractWithFeed)

            BackHandler {
                onInteractWithFeed()
            }
        }
        HomeScreenType.Feed -> {
            HomeFeedScreen(
                uiState = uiState,
                showTopAppBar = !isExpandedScreen,
                onSelectPost = onSelectPost,
                onRefreshPosts = onRefreshPosts,
                onErrorDismiss = onErrorDismiss,
                homeListLazyListState = homeListLazyListState,
                bottomBar = bottomBar,
                snackbarHostState = snackbarHostState
            )

        }
        HomeScreenType.FeedWithArticleDetails -> {
            HomeFeedWithArticleDetailsScreen(
                uiState = uiState,
                showTopAppBar = !isExpandedScreen,
                onSelectPost = onSelectPost,
                onRefreshPosts = onRefreshPosts,
                onErrorDismiss = onErrorDismiss,
                onInteractWithList = onInteractWithFeed,
                onInteractWithDetail = onInteractWithArticleDetails,
                homeListLazyListState = homeListLazyListState,
                bottomBar = bottomBar,
                articleDetailLazyListStates = articleDetailsLazyListState,
                snackbarHostState = snackbarHostState
            )
        }
    }
}

private enum class HomeScreenType {
    FeedWithArticleDetails,
    Feed,
    ArticleDetails
}

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