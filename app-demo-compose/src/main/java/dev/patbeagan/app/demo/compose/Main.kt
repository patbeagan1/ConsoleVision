package dev.patbeagan.app.demo.compose

import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.window.application
import dev.patbeagan.consolevision.ConsoleVisionRuntime
import dev.patbeagan.consolevision.compose.TerminalCanvas
import dev.patbeagan.consolevision.compose.rememberFrameRate
import dev.patbeagan.consolevision.style.ColorInt
import kotlinx.coroutines.runBlocking
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

val mona: ImageBitmap by lazy {
    ImageIO.read(
        File(
            "/home/patrick/repo/internal/ConsoleVision/assets/mona-lisa.jpeg"
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

var latestFrame: String = ""

fun main() = runBlocking {
//    fixedRateTimer(period = 1000 / 30) {
//        print(ConsoleVision.Special.CURSOR_TO_START + latestFrame)
//    }
    application {
//        val infiniteTransition = rememberInfiniteTransition()
//        val value by infiniteTransition.animateFloat(
//            0.5f, 0.8f, animationSpec = infiniteRepeatable(
//                animation = tween(durationMillis = 500),//AnimationConstants.DefaultDurationMillis),
//                repeatMode = RepeatMode.Reverse
//            )
//        )
        val value = 0
        var count by remember { mutableStateOf(0) }
        val frameRate by rememberFrameRate()
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
            it.drawText(34, 4, "Test", ColorInt.Companion.from(256, 256))
            drawIntoCanvas { canvas ->
                canvas.drawImage(
                    mona,
                    Offset(0f, 10f * value),
                    paint
                )
                canvas.drawCircle(center, 20f * value, paint.apply {
                    color = Color.Blue
                    isAntiAlias = false
                    filterQuality = FilterQuality.None
                })
            }
            drawCircle(Color.Red, 5f)
            drawLine(Color.Green, Offset(1f, 3f), Offset(30f, 10f))
        }

//        println(AnsiConstants.CURSOR_TO_START + "framerate: $frameRate\nframe: ${count++}")
    }
}
