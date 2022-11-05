package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.ansi.AnsiColor
import dev.patbeagan.consolevision.ansi.StyleExtensions.style
import dev.patbeagan.consolevision.imagefilter.ColorMutation
import dev.patbeagan.consolevision.imagefilter.ColorNormalization
import dev.patbeagan.consolevision.types.ColorInt
import dev.patbeagan.consolevision.types.ColorPalette
import dev.patbeagan.consolevision.types.CompressionStyle
import dev.patbeagan.consolevision.types.CompressionStyle.LOWER_HALF
import dev.patbeagan.consolevision.util.memoize
import java.awt.image.BufferedImage

/**
 * A class that will return an Ansi image frame as a string.
 */
class ImagePrinter(
    private val reductionRate: Int,
    private val colorMapToAnsi: ColorMapToAnsi,
    private val shouldNormalizeColors: Boolean,
    private val shouldMutateColors: Boolean = false,
    private val paletteColors: ColorPalette? = null,
    private val compressionStyle: CompressionStyle = LOWER_HALF,
) {
    private val applyPalette: (ColorInt, ColorPalette?) -> ColorInt =
        { colorInt: ColorInt, palette: ColorPalette? ->
            palette?.matchColor(colorInt) ?: colorInt
        }.memoize()

    fun getFrame(read: BufferedImage): String {
        applyFilters(read)
        val out = buildOutput(read, compressionStyle, paletteColors)
        return out.toString()
    }

    private fun buildOutput(
        read: BufferedImage,
        compressionStyle: CompressionStyle,
        paletteColors: ColorPalette?
    ): StringBuilder {
        val out = StringBuilder()
        read.withDoubledLine({ out.append("\n") }) { (firstY, secondY), x ->

            // since there will be 2 colors printed per row,
            // we actually have 2 y values per row.
            // this part decides which one will be
            // the foreground vs the background.
            val foreground = secondY ?: firstY
            val background = if (foreground != firstY) firstY else null

            compressionStyle.symbol.style(
                colorForeground = ansiColor(read, paletteColors, foreground, x),
                colorBackground = ansiColor(read, paletteColors, background, x),
            ).also { out.append(it) }
        }
        return out
    }

    private fun ansiColor(
        read: BufferedImage,
        paletteColors: ColorPalette?,
        y: Int?,
        x: Int
    ): AnsiColor = colorMapToAnsi.convertToAnsi(
        if (y == null) {
            // there are an odd number of lines
            // we'll need to fill with the default color
            null
        } else {
            applyPalette(
                ColorInt(read.getRGB(x, y)).reduceColorSpaceBy(reductionRate),
                paletteColors
            )
        }
    )

    private fun applyFilters(read: BufferedImage) {
        if (shouldMutateColors) {
            read.applyFilter(ColorMutation(50))
        }
        if (shouldNormalizeColors) {
            read.applyFilter(ColorNormalization())
        }
    }

    private inline fun BufferedImage.withDoubledLine(
        onLineEnd: () -> Unit = {},
        action: (Pair<Int?, Int?>, Int) -> Unit,
    ) {
        val chunked = if ((height - minY) % 2 == 0) {
            (minY until height).chunked(2)
        } else {
            // if there are an odd number of lines, we want to start with an odd chunk.
            // this will make it start on a foreground, instead of a background.
            // Otherwise, the last line would be 2 pixels tall.
            listOf(listOf(minY)) + (minY + 1 until height).chunked(2)
        }
        chunked.forEach { y ->
            (minX until width).forEach { x ->
                action(y.getOrNull(0) to y.getOrNull(1), x)
            }
            onLineEnd()
        }
    }
}
