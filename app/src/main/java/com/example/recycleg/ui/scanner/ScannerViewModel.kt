package com.example.recycleg.ui.scanner

import androidx.camera.core.ZoomState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.recycleg.data.Result
import com.example.recycleg.data.garbage.GarbageInfoPostsRepository
import com.example.recycleg.model.GarbageInfoPost
import com.example.recycleg.model.GarbagePostsFeed
import com.example.recycleg.model.GarbageType
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import kotlin.math.*


data class ScannerFocusInfo(
    val x: Float,
    val y: Float,
    val showFocus: Boolean
)

data class ScannerZoomInfo(
    val minZoomRatio: Float,
    val maxZoomRatio: Float,
    val currentZoomRatio: Float,
    val currentZoomRatioMode: ZoomRatioMode,
    val isZooming: Boolean
)

sealed interface ScannerUiState {
    val currentScannerMode: ScannerMode
    val takePicture: Boolean
    val currentTorchMode: TorchMode
    val zoomInfo: ScannerZoomInfo
    val hasFlashUnit: Boolean
    val focusInfo: ScannerFocusInfo

    data class NoGarbageInfoPosts(
        override val currentScannerMode: ScannerMode,
        override val takePicture: Boolean,
        override val currentTorchMode: TorchMode,
        override val zoomInfo: ScannerZoomInfo,
        override val hasFlashUnit: Boolean,
        override val focusInfo: ScannerFocusInfo,
    ) : ScannerUiState

    data class HasGarbageInfoPosts(
        val garbagePostsFeed: GarbagePostsFeed,
        val selectedPost: GarbageInfoPost,
        override val currentScannerMode: ScannerMode,
        override val takePicture: Boolean,
        override val currentTorchMode: TorchMode,
        override val zoomInfo: ScannerZoomInfo,
        override val hasFlashUnit: Boolean,
        override val focusInfo: ScannerFocusInfo,
    ) : ScannerUiState
}

class ScannerUiStateProvider {
    private lateinit var scannerUiState: ScannerUiState

    fun setScannerUiState(state: ScannerUiState) {
        this.scannerUiState = state
    }

    fun getScannerUiState() = scannerUiState
}

private data class ScannerViewModelState(
    val currentScannerMode: ScannerMode = ScannerMode.PHOTO,
    val takePicture: Boolean = false,
    val currentTorchMode: TorchMode = TorchMode.TORCH_OFF,
    val zoomInfo: ScannerZoomInfo =
        ScannerZoomInfo(1f, 1f, 1f, ZoomRatioMode.X1, false),
    val hasFlashUnit: Boolean = false,
    val scannerFocusInfo: ScannerFocusInfo = ScannerFocusInfo(0f, 0f, false),
    val postsFeed: GarbagePostsFeed? = null,
    val selectedPostType: GarbageType? = null,
) {
    fun toUiState(): ScannerUiState =
        if (postsFeed == null) {
            ScannerUiState.NoGarbageInfoPosts(
                currentScannerMode = currentScannerMode,
                takePicture = takePicture,
                currentTorchMode = currentTorchMode,
                zoomInfo = zoomInfo,
                hasFlashUnit = hasFlashUnit,
                focusInfo = scannerFocusInfo,
            )
        } else {
            ScannerUiState.HasGarbageInfoPosts(
                currentScannerMode = currentScannerMode,
                takePicture = takePicture,
                currentTorchMode = currentTorchMode,
                zoomInfo = zoomInfo,
                hasFlashUnit = hasFlashUnit,
                focusInfo = scannerFocusInfo,
                garbagePostsFeed = postsFeed,
                selectedPost = postsFeed.allInfoPosts.find {
                    it.type == selectedPostType
                } ?: postsFeed.info.first(),
            )
        }
}

class ScannerViewModel(
    private val repository: GarbageInfoPostsRepository
) : ViewModel() {

    private val viewModelState = MutableStateFlow(ScannerViewModelState())
    private var activeJob: Job? = null

    init {
        refreshPosts()
    }

    fun refreshPosts() {
        viewModelScope.launch {
            val result = repository.getGarbagePostsFeed()
            viewModelState.update {
                when (result) {
                    is Result.Success -> it.copy(postsFeed = result.data)
                    is Result.Error -> it
                }
            }
        }
    }

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )


    fun selectArticle(postType: GarbageType) {
        interactedWithArticleDetails(postType)
    }

    fun changeScannerMode(mode: ScannerMode) {
        viewModelState.update {
            it.copy(currentScannerMode = mode)
        }
    }

    fun changeZoomRatio(zoomRatio: Float, mode: ZoomRatioMode? = null) {
        viewModelState.update {
            val zoom = max(min(zoomRatio, it.zoomInfo.maxZoomRatio), it.zoomInfo.minZoomRatio)
            val zoomMode = when {
                mode != null -> mode
                zoom >= 1f && zoom < 2f -> ZoomRatioMode.X1
                zoom >= 2f && zoom < 3f -> ZoomRatioMode.X2
                zoom >= 3f && zoom < 4f -> ZoomRatioMode.X3
                else -> ZoomRatioMode.X4
            }
            it.copy(
                zoomInfo = it.zoomInfo.copy(
                    currentZoomRatio = zoom,
                    currentZoomRatioMode = zoomMode,
                    isZooming = true,
                )
            )
        }

        activeJob?.cancel()
        activeJob = viewModelScope.launch {
            changeZoomingState()
        }
    }

    fun setTakePicture(take: Boolean) {
        viewModelState.update {
            it.copy(takePicture = take)
        }
    }

    fun changeZoomRatioMode(mode: ZoomRatioMode) {
        changeZoomRatio(mode.value, mode)
    }

    fun zoomIn(zoom: Float) {
        val roundedZoom = round(zoom)
        val ratio = if (zoom == roundedZoom) roundedZoom + 1 else ceil(zoom)
        changeZoomRatio(ratio)
    }

    fun zoomOut(zoom: Float) {
        val roundedZoom = round(zoom)
        val ratio = if (zoom == roundedZoom) roundedZoom - 1 else floor(zoom)
        changeZoomRatio(ratio)
    }

    private suspend fun changeZoomingState() {
        delay(2500)
        viewModelState.update {
            it.copy(zoomInfo = it.zoomInfo.copy(isZooming = false))
        }
    }

    fun changeTorchMode(mode: TorchMode) {
        viewModelState.update {
            it.copy(currentTorchMode = mode)
        }
    }

    fun hasFlashUnit(hasFlashUnit: Boolean) {
        viewModelState.update {
            it.copy(hasFlashUnit = hasFlashUnit)
        }
    }

    fun showFocus(show: Boolean, x: Float = 0f, y: Float = 0f) {
        viewModelState.update {
            it.copy(
                scannerFocusInfo = it.scannerFocusInfo.copy(
                    x = x,
                    y = y,
                    showFocus = show,
                )
            )
        }
    }

    fun setZoomInfo(zoomState: ZoomState) {
        viewModelState.update {
            it.copy(
                zoomInfo = it.zoomInfo.copy(
                    minZoomRatio = zoomState.minZoomRatio,
                    maxZoomRatio = zoomState.maxZoomRatio
                )
            )
        }
    }

    fun interactedWithArticleDetails(postType: GarbageType) { //TODO: Open Home Article
        viewModelState.update {
            it.copy(
                selectedPostType = postType
            )
        }
    }

    companion object {
        fun provideFactory(
            repository: GarbageInfoPostsRepository,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ScannerViewModel(repository) as T
            }
        }
    }
}