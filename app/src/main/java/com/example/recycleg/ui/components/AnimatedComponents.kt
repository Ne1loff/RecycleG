package com.example.recycleg.ui.components

import android.content.res.Configuration
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.recycleg.ui.theme.RecycleGTheme
import kotlinx.coroutines.delay

@Composable
fun DrawFocusCircle(
    x: Dp, y: Dp,
    outerColor: Color = Color.White,
    innerColor: Color = Color.LightGray,
    onEnd: () -> Unit) {
    val innerStartRadius = 16f
    val innerEndRadius = 60f

    val outerStartRadius = 128f
    val outerEndRadius = 68f

    val innerAnimateFloat = remember { Animatable(innerStartRadius) }
    LaunchedEffect(innerAnimateFloat) {
        val innerResult = innerAnimateFloat.animateTo(
            targetValue = innerEndRadius,
            animationSpec = tween(durationMillis = 300, easing = LinearEasing)
        )

        while (innerResult.endState.isRunning) continue
        delay(50)
        onEnd()
    }
    Canvas(modifier = Modifier) {
        drawCircle(
            color = outerColor,
            radius =
            if (outerStartRadius - innerAnimateFloat.value > outerEndRadius)
                outerStartRadius - innerAnimateFloat.value
            else
                outerEndRadius,
            center = Offset(x.value, y.value),
            style = Stroke(5f)
        )
        drawCircle(
            color = innerColor,
            radius = innerAnimateFloat.value,
            center = Offset(x.value, y.value),
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewFocusCircle() {
    RecycleGTheme {
        var show by remember {
            mutableStateOf(false)
        }
        Surface(modifier = Modifier.fillMaxSize()) {
            if (show) DrawFocusCircle(x = 100.dp, y = 100.dp, outerColor = Color.Black) {
                show = !show
            }
            Button(onClick = { show = !show }) {
                Text(text = "Some text")
            }
        }
    }
}