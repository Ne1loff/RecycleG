package com.example.recycleg.ui.scanner.camera

import android.content.Context
import android.content.res.Configuration
import android.util.Size
import android.view.View
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.recycleg.model.GarbageType
import com.example.recycleg.ui.scanner.ScannerUiStateProvider
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.TimeUnit

class CameraViewHandler(
    private val config: Configuration,
    private val uiStateProvider: ScannerUiStateProvider,
    private val onResult: (GarbageType) -> Unit,
    private val updateCameraInfo: (CameraInfo) -> Unit
) {

    private lateinit var camera: Camera
    private lateinit var previewView: PreviewView
    private lateinit var lifecycleOwner: LifecycleOwner
    private lateinit var processProviderFuture: ListenableFuture<ProcessCameraProvider>

    @Composable
    fun SetUpLifecycleAndProcessProvider(ctx: Context) {
        lifecycleOwner = LocalLifecycleOwner.current
        processProviderFuture = remember {
            ProcessCameraProvider.getInstance(ctx)
        }
    }


    fun factory(ctx: Context): View {
        previewView = PreviewView(ctx)
        previewView.scaleType = PreviewView.ScaleType.FILL_CENTER

        val screenSize = Size(config.screenWidthDp, config.screenHeightDp)

        val preview = Preview.Builder().setTargetResolution(screenSize).build()
        preview.setSurfaceProvider(previewView.surfaceProvider)

        val selector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()


        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(screenSize)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalysis.setAnalyzer(
            ContextCompat.getMainExecutor(ctx),
            GarbageAnalyzer(
                context = ctx,
                scannerUiStateProvider = uiStateProvider,
                onGarbageScanned = onResult
            )
        )

        try {
            val cameraProvider = processProviderFuture.get()

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

        camera.cameraInfo.let { updateCameraInfo.invoke(it) }

        return previewView
    }

    fun enableTorch(enable: Boolean) {
        if (!(::camera.isInitialized)) return

        val ctrl = camera.cameraControl
        ctrl.enableTorch(enable)
    }

    fun setLinearZoom(value: Float) {
        if (!(::camera.isInitialized)) return

        val ctrl = camera.cameraControl
        ctrl.setLinearZoom(value)
    }

    fun setFocus(offset: Offset) {
        if (!(::camera.isInitialized)) return
        if (!(::previewView.isInitialized)) return

        val ctrl = camera.cameraControl
        val meteringPoint = previewView.meteringPointFactory.createPoint(offset.x, offset.y)

        val action = FocusMeteringAction
            .Builder(meteringPoint)
            .setAutoCancelDuration(3, TimeUnit.SECONDS)
            .build()

        ctrl.startFocusAndMetering(action)
    }
}