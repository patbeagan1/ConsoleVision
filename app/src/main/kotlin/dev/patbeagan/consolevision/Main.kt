package dev.patbeagan.consolevision

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.patbeagan.consolevision.types.List2D
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import org.apache.commons.cli.PosixParser
import org.jetbrains.skia.Bitmap
import org.jetbrains.skiko.toBufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import kotlin.system.exitProcess

fun main() = application {

    ImageComposeScene(
        20,
        20,
    ) {
        androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
            drawCircle(Color.Red)
        }
    }.render().use {
        val bitmap = Bitmap.makeFromImage(it)
        val image = bitmap.toBufferedImage()
        val l2d = image.toList2D()
        ConsoleVisionRuntime(
            null,
            ConsoleVisionRuntime.Config(
                reductionRate = 0,
                paletteReductionRate = 0,
                isCompatPalette = false,
                shouldNormalize = false,
            )
        ).printFrame(l2d).also { println(it) }
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Compose for Desktop",
        state = rememberWindowState(width = 300.dp, height = 300.dp)
    ) {
        val count = remember { mutableStateOf(0) }
        MaterialTheme {
            Column(Modifier.fillMaxSize(), Arrangement.spacedBy(5.dp)) {
                Button(modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        count.value++
                    }) {
                    Text(if (count.value == 0) "Hello World" else "Clicked ${count.value}!")
                }
                Button(modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        count.value = 0
                    }) {
                    Text("Reset")
                }
            }
        }
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

