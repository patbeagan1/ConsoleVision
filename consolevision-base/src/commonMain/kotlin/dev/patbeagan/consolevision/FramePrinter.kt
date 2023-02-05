package dev.patbeagan.consolevision

import dev.patbeagan.consolevision.imagefilter.ImageFilter
import dev.patbeagan.consolevision.style.ColorInt
import dev.patbeagan.consolevision.style.style
import dev.patbeagan.consolevision.types.CompressionStyle
import dev.patbeagan.consolevision.types.CompressionStyle.UPPER_HALF
import dev.patbeagan.consolevision.types.List2D

/**
 * A class that will return an Ansi image frame as a string.
 */
class FramePrinter(
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
                    colorForeground = colorConverter.convert(foreground?.let { frame.at(x, it) }),
                    colorBackground = colorConverter.convert(background?.let { frame.at(x, it) }),
                ).also { out.append(it) }
            }
            out.append("\n")
        }
        return out.toString()
    }
}
