package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.ansi.AnsiColor
import dev.patbeagan.consolevision.imagefilter.ColorMutation
import dev.patbeagan.consolevision.imagefilter.ColorNormalization
import dev.patbeagan.consolevision.types.ColorInt
import dev.patbeagan.consolevision.types.ColorPalette
import dev.patbeagan.consolevision.types.CompressionStyle
import dev.patbeagan.consolevision.types.CompressionStyle.UPPER_HALF
import dev.patbeagan.consolevision.types.List2D
import dev.patbeagan.consolevision.util.memoize

/**
 * A class that will return an Ansi image frame as a string.
 */
class ImagePrinter(
    private val reductionRate: Int,
    private val colorMapToAnsi: ColorMapToAnsi,
    private val shouldNormalizeColors: Boolean,
    private val shouldMutateColors: Boolean = false,
    private val paletteColors: ColorPalette? = null,
    private val compressionStyle: CompressionStyle = UPPER_HALF,
) {
    private val applyPalette: (ColorInt, ColorPalette?) -> ColorInt =
        { colorInt: ColorInt, palette: ColorPalette? ->
            palette?.matchColor(colorInt) ?: colorInt
        }.memoize()

    fun getFrame(frame: List2D<ColorInt>): String {
        applyFilters(frame)
        return frame.buildOutput(
            compressionStyle,
            paletteColors
        ).toString()
    }

    private fun List2D<ColorInt>.buildOutput(
        compressionStyle: CompressionStyle,
        paletteColors: ColorPalette?
    ): StringBuilder {
        val out = StringBuilder()
        withDoubledLine({ out.append("\n") }) { firstY, secondY, x ->

            // since there will be 2 colors printed per row,
            // we actually have 2 y values per row.
            // this part decides which one will be
            // the foreground vs the background.
            val foreground = secondY ?: firstY
            val background = if (foreground != firstY) firstY else null

            compressionStyle.symbol.style(
                colorForeground = ansiColor(paletteColors, foreground, x),
                colorBackground = ansiColor(paletteColors, background, x),
            ).also { out.append(it) }
        }
        return out
    }

    private fun List2D<ColorInt>.ansiColor(
        paletteColors: ColorPalette?,
        y: Int?,
        x: Int
    ): AnsiColor = colorMapToAnsi.convertToAnsi(
        if (y == null) {
            // there are an odd number of lines
            // we'll need to fill with the default color
            null
        } else {
            this@ImagePrinter.applyPalette(
                at(x, y).reduceColorSpaceBy(reductionRate),
                paletteColors
            )
        }
    )

    private fun applyFilters(read: List2D<ColorInt>) {
        if (shouldMutateColors) {
            ColorMutation(50)(read)
        }
        if (shouldNormalizeColors) {
            ColorNormalization()(read)
        }
    }

    private inline fun List2D<ColorInt>.withDoubledLine(
        onLineEnd: () -> Unit = {},
        action: (Int?, Int?, Int) -> Unit,
    ) {
        val minY = 0
        val minX = 0

        val chunked = (minY until height).chunked(2)
        chunked.forEach { y ->
            (minX until width).forEach { x ->
                action(y.getOrNull(1), y.getOrNull(0), x)
            }
            onLineEnd()
        }
    }
}
