package com.example.recycleg.ui.scanner

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.util.Log
import android.util.Size
import android.view.MotionEvent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.FlashOff
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.ViewInAr
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.recycleg.R
import com.example.recycleg.model.GarbageType
import com.example.recycleg.ui.components.DrawFocusCircle
import com.example.recycleg.ui.home.GarbageCard
import com.example.recycleg.ui.home.ReducedGarbageCard
import com.example.recycleg.ui.utils.conditional
import java.util.concurrent.TimeUnit


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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GarbageScannerScreen(
    scannerViewModel: ScannerViewModel,
    navigateBack: () -> Unit
) {
    val uiState by scannerViewModel.uiState.collectAsState()

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

    val config = LocalConfiguration.current
    val context = LocalContext.current

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

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box {
            if (hasCamPermission) {
                CameraView(
                    config,
                    context,
                    uiState = uiState,
                    updateCameraInfo = updateCameraInfo,
                    onTouch = { x, y -> scannerViewModel.showFocus(true, x, y) },
                    onResult = { garbageType = it }
                )
                ScannerAim(config)
                ScanResultCard(config, uiState, garbageType)
                CameraControlButtons(
                    torchMode = uiState.currentTorchMode,
                    zoomInfo = uiState.zoomInfo,
                    hasFlashUnit = uiState.hasFlashUnit,
                    hasZoom = true, // TODO
                    torchChange = onScannerTorchMode,
                    zoomRatioChange = onScannerZoomRatioMode,
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
                    ScannerMode.LIVE -> onScannerModeChange(ScannerMode.PHOTO)
                    ScannerMode.PHOTO -> onScannerModeChange(ScannerMode.LIVE)
                }
            },
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BoxScope.CameraControlButtons(
    torchMode: TorchMode,
    zoomInfo: ScannerZoomInfo,
    hasFlashUnit: Boolean,
    hasZoom: Boolean,
    torchChange: (TorchMode) -> Unit,
    zoomRatioChange: (ZoomRatioMode) -> Unit,
) {
    Row(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
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
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        ZoomRatioMode.X2 -> Text(
                            text = "2X",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        ZoomRatioMode.X3 -> Text(
                            text = "3X",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        ZoomRatioMode.X4 -> Text(
                            text = "4X",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
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
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = stringResource(R.string.scanner_flash_off),
                        )
                        TorchMode.TORCH_OFF -> Icon(
                            imageVector = Icons.Outlined.FlashOn,
                            tint = MaterialTheme.colorScheme.onSurface,
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
private fun CameraView(
    config: Configuration,
    context: Context,
    uiState: ScannerUiState,
    updateCameraInfo: (CameraInfo) -> Unit,
    onTouch: (x: Float, y: Float) -> Unit,
    onResult: (GarbageType) -> Unit,
) {
    LockScreenOrientation(Configuration.ORIENTATION_PORTRAIT)
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }

    var camera by remember {
        mutableStateOf<Camera?>(null)
    }

    val cameraControl = camera?.cameraControl
    cameraControl?.enableTorch(uiState.currentTorchMode == TorchMode.TORCH_ON)
    cameraControl?.setZoomRatio(uiState.zoomInfo.currentZoomRatio)

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            val previewView = PreviewView(ctx)

            previewView.scaleType = PreviewView.ScaleType.FILL_CENTER

            val screenSize = Size(config.screenWidthDp, config.screenHeightDp)

            val preview = Preview.Builder().setTargetResolution(screenSize).build()
            val selector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
            preview.setSurfaceProvider(previewView.surfaceProvider)

            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(
                    Size(
                        previewView.width,
                        previewView.height
                    )
                )
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(
                ContextCompat.getMainExecutor(context),
                GarbageAnalyzer(context, uiState.currentScannerMode, onResult)
            )

            try {
                val cameraProvider = cameraProviderFuture.get()

                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    selector,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            previewView.setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        camera?.cameraControl?.let {
                            Log.i("Touch", "Hello from autofocus")
                            val meteringPoint = previewView.meteringPointFactory
                                .createPoint(event.x, event.y)
                            val action = FocusMeteringAction.Builder(meteringPoint)
                                .setAutoCancelDuration(3, TimeUnit.SECONDS)
                                .build()
                            val result = it.startFocusAndMetering(action)

                            onTouch(event.x, event.y)

                            return@setOnTouchListener result.isDone
                        }
                        return@setOnTouchListener false
                    }
                    MotionEvent.ACTION_UP -> view.performClick()
                    else -> false
                }
            }

            camera?.cameraInfo?.let(updateCameraInfo)

            previewView
        }
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
            tint = MaterialTheme.colorScheme.onSurface,
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
                color = MaterialTheme.colorScheme.onSurface,
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
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface,
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

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
