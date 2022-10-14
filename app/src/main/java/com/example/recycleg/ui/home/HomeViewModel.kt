package com.example.recycleg.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.recycleg.R
import com.example.recycleg.data.Result
import com.example.recycleg.data.garbage.GarbageInfoPostsRepository
import com.example.recycleg.model.GarbageInfoPost
import com.example.recycleg.model.GarbagePostsFeed
import com.example.recycleg.model.GarbageType
import com.example.recycleg.utils.ErrorMessage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

sealed interface HomeUiState {

    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>

    data class NoGarbageInfoPosts(
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>
    ) : HomeUiState

    data class HasGarbageInfoPosts(
        val garbagePostsFeed: GarbagePostsFeed,
        val selectedPost: GarbageInfoPost,
        val isArticleOpen: Boolean,
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>
    ) : HomeUiState
}

private data class HomeViewModelState(
    val postsFeed: GarbagePostsFeed? = null,
    val selectedPostType: GarbageType? = null,
    val isArticleOpen: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList()
) {
    fun toUiState(): HomeUiState =
        if (postsFeed == null) {
            HomeUiState.NoGarbageInfoPosts(
                isLoading = isLoading,
                errorMessages = errorMessages
            )
        } else {
            HomeUiState.HasGarbageInfoPosts(
                garbagePostsFeed = postsFeed,
                selectedPost = postsFeed.allInfoPosts.find {
                    it.type == selectedPostType
                } ?: postsFeed.info.first(),
                isArticleOpen = isArticleOpen,
                isLoading = isLoading,
                errorMessages = errorMessages
            )
        }
}

class HomeViewModel(
    private val repository: GarbageInfoPostsRepository
) : ViewModel() {

    private val viewModelState = MutableStateFlow(HomeViewModelState(isLoading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        refreshPosts()
    }

    fun refreshPosts() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = repository.getGarbagePostsFeed()
            viewModelState.update {
                when (result) {
                    is Result.Success -> it.copy(postsFeed = result.data, isLoading = false)
                    is Result.Error -> {
                        val errorMessages = it.errorMessages + ErrorMessage(
                            id = UUID.randomUUID().mostSignificantBits,
                            messageId = R.string.load_error
                        )
                        it.copy(errorMessages = errorMessages, isLoading = false)
                    }
                }
            }
        }
    }

    fun selectArticle(postType: GarbageType) {
        interactedWithArticleDetails(postType)
    }

    fun errorShown(errorId: Long) {
        viewModelState.update { currentUiState ->
            val errorMessages = currentUiState.errorMessages.filterNot { it.id == errorId }
            currentUiState.copy(errorMessages = errorMessages)
        }
    }

    fun interactedWithFeed() {
        viewModelState.update {
            it.copy(isArticleOpen = false)
        }
    }

    fun interactedWithArticleDetails(postType: GarbageType) {
        viewModelState.update {
            it.copy(
                selectedPostType = postType,
                isArticleOpen = true
            )
        }
    }

    companion object {
        fun provideFactory(
            repository: GarbageInfoPostsRepository,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(repository) as T
            }
        }
    }
}