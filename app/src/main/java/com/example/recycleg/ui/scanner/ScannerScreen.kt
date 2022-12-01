package com.example.recycleg.ui.scanner

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.FlashOff
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.ViewInAr
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.recycleg.R
import com.example.recycleg.model.GarbageType
import com.example.recycleg.ui.components.*
import com.example.recycleg.ui.scanner.camera.CameraViewHandler
import com.example.recycleg.ui.theme.Elevation
import com.example.recycleg.ui.utils.conditional
import kotlinx.coroutines.launch
import java.util.*


enum class ScannerMode {
    LIVE,
    PHOTO
}

enum class TorchMode {
    TORCH_ON,
    TORCH_OFF,
}

enum class ZoomRatioMode(val value: Float) {
    X1(1f),
    X2(2f),
    X3(3f),
    X4(4f)
}

val uiStateProvider = ScannerUiStateProvider()

@Composable
fun uiComponentsColor() = MaterialTheme.colorScheme.primary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun GarbageScannerScreen(
    scannerViewModel: ScannerViewModel,
    navigateBack: () -> Unit
) {
    val uiState by scannerViewModel.uiState.collectAsState()
    uiStateProvider.setScannerUiState(uiState)

    val config = LocalConfiguration.current
    val context = LocalContext.current


    val onScannerModeChange: (ScannerMode) -> Unit = scannerViewModel::changeScannerMode
    val onScannerTorchMode: (TorchMode) -> Unit = scannerViewModel::changeTorchMode
    val onScannerZoomRatioMode: (ZoomRatioMode) -> Unit = scannerViewModel::changeZoomRatioMode
    val updateCameraInfo: (CameraInfo) -> Unit = {
        scannerViewModel.hasFlashUnit(it.hasFlashUnit())
        it.zoomState.value?.let { state -> scannerViewModel.setZoomInfo(state) }
    }

    var garbageType by remember {
        mutableStateOf<GarbageType?>(null)
    }

    var hasCamPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCamPermission = granted
        }
    )
    LaunchedEffect(key1 = true) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    val scope = rememberCoroutineScope()
    val state = rememberBottomSheetScaffoldState()

    fun onScan(type: GarbageType) {
        garbageType = type
        if (uiState.currentScannerMode == ScannerMode.PHOTO) {
            scannerViewModel.setTakePicture(false)
            scope.launch {
                state.bottomSheetState.apply {
                    if (isCollapsed && progress.fraction == 1f) {
                        expand()
                    }
                }
            }
        }
    }

    val cameraViewHandler by remember {
        mutableStateOf(
            CameraViewHandler(
                config = config,
                updateCameraInfo = updateCameraInfo,
                onResult = ::onScan,
                uiStateProvider = uiStateProvider,
            )
        )
    }


    val onTakePicture: (Boolean) -> Unit = {
        scannerViewModel.setTakePicture(true)
    }

    BottomSheetScaffold(
        sheetContent = {
            BottomSheetContent {
                garbageType?.let {
                    ResultBottomSheet(uiState = uiState, garbageType = it, onActionClick = {})
                }
            }
        },
        scaffoldState = state,
        sheetPeekHeight = 0.dp,
        sheetBackgroundColor = MaterialTheme.colorScheme.surface,
        sheetContentColor = MaterialTheme.colorScheme.onSurface,
        sheetShape = MaterialTheme.shapes.extraLarge.copy(
            bottomStart = CornerSize(0.dp),
            bottomEnd = CornerSize(0.dp),
        ),
        sheetElevation = Elevation.lvl1
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Box {
                if (hasCamPermission) {
                    CameraView(
                        uiState = uiState,
                        cameraViewHandler = cameraViewHandler,
                        onTransform = scannerViewModel::changeZoomRatio,
                        onTouch = { offset ->
                            scannerViewModel.showFocus(
                                true,
                                offset.x,
                                offset.y
                            )
                        },
                    )
                    ScannerAim(config = config)
                    ZoomSlider(
                        config = config,
                        zoomInfo = uiState.zoomInfo,
                        onValueChange = scannerViewModel::changeZoomRatio,
                        onZoomIn = scannerViewModel::zoomIn,
                        onZoomOut = scannerViewModel::zoomOut
                    )
                    if (uiState.currentScannerMode == ScannerMode.LIVE) {
                        ScanResultCard(
                            config = config,
                            uiState = uiState,
                            garbageType = garbageType
                        )
                    }
                    CameraControlButtons(
                        uiState = uiState,
                        torchChange = onScannerTorchMode,
                        zoomRatioChange = onScannerZoomRatioMode,
                        onTakePicture = onTakePicture
                    )

                    val focusInfo = uiState.focusInfo
                    if (focusInfo.showFocus) {
                        DrawFocusCircle(focusInfo.x.dp, focusInfo.y.dp) {
                            scannerViewModel.showFocus(false)
                        }
                    }

                }
            }
            ScannerTopBar(
                scannerMode = uiState.currentScannerMode,
                navigateBack = navigateBack,
                onActionClick = {
                    when (uiState.currentScannerMode) {
                        ScannerMode.LIVE -> onScannerModeChange.invoke(ScannerMode.PHOTO)
                        ScannerMode.PHOTO -> onScannerModeChange.invoke(ScannerMode.LIVE)
                    }
                },
            )
        }
    }
}

@Composable
private fun BoxScope.ZoomSlider(
    config: Configuration,
    zoomInfo: ScannerZoomInfo,
    onValueChange: (Float) -> Unit,
    onZoomIn: (Float) -> Unit,
    onZoomOut: (Float) -> Unit,
) {

    AnimatedVisibility(
        visible = zoomInfo.isZooming,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .padding(bottom = 160.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val modifier = Modifier.padding(horizontal = 4.dp)

            IconButton(
                onClick = { onZoomOut(zoomInfo.currentZoomRatio) },
                modifier = modifier
            ) {
                Icon(
                    imageVector = Icons.Filled.Remove,
                    contentDescription = stringResource(id = R.string.scanner_zoom_out),
                    tint = uiComponentsColor(),
                )
            }
            Slider(
                value = zoomInfo.currentZoomRatio,
                valueRange = zoomInfo.minZoomRatio..zoomInfo.maxZoomRatio,
                onValueChange = onValueChange,
                modifier = modifier.width((config.screenWidthDp * .6).dp)
            )
            IconButton(
                onClick = { onZoomIn(zoomInfo.currentZoomRatio) },
                modifier = modifier
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.scanner_zoom_in),
                    tint = uiComponentsColor(),
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun BoxScope.CameraControlButtons(
    uiState: ScannerUiState,
    torchChange: (TorchMode) -> Unit,
    zoomRatioChange: (ZoomRatioMode) -> Unit,
    onTakePicture: (Boolean) -> Unit
) {
    val torchMode: TorchMode = uiState.currentTorchMode
    val zoomInfo: ScannerZoomInfo = uiState.zoomInfo
    val hasFlashUnit: Boolean = uiState.hasFlashUnit
    val hasZoom: Boolean = uiState.zoomInfo.maxZoomRatio > uiState.zoomInfo.minZoomRatio
    val isPhotoMode: Boolean = uiState.currentScannerMode == ScannerMode.PHOTO

    Row(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        if (hasZoom) {
            IconButton(onClick = {
                val mode = when (zoomInfo.currentZoomRatioMode) {
                    ZoomRatioMode.X1 -> ZoomRatioMode.X2
                    ZoomRatioMode.X2 -> ZoomRatioMode.X3
                    ZoomRatioMode.X3 -> ZoomRatioMode.X4
                    ZoomRatioMode.X4 -> ZoomRatioMode.X1
                }
                zoomRatioChange(mode)
            }) {
                AnimatedContent(
                    targetState = zoomInfo.currentZoomRatioMode,
                    transitionSpec = {
                        // Compare the incoming number with the previous number.
                        if (targetState > initialState) {
                            // If the target number is larger, it slides up and fades in
                            // while the initial (smaller) number slides up and fades out.
                            slideInVertically { height -> height } + fadeIn() with
                                    slideOutVertically { height -> -height } + fadeOut()
                        } else {
                            // If the target number is smaller, it slides down and fades in
                            // while the initial number slides down and fades out.
                            slideInVertically { height -> -height } + fadeIn() with
                                    slideOutVertically { height -> height } + fadeOut()
                        }.using(
                            // Disable clipping since the faded slide-in/out should
                            // be displayed out of bounds.
                            SizeTransform(clip = false)
                        )
                    }
                ) { target ->
                    when (target) {
                        ZoomRatioMode.X1 -> Text(
                            text = "1X",
                            color = uiComponentsColor()
                        )
                        ZoomRatioMode.X2 -> Text(
                            text = "2X",
                            color = uiComponentsColor()
                        )
                        ZoomRatioMode.X3 -> Text(
                            text = "3X",
                            color = uiComponentsColor()
                        )
                        ZoomRatioMode.X4 -> Text(
                            text = "4X",
                            color = uiComponentsColor()
                        )
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        if (isPhotoMode) {
            AnimatedButton(
                onClick = { onTakePicture(true) },
                enabled = !uiState.takePicture,
                modifier = Modifier
                    .padding(16.dp)
                    .width(64.dp)
                    .height(64.dp)
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        if (hasFlashUnit) {
            IconButton(onClick = {
                val mode = when (torchMode) {
                    TorchMode.TORCH_ON -> TorchMode.TORCH_OFF
                    TorchMode.TORCH_OFF -> TorchMode.TORCH_ON
                }
                torchChange(mode)
            }) {
                Crossfade(
                    targetState = torchMode,
                ) {
                    when (it) {
                        TorchMode.TORCH_ON -> Icon(
                            imageVector = Icons.Outlined.FlashOff,
                            tint = uiComponentsColor(),
                            contentDescription = stringResource(R.string.scanner_flash_off),
                        )
                        TorchMode.TORCH_OFF -> Icon(
                            imageVector = Icons.Outlined.FlashOn,
                            tint = uiComponentsColor(),
                            contentDescription = stringResource(R.string.scanner_flash_on),
                        )
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ResultBottomSheet(
    uiState: ScannerUiState,
    garbageType: GarbageType,
    onActionClick: () -> Unit,
) {
    val garbageName = when (uiState is ScannerUiState.HasGarbageInfoPosts) {
        true -> uiState.garbagePostsFeed.allInfoPosts.find { it.type == garbageType }?.title
            ?: garbageType.name
        false -> garbageType.name
    }

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(
                id = R.string.scanner_result_title,
                formatArgs = arrayOf(garbageName)
            ),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge
        )
        Image(
            painter = painterResource(id = getIconNumFromGarbageType(garbageType)),
            contentDescription = garbageType.name,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = onActionClick) {
            Text(text = "Узнать больше о ${garbageType.name.lowercase(Locale.getDefault())}")
            Icon(
                imageVector = Icons.Filled.ArrowRight,
                contentDescription = "",
            )
        }
    }
}

@Composable
private fun CameraView(
    uiState: ScannerUiState,
    cameraViewHandler: CameraViewHandler,
    onTouch: (Offset) -> Unit,
    onTransform: (Float) -> Unit,
) {
    LockScreenOrientation(Configuration.ORIENTATION_PORTRAIT)
    cameraViewHandler.SetUpLifecycleAndProcessProvider(LocalContext.current)

    cameraViewHandler.enableTorch(uiState.currentTorchMode == TorchMode.TORCH_ON)
    val linearZoom = uiState.zoomInfo.currentZoomRatio / uiState.zoomInfo.maxZoomRatio
    cameraViewHandler.setLinearZoom(linearZoom)

    fun onTap(offset: Offset) {
        cameraViewHandler.setFocus(offset)
        onTouch.invoke(offset)
    }

    val state = rememberTransformableState { zoomChange, _, _ ->
        onTransform(uiState.zoomInfo.currentZoomRatio * zoomChange)
    }

    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures(onTap = ::onTap) }
            .transformable(state),
        factory = cameraViewHandler::factory
    )
}


@Composable
private fun BoxScope.ScanResultCard(
    config: Configuration,
    uiState: ScannerUiState,
    garbageType: GarbageType?
) {
    val isPortrait = config.orientation == Configuration.ORIENTATION_PORTRAIT
    Box(
        modifier = Modifier
            .conditional(
                isPortrait,
                ifTrue = { align(Alignment.BottomStart) },
                ifFalse = { align(Alignment.CenterEnd) }
            )
            .padding(24.dp, 0.dp, 24.dp, 48.dp)
    ) {
        val garbageInfo = when (uiState) {
            is ScannerUiState.HasGarbageInfoPosts -> uiState
                .garbagePostsFeed
                .allInfoPosts
                .find { it.type == garbageType }
            is ScannerUiState.NoGarbageInfoPosts -> null
        }

        if (garbageInfo != null) {
            if (isPortrait)
                GarbageCard(
                    garbageInfoPost = garbageInfo,
                    navigateToArticle = {}
                )
            else ReducedGarbageCard(
                garbageInfoPost = garbageInfo,
                navigateToArticle = {}
            )
        }
    }
}

@Composable
private fun BoxScope.ScannerAim(config: Configuration) {
    Box(
        modifier = Modifier
            .align(Alignment.Center)
    ) {
        Icon(
            painter = painterResource(R.drawable.scanner_aim),
            tint = uiComponentsColor(),
            contentDescription = "Scanner Aim",
            modifier = Modifier.conditional(
                config.orientation == Configuration.ORIENTATION_LANDSCAPE,
                ifTrue = { rotate(90f) },
                ifFalse = { padding(0.dp, 0.dp, 0.dp, 80.dp) }
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScannerTopBar(
    scannerMode: ScannerMode,
    navigateBack: () -> Unit,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    scrollBehavior: TopAppBarScrollBehavior? =
        TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.scanner_title),
                color = uiComponentsColor(),
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = navigateBack, modifier = Modifier.padding(8.dp)) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.app_back),
                )
            }
        },
        actions = {
            IconButton(onClick = onActionClick, modifier = Modifier.padding(8.dp)) {
                Crossfade(targetState = scannerMode == ScannerMode.PHOTO) { isPhoto ->
                    if (isPhoto) Icon(
                        imageVector = Icons.Outlined.PhotoCamera,
                        contentDescription = stringResource(R.string.app_back),
                    ) else Icon(
                        imageVector = Icons.Outlined.ViewInAr,
                        contentDescription = stringResource(R.string.app_back),
                    )
                }

            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent.copy(
                alpha = 0f
            ),
            navigationIconContentColor = uiComponentsColor(),
            actionIconContentColor = uiComponentsColor(),
        ),
        modifier = modifier
    )
}

@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
        }
    }
}


fun getIconNumFromGarbageType(type: GarbageType): Int {
    return when (type) {
        GarbageType.Paper -> R.drawable.paper_illustration
        GarbageType.Glass -> R.drawable.glass_illustration
        GarbageType.Metal -> R.drawable.metal_illustration
        GarbageType.Organic -> R.drawable.organic_illustration
        GarbageType.Plastic -> R.drawable.plastic_illustration
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

