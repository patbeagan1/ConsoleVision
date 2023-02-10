package dev.patbeagan.app.image.cli

import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.Options
import org.apache.commons.cli.PosixParser

class OptionProvider {
    val parser: CommandLineParser = PosixParser()

    fun parseArgs(args: Array<String>) = parser
        .parse(
            optionProvider.getOptions(),
            args
        )

    fun getOptions(): Options = Options().apply {
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
}