package dev.patbeagan.consolevision

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.use
import androidx.compose.ui.window.application
import dev.patbeagan.consolevision.ansi.AnsiConstants
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import org.apache.commons.cli.PosixParser
import org.jetbrains.skia.Bitmap
import org.jetbrains.skiko.toBufferedImage
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import kotlin.concurrent.fixedRateTimer
import kotlin.system.exitProcess

val mona: ImageBitmap by lazy {
    ImageIO.read(File("./assets/mona-lisa.jpeg")).let {
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
    null, ConsoleVisionRuntime.Config(
        reductionRate = 0,
        paletteReductionRate = 0,
        isCompatPalette = false,
        shouldNormalize = false,
    )
)

var latestFrame: String = ""

fun main() = runBlocking {
    fixedRateTimer(period = 1000 / 30) {
        print(AnsiConstants.CURSOR_TO_START + latestFrame)
    }
    application {
        val infiniteTransition = rememberInfiniteTransition()
        val value by infiniteTransition.animateFloat(
            0.5f, 0.8f, animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 500),//AnimationConstants.DefaultDurationMillis),
                repeatMode = RepeatMode.Reverse
            )
        )
        var count by remember { mutableStateOf(0) }
        val frameRate by rememberFrameRate()
        val paint by remember { mutableStateOf(Paint()) }

        TerminalCanvas(
            Modifier.background(Color.Black),
            consoleVisionRuntime,
            100,
            100,
            { latestFrame = it }
        ) {
            drawRoundRect(
                Brush.horizontalGradient(listOf(Color.Gray, Color(66, 0, 66))),
                size = this.size, cornerRadius = CornerRadius(10f, 10f)
            )
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

@Composable
private fun rememberFrameRate(): State<Int> {
    val frameRate = remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        var frameCount = 0
        var prevTime = withFrameNanos { it }

        while (isActive) {
            withFrameNanos {
                frameCount++

                val seconds = (it - prevTime) / 1E9 // 1E9 nanoseconds is 1 second
                if (seconds >= 1) {
                    frameRate.value = ((frameCount / seconds).toInt())
                    prevTime = it
                    frameCount = 0
                }
            }
        }
    }
    return frameRate
}

/**
 * Creates a canvas which will render colorized output to the terminal
 * via ANSI escape codes.
 */
@Composable
fun TerminalCanvas(
    modifier: Modifier = Modifier,
    consoleVisionRuntime: ConsoleVisionRuntime,
    width: Int = 80,
    height: Int = 72,
    onRender: (String) -> Unit = { println(it) },
    content: DrawScope.() -> Unit,
) {
    ImageComposeScene(
        width,
        height,
    ) {
        Canvas(
            modifier.fillMaxSize()
        ) {
            content()
        }
    }.use { scene ->
        scene.render()
            .use { Bitmap.makeFromImage(it) }
            .toBufferedImage()
            .toList2D()
            .let { consoleVisionRuntime.printFrame(it) }
            .also { onRender(it) }
    }
}

@Throws(InterruptedException::class, IOException::class)
fun main2(args: Array<String>) {
    val options = getOptions()
    val parser: CommandLineParser = PosixParser()

    try {
        val cmd: CommandLine = parser.parse(options, args)
        when {
            cmd.hasOption("H") -> {
                HelpFormatter().printHelp("CMD", options)
                exitProcess(0)
            }

            args.isEmpty() -> {
                val formatter = HelpFormatter()
                formatter.printHelp(Runtime::class.java.canonicalName + " <filename>", options)
            }

            else -> {
                val width = cmd.getOptionValue("w")?.toInt()
                val height = cmd.getOptionValue("h")?.toInt()
                val file = cmd.getOptionValue("f")
                    ?.let { ImageIO.read(File(it)) }
                    ?.let {
                        ImageScaler(width, height).scaledImage(it)
                    } ?: run {
                    println("Could not process the file!")
                    return
                }
                ConsoleVisionRuntime(
                    paletteImage = cmd.getOptionValue("p")
                        ?.let { ImageIO.read(File(it)) }
                        ?.toList2D(),
                    ConsoleVisionRuntime.Config(
                        reductionRate = cmd.getOptionValue("r")?.toInt() ?: 0,
                        paletteReductionRate = cmd.getOptionValue("P")?.toInt() ?: 0,
                        isCompatPalette = cmd.hasOption("c"),
                        shouldNormalize = cmd.hasOption("n"),
                    )
                ).printFrame(file.toList2D()).also { println(it) }
            }
        }
    } catch (e: ParseException) {
        System.err.println("Exception parsing command line: " + e.localizedMessage)
    }
}

private fun getOptions() = Options().apply {
    addOption(
        "v",
        "version",
        false,
        "version of this library"
    )
    addOption(
        "r",
        "reduced",
        true,
        "reduces the color space by the given amount, 256/x"
    )
    addOption(
        "c",
        "compat",
        false,
        "requests to use the reduced ansi color set"
    )
    addOption(
        "n",
        "normalize",
        false,
        "normalizes the color value so it will use the maximum amount of the color palette."
    )
    addOption(
        "f",
        "file",
        true,
        "the file to be read"
    )
    addOption(
        "p",
        "palette",
        true,
        "indicates an optional palette file which will be used to color the output."
    )
    addOption(
        "P",
        "reducePalette",
        true,
        "reduces the color space of the palette file by the given amount, 256/x"
    )
    addOption(
        "w",
        "width",
        true,
        "the maximum width of the generated video"
    )
    addOption(
        "h",
        "height",
        true,
        "the maximum height of the generated video"
    )
    addOption(
        "H",
        "help",
        false,
        "prints this help text and exits."
    )
}

