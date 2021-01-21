package com.pbeagan.demo

import org.apache.commons.cli.BasicParser
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
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
    options.addOption("r", "reduced", false, "requests to use the reduced ansi color set")
    val parser: CommandLineParser = BasicParser()
    try {
        val cmd: CommandLine = parser.parse(options, args)
        when {
            cmd.hasOption("version") -> {
                // let's find what version of the library we're running
                val version: String = io.humble.video_native.Version.getVersionInfo()
                println("Humble Version: $version")
            }
            cmd.hasOption("help") || args.isEmpty() -> {
                val formatter = HelpFormatter()
                formatter.printHelp(DecodeAndPlayVideo::class.java.canonicalName + " <filename>", options)
            }
            else -> {
                val parsedArgs: Array<String> = cmd.args
                val video = parsedArgs.getOrNull(0).also { println(it) }
                val palette = parsedArgs.getOrNull(1).also { println(it) }
                DecodeAndPlayVideo(video, palette).playVideo()
            }
        }
    } catch (e: ParseException) {
        System.err.println("Exception parsing command line: " + e.localizedMessage)
    }
}