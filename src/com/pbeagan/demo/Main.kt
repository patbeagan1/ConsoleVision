package com.pbeagan.demo

import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import org.apache.commons.cli.PosixParser
import java.io.IOException

@Throws(InterruptedException::class, IOException::class)
fun main(args: Array<String>) {
    val options = Options().apply {
        addOption("v", "version", false, "version of this library")
        addOption("r", "reduced", true, "reduces the color space by the given amount, 256/x")
        addOption("c", "compat", false, "requests to use the reduced ansi color set")
        addOption("f", "file", true, "requests to use the reduced ansi color set")
        addOption("p", "palette", true, "requests to use the reduced ansi color set")
        addOption("w", "width", true, "the maximum width of the generated video")
        addOption("h", "height", true, "the maximum height of the generated video")
    }
    val parser: CommandLineParser = PosixParser()

    try {
        val cmd: CommandLine = parser.parse(options, args)
        when {
            cmd.hasOption("v") -> {
                // let's find what version of the library we're running
                val version: String = io.humble.video_native.Version.getVersionInfo()
                println("Humble Version: $version")
            }
            args.isEmpty() -> {
                val formatter = HelpFormatter()
                formatter.printHelp(Decoder::class.java.canonicalName + " <filename>", options)
            }
            else -> {
                Decoder(
                    filename = cmd.getOptionValue("f"),
                    palette = cmd.getOptionValue("p"),
                    reductionRate = cmd.getOptionValue("r")?.toInt() ?: 0,
                    isCompatPalette = cmd.hasOption("c"),
                    width = cmd.getOptionValue("w")?.toInt(),
                    height = cmd.getOptionValue("h")?.toInt()
                ).playVideo()
            }
        }
    } catch (e: ParseException) {
        System.err.println("Exception parsing command line: " + e.localizedMessage)
    }
}