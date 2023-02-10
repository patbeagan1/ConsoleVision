package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.server.Server
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
    val options = getOptions()
    val parser: CommandLineParser = PosixParser()

    try {
        val cmd: CommandLine = parser.parse(options, args)
        when {
            cmd.hasOption("H") -> {
                HelpFormatter().printHelp("CMD", options)
                exitProcess(0)
            }

            else ->
                Server().startServer()
        }
    } catch (e: ParseException) {
        System.err.println("Exception parsing command line: " + e.localizedMessage)
    }
}

private fun getOptions() = Options().apply {
    addOption(
        "H",
        "help",
        false,
        "prints this help text and exits."
    )
}

