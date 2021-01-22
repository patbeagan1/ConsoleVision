package com.pbeagan.demo

import com.pbeagan.demo.MainParams.FILE
import com.pbeagan.demo.MainParams.IS_COMPAT_PALETTE
import com.pbeagan.demo.MainParams.PALETTE
import com.pbeagan.demo.MainParams.REDUCTION_RATE
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import org.apache.commons.cli.PosixParser
import java.io.IOException

/**
 * Takes a media container (file) as the first argument, opens it,
 * opens up a window and plays back the video.
 *
 * @param args Must contain one string which represents a filename
 * @throws IOException
 * @throws InterruptedException
 */
@Throws(InterruptedException::class, IOException::class)
fun main(args: Array<String>) {
    val options = Options()
    options.addOption("h", "help", false, "displays help")
    options.addOption("v", "version", false, "version of this library")
    options.addOption("r", REDUCTION_RATE, true, "reduces the color space by the given amount, 256/x")
    options.addOption("c", IS_COMPAT_PALETTE, false, "requests to use the reduced ansi color set")
    options.addOption("f", FILE, true, "requests to use the reduced ansi color set")
    options.addOption("p", PALETTE, true, "requests to use the reduced ansi color set")
    val parser: CommandLineParser = PosixParser()

    try {
        val cmd: CommandLine = parser.parse(options, args)
        when {
            cmd.hasOption("v") -> {
                // let's find what version of the library we're running
                val version: String = io.humble.video_native.Version.getVersionInfo()
                println("Humble Version: $version")
            }
            cmd.hasOption("h") || args.isEmpty() -> {
                val formatter = HelpFormatter()
                formatter.printHelp(DecodeAndPlayVideo::class.java.canonicalName + " <filename>", options)
            }
            else -> {
                val reductionRate = cmd.getOptionValue("r")?.toInt() ?: 0
                val isCompatPalette = cmd.hasOption("c")
                val palette: String? = cmd.getOptionValue("p")

                val video: String? = cmd.getOptionValue("f")
                DecodeAndPlayVideo(video, palette, reductionRate, isCompatPalette).playVideo()
            }
        }
    } catch (e: ParseException) {
        System.err.println("Exception parsing command line: " + e.localizedMessage)
    }


}

object MainParams {
    const val REDUCTION_RATE = "reduced"
    const val IS_COMPAT_PALETTE = "compat"
    const val FILE = "file"
    const val PALETTE = "palette"
}

