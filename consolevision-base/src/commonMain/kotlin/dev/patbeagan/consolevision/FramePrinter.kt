package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.imagefilter.ImageFilter
import dev.patbeagan.consolevision.style.ColorInt
import dev.patbeagan.consolevision.style.ansi.ConsoleVision
import dev.patbeagan.consolevision.style.style
import dev.patbeagan.consolevision.types.ColorPalette
import dev.patbeagan.consolevision.types.CompressionStyle
import dev.patbeagan.consolevision.types.CompressionStyle.UPPER_HALF
import dev.patbeagan.consolevision.types.List2D
import dev.patbeagan.consolevision.types.reduceColorSpaceBy

/**
 * A class that will return an Ansi image frame as a string.
 */
class FramePrinter(
    private val reductionRate: Int,
    private val paletteColors: ColorPalette? = null,
    private val filters: List<ImageFilter> = listOf(),
    private val colorConverter: ColorConverter = ColorConverter.NormalColorConverter(),
    private val compressionStyle: CompressionStyle = UPPER_HALF,
) {

    fun getFrame(frame: List2D<ColorInt>): String {
        filters.forEach { it.invoke(frame) }

        val out = StringBuilder()
        val chunked = (0 until frame.height).chunked(2)
        // since there will be 2 colors printed per row,
        // we actually have 2 y values per row.
        // this part decides which one will be
        // the foreground vs the background.
        chunked.forEach { y ->
            (0 until frame.width).forEach { x ->
                val p1 = y.getOrNull(1)
                val foreground = y.getOrNull(0) ?: p1
                val background = if (foreground != p1) p1 else null
                compressionStyle.symbol.toString().style(
                    colorForeground = ansiColor(frame, paletteColors, foreground, x),
                    colorBackground = ansiColor(frame, paletteColors, background, x),
                ).also { out.append(it) }
            }
            out.append("\n")
        }
        return out.toString()
    }

    private fun ansiColor(
        colorInts: List2D<ColorInt>,
        paletteColors: ColorPalette?,
        y: Int?,
        x: Int
    ): ConsoleVision.Color = colorConverter.convert(
        if (y == null) {
            // there are an odd number of lines
            // we'll need to fill with the default color
            null
        } else {
            colorInts
                .at(x, y)
                .reduceColorSpaceBy(reductionRate)
                .let { paletteColors?.matchColor(it) ?: it }
        }
    )

}
