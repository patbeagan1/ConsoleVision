package dev.patbeagan.app.demo.compose

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.patbeagan.consolevision.ConsoleVisionRuntime
import dev.patbeagan.consolevision.compose.TerminalCanvas
import dev.patbeagan.consolevision.compose.rememberFrameRate
import dev.patbeagan.consolevision.style.ColorInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.concurrent.fixedRateTimer

val mona: ImageBitmap by lazy {
    ImageIO.read(
        File(
            "/home/patrick/repo/Internal/ConsoleVision/assets/mona-lisa.jpeg"
        )
    ).let {
        AffineTransformOp(
            AffineTransform().apply {
                scale(0.01, 0.01)
            }, AffineTransformOp.TYPE_BILINEAR
        ).filter(
            it, BufferedImage(
                it.width, it.height, BufferedImage.TYPE_INT_ARGB
            )
        )
    }.toComposeImageBitmap()
}

val consoleVisionRuntime = ConsoleVisionRuntime(
    ConsoleVisionRuntime.Config(
        reductionRate = 0,
        paletteReductionRate = 0,
        isCompatPalette = false,
        shouldNormalize = false,
    )
)

fun main() = runBlocking {
//    val canRender = MutableStateFlow(0)
//    fixedRateTimer(period = 1000 / 1) {
//        canRender.value += 1
//    }

    application {
        Window(
            visible = false,
            create = { ComposeWindow() },
            dispose = { it.dispose() }
        ) {
//            var count by remember { mutableStateOf(0) }

            val infiniteTransition = rememberInfiniteTransition()

            val limiter = remember {
                AnimationLimiter(
                    min = 0.5f,
                    max = 0.8f,
                    duration = 500,
                    fps = 30f
                )
            }
            val value by infiniteTransition.animateFloat(
                limiter.min, limiter.max, animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = limiter.duration),//AnimationConstants.DefaultDurationMillis),
                    repeatMode = RepeatMode.Reverse
                )
            )

            val animValue = remember {
                derivedStateOf { limiter.toBucketValue(value) }
            }

            val frameRate by rememberFrameRate()
            println(frameRate)

            Frame(animValue.value ?: -1)

            val scope = rememberCoroutineScope()
            LaunchedEffect(true) {
                scope.launch {
                    delay(100_000)
                }
            }
        }
//        println(AnsiConstants.CURSOR_TO_START + "framerate: $frameRate\nframe: ${count++}")

    }
}


@Composable
private fun Frame(frame: Int) {
    val paint by remember { mutableStateOf(Paint()) }

    TerminalCanvas(
        Modifier.background(Color.Black),
        consoleVisionRuntime,
        100,
        100,
//            { latestFrame = it }
    ) {
        drawRoundRect(
            Brush.horizontalGradient(listOf(Color.Gray, Color(66, 0, 66))),
            size = this.size,
            cornerRadius = CornerRadius(10f, 10f)
        )
        it.drawText(x = 24, y = 7, text = "Test", colorBackground = ColorInt(0xaaffaa))
        it.drawText(34, 4, "Test", ColorInt.from(256, 256))
        drawIntoCanvas { canvas ->
            canvas.drawImage(
                mona,
                Offset(0f, 10f * frame),
                paint
            )
            canvas.drawCircle(center, 20f * frame, paint.apply {
                color = Color.Blue
                isAntiAlias = false
                filterQuality = FilterQuality.None
            })
        }
        drawCircle(Color.Red, 5f)
        drawLine(Color.Green, Offset(1f, 3f), Offset(30f, 10f))
    }
}
