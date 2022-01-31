package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.Router.getHome
import dev.patbeagan.consolevision.Router.getImage
import dev.patbeagan.consolevision.Router.postUpload
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import org.apache.commons.cli.PosixParser
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import kotlin.system.exitProcess

@Throws(InterruptedException::class, IOException::class)
fun main(args: Array<String>) {
    val options = Options().apply {
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
            "s",
            "server",
            false,
            "starts a server that will handle multiple requests."
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
            cmd.hasOption("s") -> {
                startServer()
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
                    paletteImage = cmd.getOptionValue("p")?.let { ImageIO.read(File(it)) },
                    reductionRate = cmd.getOptionValue("r")?.toInt() ?: 0,
                    paletteReductionRate = cmd.getOptionValue("P")?.toInt() ?: 0,
                    isCompatPalette = cmd.hasOption("c"),
                    shouldNormalize = cmd.hasOption("n"),
                ).printFrame(file).also { println(it) }
            }
        }
    } catch (e: ParseException) {
        System.err.println("Exception parsing command line: " + e.localizedMessage)
    }
}

private fun startServer() {
    embeddedServer(Netty, port = 3000) {
        routing {
            getHome()
            postUpload()
            getImage()
        }
    }.start(wait = true)
}

