package com.example.recycleg.ui.scanner.camera

import android.content.Context
import android.graphics.*
import android.media.Image
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.recycleg.ml.GarbageClassifierQuant
import com.example.recycleg.model.GarbageType
import com.example.recycleg.ui.scanner.ScannerMode
import com.example.recycleg.ui.scanner.ScannerUiStateProvider
import org.tensorflow.lite.DataType
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.common.ops.QuantizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.model.Model
import java.io.ByteArrayOutputStream


class GarbageAnalyzer(
    private val context: Context,
    private val scannerUiStateProvider: ScannerUiStateProvider,
    private val onGarbageScanned: (GarbageType) -> Unit,
) : ImageAnalysis.Analyzer {

    private val garbageModel: GarbageClassifierQuant by lazy {
        val compatList = CompatibilityList()

        val options = if (compatList.isDelegateSupportedOnThisDevice) {
            Model.Options.Builder().setDevice(Model.Device.GPU).build()
        } else {
            Model.Options.Builder().setNumThreads(4).build()
        }

        GarbageClassifierQuant.newInstance(context, options)
    }

    private val supportedImageFormats = listOf(
        ImageFormat.YUV_420_888,
    )
    private val frameCount = 10
    private var counter = 0

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(image: ImageProxy) {
        image.use {
            if (image.format !in supportedImageFormats) return
            val bitmap = image.image?.toBitmap() ?: return

            val uiState = scannerUiStateProvider.getScannerUiState()
            val scannerMode = uiState.currentScannerMode
            val takePicture = uiState.takePicture

            if (scannerMode == ScannerMode.PHOTO && !takePicture) return
            if (scannerMode == ScannerMode.LIVE) {
                if ((++counter % frameCount) != 0) return
                else counter = 0
            }

            val imageProcessor = ImageProcessor.Builder()
                .add(ResizeOp(256, 256, ResizeOp.ResizeMethod.BILINEAR))
                .add(NormalizeOp(127.5f, 127.5f))
                .add(QuantizeOp(128.0f, 1 / 128.0f))
                .build()

            val tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(bitmap)

            val tfImage = imageProcessor.process(tensorImage)
            val output = garbageModel.process(tfImage)
                .probabilityAsCategoryList.apply {
                    sortByDescending { it.score } // Sort with highest confidence first
                }.first()

            val garbageType = when (output.label) {
                "0" -> GarbageType.Paper
                "1" -> GarbageType.Glass
                "2" -> GarbageType.Metal
                "3" -> GarbageType.Paper
                "4" -> GarbageType.Plastic
                else -> GarbageType.Organic
            }

            onGarbageScanned(garbageType)
        }
    }

    private fun Image.toBitmap(): Bitmap {
        val yBuffer = planes[0].buffer // Y
        val vuBuffer = planes[2].buffer // VU

        val ySize = yBuffer.remaining()
        val vuSize = vuBuffer.remaining()

        val nv21 = ByteArray(ySize + vuSize)

        yBuffer.get(nv21, 0, ySize)
        vuBuffer.get(nv21, ySize, vuSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}