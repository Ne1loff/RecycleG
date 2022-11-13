package com.example.recycleg.ui.components

import android.content.res.Configuration
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.recycleg.ui.theme.RecycleGTheme
import kotlinx.coroutines.delay

@Composable
fun DrawFocusCircle(
    x: Dp, y: Dp,
    outerColor: Color = Color.White,
    innerColor: Color = Color.LightGray,
    onEnd: () -> Unit
) {
    val innerStartRadius = 16f
    val innerEndRadius = 60f

    val outerStartRadius = 128f
    val outerEndRadius = 68f

    val innerAnimateFloat = remember { Animatable(innerStartRadius) }
    LaunchedEffect(innerAnimateFloat) { // TODO
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

@Composable
fun AnimatedButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    outerColor: Color = Color.White,
    innerColor: Color = Color.White,
) {
    val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    var size by remember { mutableStateOf(IntSize.Zero) }

    val radius = (size / 2).width * 1f

    val alpha: Float by animateFloatAsState(
        targetValue = if (isPressed) 1f else 0f,
        animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing)
    )
    val outerRadius: Float by animateFloatAsState(
        targetValue = if (isPressed) radius * 1.1f else radius,
        animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing)
    )
    Box(
        modifier = modifier
            .onSizeChanged { size = it }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = if (enabled) outerColor else outerColor.copy(alpha = .4f),
                radius = outerRadius,
                style = Stroke(8f)
            )
            drawCircle(
                color = if (enabled) innerColor else innerColor.copy(alpha = .4f),
                radius = radius * .9f,
                alpha = alpha
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewFocusCircle() {
    RecycleGTheme {
        var show by remember {
            mutableStateOf(false)
        }
        Box(modifier = Modifier.fillMaxSize()) {
            if (show) DrawFocusCircle(x = 200.dp, y = 400.dp, outerColor = Color.Black) {
                show = !show
            }
            Row(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                /* Button(
                     onClick = { show = !show },
                     modifier = Modifier
                         .padding(16.dp)
                         .width(128.dp)
                         .height(64.dp)
                 ) {
                     Text(text = "Some text")
                 }*/
                AnimatedButton(
                    onClick = { show = !show },
                    enabled = !show,
                    modifier = Modifier
                        .padding(16.dp)
                        .width(64.dp)
                        .height(64.dp)
                )
            }

        }
    }
}