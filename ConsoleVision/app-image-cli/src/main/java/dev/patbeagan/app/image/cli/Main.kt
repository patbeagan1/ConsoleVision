package dev.patbeagan.app.image.cli

import dev.patbeagan.consolevision.ConsoleVisionRuntime
import dev.patbeagan.consolevision.ImageScaler
import dev.patbeagan.consolevision.toList2D
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.ParseException
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import kotlin.system.exitProcess

val optionProvider: OptionProvider = OptionProvider()
const val commandName: String = "consolevision"

@Throws(InterruptedException::class, IOException::class)
fun main(args: Array<String>) {
    try {
        val cmd: CommandLine = optionProvider.parseArgs(args)
        when {
            cmd.hasOption("H") -> {
                HelpFormatter().printHelp("CMD", optionProvider.getOptions())
                exitProcess(0)
            }

            args.isEmpty() -> {
                val formatter = HelpFormatter()
                formatter.printHelp(
                    """$commandName <filename>""",
                    optionProvider.getOptions()
                )
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
                    config = ConsoleVisionRuntime.Config(
                        reductionRate = cmd.getOptionValue("r")?.toInt() ?: 0,
                        paletteReductionRate = cmd.getOptionValue("P")?.toInt() ?: 0,
                        isCompatPalette = cmd.hasOption("c"),
                        shouldNormalize = cmd.hasOption("n"),
                    ),
                    paletteImage = cmd.getOptionValue("p")
                        ?.let { ImageIO.read(File(it)) }
                        ?.toList2D(),
                ).getFrame(file.toList2D()).also { println(it) }
            }
        }
    } catch (e: ParseException) {
        System.err.println("Exception parsing command line: " + e.localizedMessage)
    }
}
